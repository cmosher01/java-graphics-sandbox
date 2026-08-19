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

package nu.mine.mosher.zoom.swingpanel;

import lombok.val;

import javax.swing.*;
import java.awt.*;

public final class ZoomPanPanel extends JPanel {
    private static final Rectangle NO_CLIP = new Rectangle(-Integer.MAX_VALUE/2, -Integer.MAX_VALUE/2, Integer.MAX_VALUE, Integer.MAX_VALUE);
    private static final boolean DOUBLE_BUFFERED = true;

    private final ZoomPan zp;

    public ZoomPanPanel(final Component wrapped, final ZoomPan zp) {
        super(new BorderLayout(), DOUBLE_BUFFERED);
        this.zp = zp;

        add(wrapped);

        val mouser = new ZoomPanPanelEventHandler(zp, this);
        addMouseListener(mouser);
        addMouseMotionListener(mouser);
        addMouseWheelListener(mouser);
    }

    @Override
    public void paint(final Graphics g) {
        val g2 = (Graphics2D)g.create();
        g2.setClip(NO_CLIP);
        this.zp.paint(g2);
        paintChildren(g2);
        g2.dispose();
    }
}
