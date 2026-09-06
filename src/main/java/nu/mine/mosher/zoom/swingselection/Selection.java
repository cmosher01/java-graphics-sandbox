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

package nu.mine.mosher.zoom.swingselection;

import lombok.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Objects;



public class Selection implements MouseListener, MouseMotionListener, WindowFocusListener, AWTEventListener {
    public static final Color RECT_FILL_COLOR = new Color(38, 139, 210, 10);
    public static final Color RECT_STROKE_COLOR = new Color(38, 139, 210);
    public static final BasicStroke RECT_STROKE = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{1f, 2f}, 0);



    @Setter
    private Selectable selectable = new Selectable() {};

    private Point ptOrig;
    private Point ptCurr;



    @Override
    public void mousePressed(final MouseEvent e) {
        if (e.isShiftDown()) {
            val pt = getClippedPoint(e);
            this.ptOrig = pt;
            this.ptCurr = pt;

            this.selectable.start(bounds());

            e.consume();
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (selecting()) {
            val pt = getClippedPoint(e);
            if (!pt.equals(this.ptCurr)) {
                this.ptCurr = pt;

                this.selectable.update(bounds());
            }
            e.consume();
        }
    }

    @Override
    public void windowLostFocus(final WindowEvent e) {
        mouseReleased();
    }

    @Override
    public void eventDispatched(final AWTEvent e) {
        if (e.getID() == MouseEvent.MOUSE_RELEASED) {
            mouseReleased();
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (selecting()) {
            e.consume();
        }
        mouseReleased();
    }

    public void mouseReleased() {
        if (selecting()) {
            this.selectable.end();

            this.ptOrig = null;
            this.ptCurr = null;
        }
    }



    @Override
    public void mouseEntered(final MouseEvent e) {
    }
    @Override
    public void mouseMoved(final MouseEvent e) {
    }
    @Override
    public void mouseExited(final MouseEvent e) {
    }
    @Override
    public void mouseClicked(final MouseEvent e) {
    }
    @Override
    public void windowGainedFocus(final WindowEvent e) {
    }





    public void paint(final Graphics2D g) {
        if (selecting()) {
            val r = bounds();

            g.setColor(RECT_FILL_COLOR);
            g.fill(r);

            g.setColor(RECT_STROKE_COLOR);
            g.setStroke(RECT_STROKE);
            g.draw(r);
        }
    }



    private boolean selecting() {
        return Objects.nonNull(this.ptOrig) && Objects.nonNull(this.ptCurr);
    }

    private Rectangle2D.Double bounds() {
        val r = new Rectangle(this.ptOrig);
        r.add(this.ptCurr);
        return new Rectangle2D.Double(r.getX(), r.getY(), nonZero(r.getWidth()), nonZero(r.getHeight()));
    }



    private static double nonZero(final double a) {
        return Math.max(1.0, a);
    }

    private static Point getClippedPoint(final MouseEvent e) {
        val p = e.getPoint();
        val c = e.getComponent();
        return new Point(Math.clamp(p.x, 0, c.getWidth()), Math.clamp(p.y, 0, c.getHeight()));
    }





    private static class LoggingSelectable implements Selectable {
        @Override
        public void start(final Rectangle2D bounds) {
            trace("START", bounds);
        }
        @Override
        public void update(final Rectangle2D bounds) {
            trace("UPDATE", bounds);
        }
        @Override
        public void end() {
            trace("END", null);
        }
        private void trace(final String m, final Rectangle2D r) {
            if (Objects.nonNull(r)) {
                System.out.printf("SELECTION: %-6s bounds=(%04.0f,%04.0f)[%04.0fx%04.0f] %s\n",
                    m, r.getX(), r.getY(), r.getWidth(), r.getHeight(), (r.isEmpty() ? " [EMPTY]" : ""));
            } else {
                System.out.printf("SELECTION: %-6s\n", m);
            }
        }
    }
}
