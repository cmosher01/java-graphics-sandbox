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

import lombok.*;
import org.checkerframework.checker.units.qual.A;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.List;

public class InfiniteSpeedCanvas extends JFrame {
    public InfiniteSpeedCanvas() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        val dimViewport = new Dimension(1024, 768);

        val dataset = new ArrayList<MyRect>(1000000);
        val r = new Random();
        for (int i = 0; i < 1_000_000; i++) {
            dataset.add(new MyRect(r.nextInt(100_000_000), r.nextInt(100_000_000), 170, 14));
        }

        val canvasPanel = new UnifiedCanvasPanel(dataset);
        canvasPanel.setPreferredSize(dimViewport);
        setContentPane(canvasPanel);

        pack();
        setLocationRelativeTo(null);
    }





    //============================================================================================================



    private static class MyRect extends Rectangle {
        String text;
        public MyRect(int x, int y, int w, int h) {
            super(x, y, w, h);
            this.text = calc(x, y);
        }

        static String calc(int x, int y) {
            return String.format("(%09d,%09d)", x, y);
        }

        @Override
        public boolean equals(Object obj) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int hashCode() {
            throw new UnsupportedOperationException();
        }

        public void drag(int x, int y) {
            this.x = x;
            this.y = y;
            this.text = calc(x,y);
        }
    }






    private static class UnifiedCanvasPanel extends JPanel {
        private static final double MIN_ZOOM_OUT = 1e-6; // 2-finger drag touchpad towards user, wheelRotation > 0
        private static final double MAX_ZOOM_IN = 1e+2; // " away from user, wheelRotation < 0
        private static final double ZOOM_SPEED = 0.05;
        private static final boolean HILITE = true;



        private final List<MyRect> shapes;

        private final Set<BufferedImage> screenCacheImg = Collections.newSetFromMap(new IdentityHashMap<>());

        // Selection / Drag State
        private final Set<MyRect> dragging = Collections.newSetFromMap(new IdentityHashMap<>());

        // Camera Pan & Zoom State
        private double panX = 50_000_000;
        private double panY = 50_000_000;
        private double zoomFactor = 1D;

//        private final int x;
//        private final int y;
//        private final int w;
//        private final int h;


        private Font f = new Font("Courier New", Font.PLAIN, 14);



        public UnifiedCanvasPanel(final List<MyRect> shapes) {
            this.shapes = shapes;
            setOpaque(true);
            setBackground(Color.DARK_GRAY);

//            {
//                // find bounds of entire image
//                int minx = Integer.MAX_VALUE;
//                int maxx = Integer.MIN_VALUE;
//                int miny = Integer.MAX_VALUE;
//                int maxy = Integer.MIN_VALUE;
//                for (val r : this.shapes) {
//                    if (r.x < minx) {
//                        minx = r.x;
//                    }
//                    if (maxx < r.x + r.width) {
//                        maxx = r.x + r.width;
//                    }
//                    if (r.y < miny) {
//                        miny = r.y;
//                    }
//                    if (maxy < r.y + r.height) {
//                        maxy = r.y + r.height;
//                    }
//                }
//                x = minx;
//                y = miny;
//                w = maxx - minx;
//                h = maxy - miny;
//            }

            val dragPanZoomAdapter = new MouseAdapter() {
                private int offsetX;
                private int offsetY;
                private int prevX;
                private int prevY;

                @Override
                public void mousePressed(final MouseEvent e) {
                    val currX = e.getX();
                    val currY = e.getY();
                    this.prevX = currX;
                    this.prevY = currY;
                    dragging.clear();

                    // Convert screen pixel space back into Infinite World Space
                    val worldMouseX = (int) ((currX - panX) / zoomFactor);
                    val worldMouseY = (int) ((currY - panY) / zoomFactor);

                    // iterate backwards to find top-most shape (last drawn)
                    for (val r : shapes.reversed()) {
                        if (worldMouseX >= r.x && worldMouseX <= (r.x + r.width) && worldMouseY >= r.y && worldMouseY <= (r.y + r.height)) {
                            dragging.add(r);
                            offsetX = worldMouseX - r.x;
                            offsetY = worldMouseY - r.y;
                            break;
                        }
                    }

                    // see if click is on a shape (to drag it), or on the background (to pan)
                    if (!dragging.isEmpty()) {
                        // rebuild cache to remove shape that's now being dragged
                        // because we draw it independently in paintComponent
                        screenCacheImg.clear();
                    }
                    repaint();
                }

                @Override
                public void mouseDragged(final MouseEvent e) {
                    if (!dragging.isEmpty()) {
                        for (val ds : dragging) {
                            int x = (int) ((e.getX() - panX) / zoomFactor) - offsetX;
                            int y = (int) ((e.getY() - panY) / zoomFactor) - offsetY;
                            ds.drag(x, y);
                        }
//                        System.out.printf("dragged to: (%d,%d)\n", draggedShape.x, draggedShape.y);
                    } else {
                        panX += (e.getX() - prevX);
                        panY += (e.getY() - prevY);
//                        pan = new Xlation(new MoveTerm(fp(pan.x().d().n() + e.getX() - prevX)), new MoveTerm(fp(pan.y().d().n() + e.getY() - prevY)));
                        prevX = e.getX();
                        prevY = e.getY();
                        screenCacheImg.clear(); // TODO can we redraw only the part(s) becoming visible?
                    }
                    repaint();
                }

                @Override
                public void mouseReleased(final MouseEvent e) {
                    if (!dragging.isEmpty()) {
                        dragging.clear();
                        screenCacheImg.clear();
                    }
                    repaint();
                }

                @Override
                public void mouseWheelMoved(final MouseWheelEvent e) {
                    val oldZoom = zoomFactor;
                    zoomFactor = Math.clamp(zoomFactor / Math.exp(e.getWheelRotation() * ZOOM_SPEED), MIN_ZOOM_OUT, MAX_ZOOM_IN);

                    val fwdZoom = zoomFactor/oldZoom;
                    System.out.printf("SCALE FACTOR: %09.7f\n", zoomFactor);
                    val invZoom = 1 - fwdZoom;

                    panX = panX*fwdZoom + e.getX()*invZoom;
                    panY = panY*fwdZoom + e.getY()*invZoom;

                    screenCacheImg.clear();
                    repaint();
                }
            };

            addMouseListener(dragPanZoomAdapter);
            addMouseMotionListener(dragPanZoomAdapter);
            addMouseWheelListener(dragPanZoomAdapter);

            addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    screenCacheImg.clear();
                }
            });
        }




        private void rebuildScreenCache(final GraphicsConfiguration gc) {
            if (!screenCacheImg.isEmpty()) {
                return;
            }

            val clip = new MyRect((int)(-panX/zoomFactor), (int)(-panY/zoomFactor), (int)(getWidth()/zoomFactor), (int)(getHeight()/zoomFactor));
//            System.err.printf("%s: REBUILDING VIEWPORT IMAGE CACHE: x:[%d,%d], y[:%d,%d]\n", Instant.now(), clip.x, clip.x+clip.width, clip.y, clip.y+clip.height);

            val img = gc.createCompatibleImage(getWidth(), getHeight(), Transparency.TRANSLUCENT);

            val g = img.createGraphics();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);


            g.translate(panX, panY);

            g.scale(zoomFactor, zoomFactor);

            val tr = g.getTransform();
            g.setTransform(new AffineTransform());
            float fs = 12F * (float)zoomFactor;
            val sf = f.deriveFont(fs);

            for (val r : shapes) {
                // draw visible shapes (but not the ones being dragged)
                if (r.intersects(clip) && !dragging.contains(r)) {
                    drawShape(g, r.x, r.y, r.width, r.height, !HILITE, r.text);
                }
            }

            g.dispose();

            screenCacheImg.add(img);
        }




        @Override
        protected void paintComponent(final Graphics gr) {
            super.paintComponent(gr);
            val g = (Graphics2D) gr;

            rebuildScreenCache(g.getDeviceConfiguration());
            g.drawImage(screenCacheImg.stream().findFirst().get(), 0, 0, null);

            // Draw the shapes being dragged
            g.translate(panX, panY);
            g.scale(zoomFactor, zoomFactor);
            g.setFont(f);
            for (val r : dragging) {
                drawShape(g, r.x, r.y, r.width, r.height, HILITE, r.text);
            }
        }

        private void drawShape(final Graphics2D g, final int x, final int y, final int w, final int h, final boolean hilite, final String s) {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, w, h);
            g.setColor(hilite ? Color.MAGENTA : Color.ORANGE);
            g.drawRect(x, y, w, h);
            if (8 < zoomFactor*h) {
                g.setColor(Color.GREEN);
                g.drawString(s, x, y+10);
            }
        }
    }







//============================================================================================================




    public static void main(final String... args) throws InterruptedException, InvocationTargetException {
        // Enable Hardware Acceleration Flags for Swing pipelines
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("sun.java2d.metal", "false");

        SwingUtilities.invokeAndWait(() -> {
            val frame = new InfiniteSpeedCanvas();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1024, 768);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
