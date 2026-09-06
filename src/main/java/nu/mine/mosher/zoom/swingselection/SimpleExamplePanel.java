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

import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

class SimpleExamplePanel extends JPanel implements Selectable {
    private final Selection selection;

    public SimpleExamplePanel(final Selection selection) {
        setBackground(Color.WHITE);

        this.selection = selection;
        addMouseListener(this.selection);
        addMouseMotionListener(this.selection);
        Toolkit.getDefaultToolkit().addAWTEventListener(this.selection, AWTEvent.MOUSE_EVENT_MASK);
    }

    @Override
    protected void paintComponent(final Graphics gr) {
        super.paintComponent(gr);
        val g = (Graphics2D)gr;

        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.drawString("Shift-click and drag to see a transparent selection rectangle.", 40, 100);

        g.setColor(Color.ORANGE);
        g.fillRect(150, 150, 100, 100);

        this.selection.paint(g);
    }

    @Override
    public void update(final Rectangle2D bounds) {
        System.out.println("SELECTING: "+bounds);
        repaint();
    }

    @Override
    public void end() {
        repaint();
    }
}
