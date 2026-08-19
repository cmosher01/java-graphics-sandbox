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

package nu.mine.mosher.zoom.swinglayer.example;

import lombok.val;
import nu.mine.mosher.zoom.swinglayer.ZoomPan;

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

            private boolean mine(final MouseEvent e) {
                return e.getSource() == MyPanel.this && !e.isConsumed();
            }

            @Override
            public void mousePressed(final MouseEvent e) {
                val p = zp.viewportToCanvas(e.getPoint());
                if (mine(e) && sq.hit(p)) {
                    draggedFrom = p;
                    repaint();
                    e.consume();
                }
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                if (mine(e) && Objects.nonNull(draggedFrom)) {
                    final Point p = zp.viewportToCanvas(e.getPoint());
                    sq.move(new Point(p.x-draggedFrom.x, p.y-draggedFrom.y));
                    draggedFrom = p;
                    repaint();
                    e.consume();
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (mine(e) && Objects.nonNull(draggedFrom)) {
                    draggedFrom = null;
                    repaint();
                    e.consume();
                }
            }
        };

        addMouseListener(mouser);
        addMouseMotionListener(mouser);
    }

    private static Font FONT = new Font("Noto Sans", Font.PLAIN, 32);

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.setClip(null);

        g.setFont(FONT);
        g.drawString("This is my custom Panel!", 10, 20);

        this.sq.paintSquare(g);
    }
}
