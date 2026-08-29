/*
 *     Copyright 2026, Christopher Alan Mosher, New York, New York, USA, <cmosher01@gmail.com>.
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

package nu.mine.mosher.zoom.swinglayer.example;

import lombok.val;
import nu.mine.mosher.zoom.swinglayer.*;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

import static java.awt.BasicStroke.*;
import static nu.mine.mosher.zoom.swinglayer.Solarized.*;

final class MyPanel extends JPanel {
    private static final LayoutManager NO_LAYOUT_MANAGER = null;
    private static final Point2D.Double ORIGIN = new Point2D.Double();

    private static final boolean BACKGROUND_FILL = true;
    private static final Color COLOR_BACKGROUND = BASE_01;

    private static final boolean AXES = true;
    private static final Color COLOR_AXES = BASE_1;

    private static final boolean BOUNDS_FILL = true;
    private static final Color COLOR_CANVAS_BG = BASE_3;

    private static final boolean BOUNDS_DRAW = false;
    private static final Stroke LINE_BOUNDS = new BasicStroke(2F, CAP_BUTT, JOIN_BEVEL);

    private static final boolean CLIP_DRAW = true;
    private static final double CLIP_DRAW_INSET = 10.0D;
    private static final Color COLOR_CLIP = YELLOW;


    private final StatusBar sb;
    private final ZoomPan zp;
    private final Status status;

    private final RedSquare sq = new RedSquare();
    private final LineOfText tx = new LineOfText("This is my custom Panel!", new Point2D.Double(200D,200D));



    public MyPanel(final ZoomPan zp, StatusBar sb) {
        super(NO_LAYOUT_MANAGER);
        this.sb = sb;
        this.zp = zp;
        this.status = new Status(zp, this);

        setOpaque(false);

        resetZoomLimits();

        new Timer(200, e -> sb.setText(this.status.set())).start();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                sb.setText(MyPanel.this.status.set());
            }
        });

        val mouser = new MouseAdapter() {
            private Point2D draggedFrom;

            private boolean mine(final MouseEvent e) {
                return Objects.nonNull(e) && e.getSource() == MyPanel.this && !e.isConsumed();
            }

            @Override
            public void mouseMoved(final MouseEvent e) {
                if (mine(e)) {
                    sb.setText(MyPanel.this.status.set(e.getPoint()));
                }
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                val pView = e.getPoint();
                val p = zp.viewportToCanvas(new Point2D.Double(pView.x, pView.y));
                if (mine(e) && sq.contains(p)) {
                    draggedFrom = p;
                    repaint();
                    e.consume();
                }
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                if (mine(e) && Objects.nonNull(draggedFrom)) {
                    final Point2D p = zp.viewportToCanvas(new Point2D.Double(e.getX(), e.getY()));
                    sq.setRect(sq.x+p.getX()-draggedFrom.getX(), sq.y+p.getY()-draggedFrom.getY(), sq.width, sq.height);
                    draggedFrom = p;
                    resetZoomLimits();
                    repaint();
                    e.consume();
                }
                sb.setText(MyPanel.this.status.set(e.getPoint()));
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (mine(e) && Objects.nonNull(draggedFrom)) {
                    draggedFrom = null;
                    resetZoomLimits();
                    repaint();
                    e.consume();
                }
                val p = zp.viewportToCanvas(new Point2D.Double(e.getX(), e.getY()));
                val clip = clip();
                if (clip.contains(p)) {
                    sb.setText(MyPanel.this.status.set(e.getPoint()));
                } else {
                    sb.setText(MyPanel.this.status.set());
                }
            }

            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                if (mine(e)) {
                    sb.setText(MyPanel.this.status.set(e.getPoint()));
                }
            }

            @Override
            public void mouseEntered(final MouseEvent e) {
                if (mine(e)) {
                    sb.setText(MyPanel.this.status.set(e.getPoint()));
                }
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                sb.setText(MyPanel.this.status.set());
            }
        };

        addMouseListener(mouser);
        addMouseMotionListener(mouser);
        addMouseWheelListener(mouser);
    }



    private void resetZoomLimits() {
        val b = this.sq.bounds();
        Rectangle2D.union(this.tx.bounds(), b, b);
        this.zp.setCanvasBounds(b);
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        val g2 = (Graphics2D)g;

        val clip = clip();
        g2.setClip(clip);

        if (BACKGROUND_FILL) {
            g2.setColor(COLOR_BACKGROUND);
            g2.fill(clip);
        }

        if (BOUNDS_FILL) {
            g2.setColor(COLOR_CANVAS_BG);
            g2.fill(this.zp.bounds());
        }

        if (BOUNDS_DRAW) {
            g2.setStroke(LINE_BOUNDS);
            g2.setColor(Color.BLACK);
            g2.draw(this.zp.bounds());
        }

        if (AXES) {
            g2.setColor(COLOR_AXES);
            axesClipped(g2);
//            axesSimple(g2);
        }

        // to show (an inset) clipping region
        if (CLIP_DRAW) {
            g2.setColor(COLOR_CLIP);
            g2.draw(new Rectangle2D.Double(clip.x+CLIP_DRAW_INSET, clip.y+CLIP_DRAW_INSET, clip.width-2*CLIP_DRAW_INSET, clip.height-2*CLIP_DRAW_INSET));
        }



        if (this.tx.bounds().intersects(clip)) {
            this.tx.paint(g2);
        }

        if (this.sq.bounds().intersects(clip)) {
            this.sq.paint(g2);
        }
    }

    private void axesClipped(final Graphics2D g) {
        // calculate clipping manually works:
        final double zer = 0D;
        final double wid = getWidth();
        final double hgt = getHeight();

        // L = left, R = right, T = top, B = bottom
        val c_L = zp.viewportToCanvas(new Point2D.Double(zer, zp.canvasToViewport(ORIGIN).y));
        val c_R = zp.viewportToCanvas(new Point2D.Double(wid, zp.canvasToViewport(ORIGIN).y));
        val c_T = zp.viewportToCanvas(new Point2D.Double(zp.canvasToViewport(ORIGIN).x, zer));
        val c_B = zp.viewportToCanvas(new Point2D.Double(zp.canvasToViewport(ORIGIN).x, hgt));

        // but the dashed strokes need to be optimized!
//        g.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, new float[]{2.0f, 6.0f}, (float) (clip.x % 8 + 8) % 8 + 1));
        g.draw(new Line2D.Double(c_L.x, c_L.y, c_R.x, c_R.y));
//        g.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 1.0f, new float[]{2.0f, 6.0f}, (float) (clip.y % 8 + 8) % 8 + 1));
        g.draw(new Line2D.Double(c_T.x, c_T.y, c_B.x, c_B.y));
    }

    private void axesSimple(final Graphics2D g) {
        // with proper clipping (using doubles everywhere and no ints), this works but a little glitchy at high zoom:
        g.draw(new Line2D.Double(-1e8, 0, 1e8, 0));
        g.draw(new Line2D.Double(0, -1e8, 0, 1e8));
    }

    /**
     * Calculates the clipping rectangle in viewport coordinates
     * @return
     */
    public Rectangle2D.Double viewportClip() {
        val ipt = getLocation();
        val idim = getSize();
        return new Rectangle2D.Double(ipt.x, ipt.y, idim.width, idim.height);
    }

    /**
     * Calculates the clipping rectangle in canvas coordinates
     * @return clipping rect
     */
    public Rectangle2D.Double clip() {
        return this.zp.viewportToCanvas(viewportClip());
    }
}
