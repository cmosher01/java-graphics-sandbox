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

package nu.mine.mosher.zoom.swinglayer;

import lombok.val;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.*;

import static java.awt.AWTEvent.*;

public final class ZoomPanUi extends LayerUI<JPanel> {
    private static final long NO_EVENTS_MASK = 0L;

    private final ZoomPan zp;
    private final ZoomPanUiEventHandler handler;

    public ZoomPanUi(final ZoomPan zp) {
        this.zp = zp;
        this.handler = new ZoomPanUiEventHandler(zp);
    }

    @Override
    public void installUI(final JComponent c) {
        if (c instanceof JLayer<?> j) {
            super.installUI(j);
            j.setLayerEventMask(MOUSE_EVENT_MASK | MOUSE_MOTION_EVENT_MASK | MOUSE_WHEEL_EVENT_MASK);
        }
    }

    @Override
    public void uninstallUI(final JComponent c) {
        if (c instanceof JLayer<?> j) {
            j.setLayerEventMask(NO_EVENTS_MASK);
            super.uninstallUI(j);
        }
    }



    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        super.processMouseEvent(e, l);
        this.handler.processMouseEvent(e, l);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        super.processMouseMotionEvent(e, l);
        this.handler.processMouseMotionEvent(e, l);
    }

    @Override
    protected void processMouseWheelEvent(MouseWheelEvent e, JLayer<? extends JPanel> l) {
        super.processMouseWheelEvent(e, l);
        this.handler.processMouseWheelEvent(e, l);
    }



    @Override
    public void paint(final Graphics g, final JComponent c) {
        val g2 = (Graphics2D)g.create();
        g2.setClip(null);
        this.zp.paint(g2);
        c.paint(g2);
        g2.dispose();
    }
}
