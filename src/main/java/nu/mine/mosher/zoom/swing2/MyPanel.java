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

package nu.mine.mosher.zoom.swing2;

import lombok.val;
import nu.mine.mosher.zoom.swingpanel.ZoomPan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

class MyPanel extends JPanel {
    private static final boolean DOUBLE_BUFFERED = true;
    private static final LayoutManager NO_LAYOUT_MANAGER = null;

    private final RedSquare sq = new RedSquare();

    public MyPanel(final ZoomPan zp) {
        super(NO_LAYOUT_MANAGER, DOUBLE_BUFFERED);

        val mouser = new MouseAdapter() {
            private Point draggedFrom;

            @Override
            public void mousePressed(final MouseEvent e) {
                val p = zp.viewportToCanvas(e.getPoint());
                if (sq.hit(p)) {
                    draggedFrom = p;
                    getParent().repaint();
                } else {
                    redispatch(e);
                }
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                if (Objects.nonNull(draggedFrom)) {
                    final Point p = zp.viewportToCanvas(e.getPoint());
                    sq.move(new Point(p.x-draggedFrom.x, p.y-draggedFrom.y));
                    draggedFrom = p;
                    getParent().repaint();
                } else {
                    redispatch(e);
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (Objects.nonNull(draggedFrom)) {
                    draggedFrom = null;
                    getParent().repaint();
                } else {
                    redispatch(e);
                }
            }

            // need to redispatch unhandled events so wrapping ZoomPanPanel gets them
            private void redispatch(final MouseEvent e) {
                val e2 = SwingUtilities.convertMouseEvent(MyPanel.this, e, getParent());
                getParent().dispatchEvent(e2);
            }
        };

        addMouseListener(mouser);
        addMouseMotionListener(mouser);
    }

    private static Font FONT = new Font("Noto Sans", Font.PLAIN, 32);
    private static final Rectangle NO_CLIP = new Rectangle(-Integer.MAX_VALUE/2, -Integer.MAX_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.setClip(NO_CLIP);

        g.setFont(FONT);
        g.drawString("This is my custom Panel!", 10, 20);

        this.sq.paintSquare(g);
    }
}
