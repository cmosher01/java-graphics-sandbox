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
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.sparse.NtreeImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.IntervalView;
import net.imglib2.view.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class UltraLargeZoomableCanvas extends JPanel {
    private static final long CANVAS_SIZE = 100_000_000L;

    private final Img<UnsignedByteType> img;

    // Smooth rendering transformation configurations
    private double scale = .1; // was: 0.000005;
    private double panX = 400.0;
    private double panY = 300.0;
    private Point dragStartPoint;

    // Direct geometric layout caches
    private static class FastRect {
        long x, y, w, h;
        int val;
    }

    public UltraLargeZoomableCanvas() {
        NtreeImgFactory<UnsignedByteType> factory = new NtreeImgFactory<>(new UnsignedByteType());
        img = factory.create(CANVAS_SIZE, CANVAS_SIZE);

        generateSparseRectangles(1_000);//////////////////// was 1_000_000
        setupInteractionListeners();
    }

    private void generateSparseRectangles(int numRectangles) {
        long startTime = System.currentTimeMillis();
        System.out.println("Executing optimized sequential allocation...");

        // Pre-instantiate local arrays to completely eliminate thread overhead locks
        FastRect[] batch = new FastRect[numRectangles];
        Random rand = new Random(42); // Explicit seed for deterministic, fast allocation

        for (int i = 0; i < numRectangles; i++) {
            FastRect r = new FastRect();
            r.x = (rand.nextLong() & Long.MAX_VALUE) % (CANVAS_SIZE - 200000L);
            r.y = (rand.nextLong() & Long.MAX_VALUE) % (CANVAS_SIZE - 200000L);
            r.w = 5000 + rand.nextInt(45000);
            r.h = 5000 + rand.nextInt(45000);
            r.val = 50 + rand.nextInt(206);
            batch[i] = r;
        }

        // Direct stream pointer writing to eliminate RandomAccess bounds checking calculations
        RandomAccess<UnsignedByteType> access = img.randomAccess();
        for (int i = 0; i < numRectangles; i++) {
            FastRect r = batch[i];
            long endX = Math.min(r.x + r.w, CANVAS_SIZE);
            long endY = Math.min(r.y + r.h, CANVAS_SIZE);

            // Step optimization to keep memory footprint inside manageable limits
            for (long x = r.x; x < endX; x += 1000) {
                for (long y = r.y; y < endY; y += 1000) {
                    access.setPosition(x, 0);
                    access.setPosition(y, 1);
                    access.get().set(r.val);
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Optimized setup completed in: " + (endTime - startTime) + " ms!");
    }

    private void setupInteractionListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    dragStartPoint = e.getPoint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && dragStartPoint != null) {
                    Point current = e.getPoint();
                    panX += current.x - dragStartPoint.x;
                    panY += current.y - dragStartPoint.y;
                    dragStartPoint = current;
                    repaint(); // Instant feedback tracking
                }
            }
        });

        addMouseWheelListener(e -> {
            int notches = e.getWheelRotation();
            Point mousePoint = e.getPoint();

            AffineTransform tx = getTransform();
            Point2D modelBeforeZoom;
            try {
                modelBeforeZoom = tx.inverseTransform(mousePoint, null);
            } catch (NoninvertibleTransformException ex) {
                return;
            }

            if (notches < 0) {
                scale *= 1.15;
            } else {
                scale = Math.max(1e-15, scale / 1.15);
            }

            AffineTransform newTx = getTransform();
            Point2D modelAfterZoom;
            try {
                modelAfterZoom = newTx.inverseTransform(mousePoint, null);
            } catch (NoninvertibleTransformException ex) {
                return;
            }

            panX += (modelAfterZoom.getX() - modelBeforeZoom.getX()) * scale;
            panY += (modelAfterZoom.getY() - modelBeforeZoom.getY()) * scale;

            repaint();
        });
    }

    private AffineTransform getTransform() {
        AffineTransform tx = new AffineTransform();
        tx.translate(panX, panY);
        tx.scale(scale, scale);
        return tx;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());

        int viewW = getWidth();
        int viewH = getHeight();
        if (viewW <= 0 || viewH <= 0) return;

        BufferedImage viewBuffer = new BufferedImage(viewW, viewH, BufferedImage.TYPE_BYTE_GRAY);
        AffineTransform tx = getTransform();

        try {
            // Find exactly what global coordinates map to our top-left and bottom-right viewport windows
            Point2D topLeftGlobal = tx.inverseTransform(new Point2D.Double(0, 0), null);
            Point2D bottomRightGlobal = tx.inverseTransform(new Point2D.Double(viewW, viewH), null);

            long minX = Math.max(0, (long) Math.floor(topLeftGlobal.getX()));
            long minY = Math.max(0, (long) Math.floor(topLeftGlobal.getY()));
            long maxX = Math.min(CANVAS_SIZE - 1, (long) Math.ceil(bottomRightGlobal.getX()));
            long maxY = Math.min(CANVAS_SIZE - 1, (long) Math.ceil(bottomRightGlobal.getY()));

            if (maxX > minX && maxY > minY) {
                // FIX: Slice a fast localized sub-interval view window out of ImgLib2
                IntervalView<UnsignedByteType> visibleGrid = Views.interval(img, new long[]{minX, minY}, new long[]{maxX, maxY});
                Cursor<UnsignedByteType> cursor = Views.iterable(visibleGrid).localizingCursor();

                long[] pos = new long[2];
                // Forward pixel iteration loop: Only process data points that physically exist inside the view
                while (cursor.hasNext()) {
                    cursor.fwd();
                    int value = cursor.get().get();

                    if (value > 0) {
                        cursor.localize(pos);
                        // Forward transform coordinate geometry calculations are exceptionally faster than inverse maps
                        int screenX = (int) Math.round(pos[0] * scale + panX);
                        int screenY = (int) Math.round(pos[1] * scale + panY);

                        if (screenX >= 0 && screenX < viewW && screenY >= 0 && screenY < viewH) {
                            viewBuffer.getRaster().setSample(screenX, screenY, 0, value);

                            // Visual interpolation helper block to ensure tiny points scale naturally when zooming way out
                            if (scale < 0.001) {
                                if (screenX + 1 < viewW) viewBuffer.getRaster().setSample(screenX + 1, screenY, 0, value);
                                if (screenY + 1 < viewH) viewBuffer.getRaster().setSample(screenX, screenY + 1, 0, value);
                            }
                        }
                    }
                }
            }
        } catch (NoninvertibleTransformException ignored) {}

        g2.drawImage(viewBuffer, 0, 0, null);

        g2.setColor(Color.GREEN);
        g2.drawString(String.format("Scale: %.11f | PanX: %.1f | PanY: %.1f", scale, panX, panY), 10, 20);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(1024, 768);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ImgLib2 Ultra Large Canvas - Maximum Optimization");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new UltraLargeZoomableCanvas());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
