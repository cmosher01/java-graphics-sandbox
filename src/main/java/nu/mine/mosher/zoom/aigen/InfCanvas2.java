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

package nu.mine.mosher.zoom.aigen;

import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.strtree.STRtree;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class InfCanvas2 extends JPanel {
    // Spatial Index holding all our diagram objects
    private final STRtree spatialIndex = new STRtree();

    // Transform tracking current pan and zoom
    private final AffineTransform transform = new AffineTransform();

    // Mouse tracking for panning
    private Point dragStart;

    public InfCanvas2() {
        setBackground(Color.WHITE);
        initMouseListeners();
        generateDummySparseData();
        spatialIndex.build(); // Optimizes the R-Tree structure
    }

    // Wrap your diagram shapes in a custom class for the index
    public static class DiagramElement {
        public Shape shape;
        public String text;
        public Envelope bounds; // JTS bounding box

        public DiagramElement(Shape shape, String text, double x, double y, double w, double h) {
            this.shape = shape;
            this.text = text;
            this.bounds = new Envelope(x, x + w, y, y + h);
        }
    }

    private void generateDummySparseData() {
        // Generate sparse items across a 100,000,000 square space
        double maxCoord = 100_000_000.0;
        for (int i = 0; i < 50_000; i++) {
            double x = Math.random() * maxCoord;
            double y = Math.random() * maxCoord;

            // Random rectangles and lines
            Rectangle2D.Double rect = new Rectangle2D.Double(x, y, 300, 50);
            DiagramElement elem = new DiagramElement(rect, String.format("node #%05d: (%011.1f,%011.1f)", i, x, y), x, y, 300, 50);

            spatialIndex.insert(elem.bounds, elem);
        }
    }




    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable basic anti-aliasing hints
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fetch the visible area in Virtual World Coordinates
        Envelope viewEnvelope = getVisibleWorldBounds();

        // Fetch only visible elements from the Spatial Index
        List<DiagramElement> visibleElements = spatialIndex.query(viewEnvelope);

        // Extract exact zoom scale and top-left virtual coordinates
        double scale = transform.getScaleX();
        Point2D worldTopLeft;
        try {
            worldTopLeft = transform.createInverse().transform(new Point2D.Double(0, 0), null);
        } catch (NoninvertibleTransformException e) {
            return;
        }

        // scale the font itself
        float baseFontSize = 12.0f;
        float scaledFontSize = (float) (baseFontSize * scale);

        boolean shouldRenderText = 2.0f <= scaledFontSize;

        if (shouldRenderText) {
            // Derive a new font matching the pixel size required by the zoom level
            Font baseFont = new Font("Monospaced", Font.PLAIN, 12); // Template font
            Font scaledFont = baseFont.deriveFont(scaledFontSize);
            g2d.setFont(scaledFont);
        }



        // Draw elements MANUALLY scaled to screen space
        g2d.setColor(Color.BLUE);
        g2d.setStroke(new BasicStroke(3.0F));

        for (DiagramElement elem : visibleElements) {
            Rectangle2D b = elem.shape.getBounds2D();

            // Convert world coordinates directly into exact screen pixels
            double screenX = (b.getX() - worldTopLeft.getX()) * scale;
            double screenY = (b.getY() - worldTopLeft.getY()) * scale;
            double screenW = b.getWidth() * scale;
            double screenH = b.getHeight() * scale;

            // Create a temporary rectangle mapped 1:1 to monitor pixels
            Rectangle2D.Double screenRect = new Rectangle2D.Double(screenX, screenY, screenW, screenH);
            g2d.draw(screenRect);


            // Render text tracking the rectangle position smoothly
            if (elem.text != null && shouldRenderText/*isZoomLevelAppropriateForText()*/) {
                //g2d.setFont(new Font("Monospaced", Font.PLAIN, 10));
                g2d.drawString(elem.text, (float)(screenX + 5), (float)(screenY + scaledFontSize));
            }
        }
    }





    /**
     * Converts the current screen pixel dimensions back into the
     * 100M x 100M virtual world coordinates to see what's actually visible.
     */
    private Envelope getVisibleWorldBounds() {
        try {
            AffineTransform inverse = transform.createInverse();
            Point2D topLeft = inverse.transform(new Point2D.Double(0, 0), null);
            Point2D bottomRight = inverse.transform(new Point2D.Double(getWidth(), getHeight()), null);
            return new Envelope(topLeft.getX(), bottomRight.getX(), topLeft.getY(), bottomRight.getY());
        } catch (NoninvertibleTransformException e) {
            return new Envelope(0, 100_000_000, 0, 100_000_000);
        }
    }

    private boolean isZoomLevelAppropriateForText() {
        // Optimization: Don't render text if zoomed out too far (prevents lag)
        return transform.getScaleX() > 0.01;
    }

    private static final double MIN_ZOOM_OUT = 1e-8; // 2-finger drag touchpad towards user, wheelRotation > 0
    private static final double MAX_ZOOM_IN = 1e+2; // " away from user, wheelRotation < 0
    final AtomicReference<Double> zoomFactor = new AtomicReference<>(1D);

    private void initMouseListeners() {
        // Panning Logic
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragStart = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragStart == null) return;
                Point dragEnd = e.getPoint();

                double dx = dragEnd.x - dragStart.x;
                double dy = dragEnd.y - dragStart.y;

                // Shift transform by pixel delta, accounting for current zoom scale
                transform.translate(dx / transform.getScaleX(), dy / transform.getScaleY());
                dragStart = dragEnd;
                repaint();
            }
        });


        // Zooming Logic (Zoom to Mouse Position)
        addMouseWheelListener(e -> {
//            System.out.println("rot: "+e.getWheelRotation());
            if (e.getWheelRotation() == 0) {
                return;
            }
//            zoomFactor = (e.getWheelRotation() < 0) ? 1.1 : 1 / 1.1;
            double newz = zoomFactor.get();
            final double oldz = newz;

            newz /= Math.exp(e.getWheelRotation() * 1.0e-2);
            newz = Math.clamp(newz, MIN_ZOOM_OUT, MAX_ZOOM_IN);

            zoomFactor.set(newz);
            newz /= oldz;

            Point mousePt = e.getPoint();

            // Transform mouse point to virtual coordinates
            Point2D worldPt;
            try {
                worldPt = transform.createInverse().transform(mousePt, null);
            } catch (NoninvertibleTransformException ex) { return; }

            // Scale around the mouse pointer
            transform.translate(worldPt.getX(), worldPt.getY());
            transform.scale(newz, newz);
            transform.translate(-worldPt.getX(), -worldPt.getY());

            repaint();
        });
    }

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> {
            JFrame frame = new JFrame("Infinite 100M x 100M Canvas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1024, 768);
            frame.add(new InfCanvas2());
            frame.setVisible(true);
        });
    }
}
