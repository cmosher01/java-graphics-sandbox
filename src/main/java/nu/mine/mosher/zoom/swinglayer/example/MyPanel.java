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
import java.awt.*;
import java.awt.geom.*;

import static nu.mine.mosher.zoom.swinglayer.Solarized.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class MyPanel extends JPanel {
    private static final LayoutManager NO_LAYOUT_MANAGER = null;
    private static final Point2D.Double ORIGIN = new Point2D.Double();

    private static final boolean BACKGROUND_FILL = true;
    private static final Color BACKGROUND_COLOR = BASE_3;

    private static final boolean AXES = true;
    private static final Color AXES_COLOR = BASE_1;
    private static final Stroke AXES_STROKE = new BasicStroke(1.0F);

    private static final boolean CLIP_DRAW = false;
    private static final double CLIP_DRAW_INSET = 10.0D;
    private static final Color CLIP_COLOR = YELLOW;


//    private final StatusBar sb;
    private final ZoomPan zp;
//    private final Status status;



//    private Optional<Graphic> graphic = Optional.of(new DropLineChart());
//    private Optional<Graphic> graphic = Optional.empty();
    private final RedSquaresModel model;
    private final DragSelectionModel modelDragSelection;


    public MyPanel(RedSquaresModel model, final ZoomPan zp,/*, StatusBar sb*/DragSelectionModel modelDragSelection) {
        super(NO_LAYOUT_MANAGER);
        this.model = model;
        this.modelDragSelection = modelDragSelection;
//        this.sb = sb;
        this.zp = zp;
//        this.status = new Status(zp, this);



        setOpaque(false);

//        this.graphic.ifPresent(Graphic::updateBounds);
//        resetZoomLimits();



//        new Timer(200, e -> sb.setText(this.status.set())).start();

//        addComponentListener(new ComponentAdapter() {
//            @Override
//            public void componentResized(ComponentEvent e) {
//                sb.setText(MyPanel.this.status.set());
//            }
//        });

//        val mouser = new MouseAdapter() {
//            private Optional<Point2D> draggedFrom = Optional.empty();
//
//            @Override
//            public void mouseMoved(final MouseEvent e) {
//                if (mine(e)) {
//                    sb.setText(MyPanel.this.status.set(pointOf(e)));
//                }
//            }
//
//            @Override
//            public void mousePressed(final MouseEvent e) {
//                val p = zp.viewportToCanvas(pointOf(e));
//                if (mine(e) && graphic.isPresent() && graphic.get().press(p, e)) {
//                    draggedFrom = Optional.of(p);
//                    repaint();
//                    e.consume();
//                }
//            }
//
//            @Override
//            public void mouseDragged(final MouseEvent e) {
//                if (mine(e) && draggedFrom.isPresent()) {
//                    final Point2D p = zp.viewportToCanvas(pointOf(e));
//                    graphic.get().drag(Swings.delta(draggedFrom.get(), p), e);
//                    draggedFrom = Optional.of(p);
//                    resetZoomLimits();
//                    repaint();
//                    e.consume();
//                }
////                sb.setText(MyPanel.this.status.set(e.getPoint()));
//            }
//
//            @Override
//            public void mouseReleased(final MouseEvent e) {
//                if (mine(e) && draggedFrom.isPresent()) {
//                    draggedFrom = Optional.empty();
//                    repaint();
//                    e.consume();
//                }
//            }
//
//            @Override
//            public void mouseWheelMoved(final MouseWheelEvent e) {
//                if (mine(e)) {
//                    sb.setText(MyPanel.this.status.set(pointOf(e)));
//                }
//            }
//
//
//
//            private boolean mine(final MouseEvent e) {
//                return Objects.nonNull(e) && e.getSource() == MyPanel.this && !e.isConsumed();
//            }
//        };

//        addMouseListener(mouser);
//        addMouseMotionListener(mouser);
//        addMouseWheelListener(mouser);
    }



//    private void resetZoomLimits() {
//        this.graphic.ifPresent(gr -> this.zp.setZoomOutMinFromBounds(gr.bounds()));
//        this.zp.setZoomOutMinFromBounds(this.model.bounds());
//    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        val g2 = (Graphics2D)g;

        this.zp.paint(g2);

        val clip = clip();
        g2.setClip(clip);

        if (BACKGROUND_FILL) {
            g2.setColor(BACKGROUND_COLOR);
            g2.fill(clip);
        }

        // TODO
//        this.graphic.ifPresent(gr -> gr.paintBackground(g2, clip));
        this.model.paintBackground(g2, clip);

        if (AXES) {
            g2.setStroke(AXES_STROKE);
            g2.setColor(AXES_COLOR);
            axesClipped(g2);
//            axesSimple(g2);
        }

        // to show (an inset) clipping region
        if (CLIP_DRAW) {
            g2.setColor(CLIP_COLOR);
            g2.draw(new Rectangle2D.Double(clip.x+CLIP_DRAW_INSET, clip.y+CLIP_DRAW_INSET, clip.width-2*CLIP_DRAW_INSET, clip.height-2*CLIP_DRAW_INSET));
        }

//        this.graphic.ifPresent(gr -> gr.paint(g2, clip));
        this.model.paint(g2, clip);
        this.modelDragSelection.paint(g2, this.zp);
    }

    private void axesClipped(final Graphics2D g) {
        // calculate clipping manually works:
        final double zer = 0D;
        final double wid = super.getWidth();
        final double hgt = super.getHeight();

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
     * @return viewport
     */
    public Rectangle2D.Double viewportClip() {
        return new Rectangle2D.Double(
            super.getX(), super.getY(),
            Math.max(0.0, super.getWidth()), Math.max(0.0, super.getHeight()));
    }

    /**
     * Calculates the clipping rectangle in canvas coordinates
     * @return clipping rect
     */
    public Rectangle2D.Double clip() {
        return this.zp.viewportToCanvas(viewportClip());
    }
}
