/*
 *     Copyright © 2026, Christopher Alan Mosher, New York, New York, USA, <cmosher01@gmail.com>.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nu.mine.mosher.zoom.imglib2;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.integer.UnsignedByteType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;

public class MouseZoomPanel extends JPanel {

    // --- 1. MEMORY-MANAGED BOUNDED MIPMAP CACHE ---
    private static final int MAX_CACHE_SIZE = 4;
    private final LinkedHashMap<Double, BufferedImage> mipmapCache =
            new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Double, BufferedImage> eldest) {
                    if (size() > MAX_CACHE_SIZE) {
                        // FIX: Explicitly flush graphics memory resources to prevent OS leaks
                        eldest.getValue().flush();
                        return true;
                    }
                    return false;
                }
            };

    private final RandomAccessibleInterval<UnsignedByteType> sourceImg;
    private final BufferedImage baseNativeImage;
    private final long srcWidth;
    private final long srcHeight;

    // --- 2. ROBUST COORDINATE SYSTEM ---
    private double currentZoom = 1.0;
    private double panX = 0.0;
    private double panY = 0.0;

    private static final double MIN_ZOOM = 1e-2;
    private static final double MAX_ZOOM = 1e+2;

    public MouseZoomPanel(RandomAccessibleInterval<UnsignedByteType> sourceImg) {
        this.sourceImg = sourceImg;
        this.srcWidth = sourceImg.dimension(0);
        this.srcHeight = sourceImg.dimension(1);

        // Render base map layer once
        this.baseNativeImage = ImageJFunctions.wrap(sourceImg, "Base").getBufferedImage();

        // --- 3. CONSOLIDATED MOUSE DRAG & ZOOM CONTROLLER ---
        MouseAdapter mouseHandler = new MouseAdapter() {
            private Point dragStart;

            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    // Update persistent translation metrics directly
                    panX += (e.getX() - dragStart.x);
                    panY += (e.getY() - dragStart.y);
                    dragStart = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                final double oldZoom = currentZoom;


//                // Multiplicative zoom for perceptual consistency
//                if (e.getWheelRotation() < 0) {
//                    currentZoom *= 1.15;
//                } else {
//                    currentZoom /= 1.15;
//                }
                currentZoom /= Math.exp(e.getWheelRotation() * 1.0e-2);

                currentZoom = Math.clamp(currentZoom, MIN_ZOOM, MAX_ZOOM);

                double mouseX = e.getX();
                double mouseY = e.getY();
                panX = mouseX - (mouseX - panX) * (currentZoom / oldZoom);
                panY = mouseY - (mouseY - panY) * (currentZoom / oldZoom);

                repaint();
            }
        };

        this.addMouseListener(mouseHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseWheelListener(mouseHandler);
        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;



        // Discrete mapping key generation prevents fraction distortion errors
        final double mipmapLevel;
        final BufferedImage optimalBitmap;
        if (currentZoom >= 8.0) {
            mipmapLevel = 8.0;
            optimalBitmap = mipmapCache.computeIfAbsent(mipmapLevel, this::renderImgLib2Mipmap);
        } else if (currentZoom >= 4.0) {
            mipmapLevel = 4.0;
            optimalBitmap = mipmapCache.computeIfAbsent(mipmapLevel, this::renderImgLib2Mipmap);
        } else if (currentZoom >= 2.0) {
            mipmapLevel = 2.0;
            optimalBitmap = mipmapCache.computeIfAbsent(mipmapLevel, this::renderImgLib2Mipmap);
        } else {
            mipmapLevel = 1.0;
            optimalBitmap = baseNativeImage;
        }

        if (optimalBitmap != null) {
            var originalTransform = g2d.getTransform();

            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            if (currentZoom >= 1.0) {
                // When zooming in close, use Nearest Neighbor to show razor-sharp original pixel boundaries
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            } else {
                // When zooming out far, use Bilinear to avoid rough aliasing artifacts
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }


            double renderScale = currentZoom / mipmapLevel;


            var textureAnchor = new Rectangle2D.Double(panX, panY, optimalBitmap.getWidth() * renderScale, optimalBitmap.getHeight() * renderScale);

            // Map the image natively inside our double-precision coordinate bounding space
            var highPrecisionPaint = new TexturePaint(optimalBitmap, textureAnchor);

            // Feed the paint to the graphics pipe and fill the screen canvas area
            g2d.setPaint(highPrecisionPaint);
            g2d.fill(textureAnchor);

            g2d.setTransform(originalTransform);
        }



        // --- HEADS-UP DISPLAY
//        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
        String zoomText = String.format(" zoom: %07.3fx  pan: (%07.1f,%07.1f)", currentZoom, panX/currentZoom, panY/currentZoom);

        // Draw the background panel first
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(10, 10, 300, 35); // Top-left is at (10, 10)

        // Draw the text overlay second, shifted down to (22, 31) to sit safely inside the box
        g2d.setColor(Color.WHITE);
        g2d.drawString(zoomText, 22, 31);
        // ----------------------------------------
    }







    // --- 4. PREDICTABLE STRIDED COGNITIVE SAMPLER ---
    private BufferedImage renderImgLib2Mipmap(double targetScale) {
        long newWidth = (long) (srcWidth * targetScale);
        long newHeight = (long) (srcHeight * targetScale);

        if (newWidth <= 0 || newHeight <= 0) return null;

//        Img<UnsignedByteType> scaledImg = ArrayImgs.unsignedBytes(newWidth, newHeight);
        // Use a CellImg with 256x256 pixel blocks instead of an unbroken ArrayImg
        CellImgFactory<UnsignedByteType> factory = new CellImgFactory<>(new UnsignedByteType(), 256, 256);
        Img<UnsignedByteType> scaledImg = factory.create(newWidth, newHeight);

        var cursor = scaledImg.localizingCursor();
        var sourceRandomAccess = sourceImg.randomAccess();

        while (cursor.hasNext()) {
            cursor.fwd();

            long srcX = (long) (cursor.getLongPosition(0) / targetScale);
            long srcY = (long) (cursor.getLongPosition(1) / targetScale);

            srcX = Math.max(0, Math.min(srcWidth - 1, srcX));
            srcY = Math.max(0, Math.min(srcHeight - 1, srcY));

            sourceRandomAccess.setPosition(srcX, 0);
            sourceRandomAccess.setPosition(srcY, 1);

            cursor.get().set(sourceRandomAccess.get().get());
        }

        return ImageJFunctions.wrap(scaledImg, "SparseMipmap").getBufferedImage();
    }

    private static final long XDIM = 15000;
    private static final long YDIM = 15000;
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Img<UnsignedByteType> testImg = ArrayImgs.unsignedBytes(XDIM, YDIM);
            var access = testImg.randomAccess();
            for (int x = 0; x < XDIM; x++) {
                for (int y = 0; y < YDIM; y++) {
                    access.setPosition(x, 0);
                    access.setPosition(y, 1);
                    int val = ((x / 40) + (y / 40)) % 2 == 0 ? 235 : 40;
                    access.get().set(val);
                }
            }

            JFrame frame = new JFrame("Production-Grade ImgLib2 Canvas");
            MouseZoomPanel zoomPanel = new MouseZoomPanel(testImg);
            zoomPanel.setPreferredSize(new Dimension(800, 800));

            frame.add(zoomPanel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
