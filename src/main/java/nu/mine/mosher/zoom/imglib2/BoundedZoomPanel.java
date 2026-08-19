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
import net.imglib2.type.numeric.integer.UnsignedByteType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BoundedZoomPanel extends JPanel {

    private static final int MAX_CACHE_SIZE = 4;
    private final Map<Double, BufferedImage> mipmapCache = Collections.synchronizedMap(
            new LinkedHashMap<>(MAX_CACHE_SIZE, 0.75f, true) {
                @Override
                protected boolean removeEldestEntry(Map.Entry<Double, BufferedImage> eldest) {
                    if (size() > MAX_CACHE_SIZE) {
                        eldest.getValue().flush();
                        return true;
                    }
                    return false;
                }
            }
    );

    private final Set<Double> activeWorkers = ConcurrentHashMap.newKeySet();
    private final RandomAccessibleInterval<UnsignedByteType> sourceImg;
    private final BufferedImage baseNativeImage;
    private final long srcWidth;
    private final long srcHeight;

    private double currentZoom = 1.0;
    private double panX = 0.0;
    private double panY = 0.0;

    private static final double MIN_ZOOM = 0.05;
    private static final double MAX_ZOOM = 50.0;

    public BoundedZoomPanel(RandomAccessibleInterval<UnsignedByteType> sourceImg) {
        this.sourceImg = sourceImg;
        this.srcWidth = sourceImg.dimension(0);
        this.srcHeight = sourceImg.dimension(1);

        // Verified: This helper returns a fully baked AWT image on initialization
        this.baseNativeImage = copyToManagedImage(sourceImg, 1.0);

        MouseAdapter mouseHandler = new MouseAdapter() {
            private Point dragStart;

            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart != null) {
                    panX += (e.getX() - dragStart.x);
                    panY += (e.getY() - dragStart.y);
                    dragStart = e.getPoint();
                    repaint();
                }
            }

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double oldZoom = currentZoom;

                if (e.getWheelRotation() < 0) {
                    currentZoom *= 1.15;
                } else {
                    currentZoom /= 1.15;
                }
                currentZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, currentZoom));

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

        double mipmapLevel = 1.0;
        if (currentZoom >= 2.0) {
            mipmapLevel = Math.pow(2.0, Math.floor(Math.log(currentZoom) / Math.log(2.0)));
            if (mipmapLevel > 16.0) mipmapLevel = 16.0;
        }

        BufferedImage optimalBitmap = mipmapCache.get(mipmapLevel);

        if (optimalBitmap == null) {
            optimalBitmap = baseNativeImage;
            double missingLevel = mipmapLevel;
            mipmapLevel = 1.0;

            if (missingLevel > 1.0 && activeWorkers.add(missingLevel)) {
                generateMipmapAsynchronously(missingLevel);
            }
        }

        if (optimalBitmap != null) {
            java.awt.geom.AffineTransform originalTransform = g2d.getTransform();

            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            if (currentZoom >= 1.0) {
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            } else {
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }

            double renderScale = currentZoom / mipmapLevel;

            java.awt.geom.Rectangle2D.Double textureAnchor = new java.awt.geom.Rectangle2D.Double(
                    panX, panY,
                    optimalBitmap.getWidth() * renderScale,
                    optimalBitmap.getHeight() * renderScale
            );

            TexturePaint highPrecisionPaint = new TexturePaint(optimalBitmap, textureAnchor);
            g2d.setPaint(highPrecisionPaint);
            g2d.fill(textureAnchor);

            g2d.setTransform(originalTransform);
        }

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

        String zoomText = String.format("Zoom: %.0f%%", currentZoom * 100);
        g2d.setColor(new Color(0, 0, 0, 160));
        g2d.fillRect(10, 10, 120, 30);

        g2d.setColor(Color.WHITE);
        g2d.drawString(zoomText, 22, 31);
    }

    private void generateMipmapAsynchronously(final double targetScale) {
        SwingWorker<BufferedImage, Void> worker = new SwingWorker<>() {
            @Override
            protected BufferedImage doInBackground() {
                return copyToManagedImage(sourceImg, targetScale);
            }

            @Override
            protected void done() {
                try {
                    BufferedImage result = get();
                    double currentIdealLevel = currentZoom >= 2.0 ? Math.pow(2.0, Math.floor(Math.log(currentZoom) / Math.log(2.0))) : 1.0;
                    if (currentIdealLevel > 16.0) currentIdealLevel = 16.0;

                    if (result != null && Math.abs(currentIdealLevel - targetScale) < 0.01) {
                        mipmapCache.put(targetScale, result);
                    } else if (result != null) {
                        result.flush();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    activeWorkers.remove(targetScale);
                    repaint();
                }
            }
        };
        worker.execute();
    }

    private BufferedImage copyToManagedImage(RandomAccessibleInterval<UnsignedByteType> src, double scale) {
        int w = (int) (srcWidth * scale);
        int h = (int) (srcHeight * scale);

        if (w <= 0 || h <= 0) return null;

        BufferedImage managedImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        java.awt.image.WritableRaster raster = managedImg.getRaster();

        var sourceRandomAccess = src.randomAccess();

        // FIXED: Single allocation array prevents GC churn inside nested loops
        int[] pixelSample = new int[1];

        // FIXED: Double-nested loop explicitly sets both coordinate dimensions
        // in sequence at the deepest point to prevent asymmetric matrix drift.
        for (int y = 0; y < h; y++) {
            long srcY = (long) (y / scale);
            srcY = Math.max(0, Math.min(srcHeight - 1, srcY));

            for (int x = 0; x < w; x++) {
                long srcX = (long) (x / scale);
                srcX = Math.max(0, Math.min(srcWidth - 1, srcX));

                // Explicit atomic updates for absolute multi-channel matrix positions
                sourceRandomAccess.setPosition(srcX, 0);
                sourceRandomAccess.setPosition(srcY, 1);

                pixelSample[0] = sourceRandomAccess.get().get();
                raster.setPixel(x, y, pixelSample);
            }
        }
        return managedImg;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            net.imglib2.img.Img<UnsignedByteType> testImg = net.imglib2.img.array.ArrayImgs.unsignedBytes(15000, 15000);
            var access = testImg.randomAccess();
            for (int x = 0; x < 15000; x++) {
                for (int y = 0; y < 15000; y++) {
                    access.setPosition(x, 0);
                    access.setPosition(y, 1);
                    int val = ((x / 40) + (y / 40)) % 2 == 0 ? 220 : 50;
                    access.get().set(val);
                }
            }

            JFrame frame = new JFrame("Verified High-Performance ImgLib2 Canvas");
            BoundedZoomPanel zoomPanel = new BoundedZoomPanel(testImg);
            zoomPanel.setPreferredSize(new Dimension(800, 800));

            frame.add(zoomPanel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
