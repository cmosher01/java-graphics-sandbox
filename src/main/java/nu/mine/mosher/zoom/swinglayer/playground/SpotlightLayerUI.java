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

package nu.mine.mosher.zoom.swinglayer.playground;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;

import static java.awt.event.MouseEvent.*;

class SpotlightLayerUI extends LayerUI<JPanel> {
    @Override
    public void installUI(final JComponent c) {
        if (c instanceof JLayer<?> j) {
            super.installUI(j);
            j.setLayerEventMask(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        }
    }

    @Override
    public void uninstallUI(final JComponent c) {
        if (c instanceof JLayer<?> j) {
            j.setLayerEventMask(0);
            super.uninstallUI(j);
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        if (e.getID() != MOUSE_PRESSED) {
            return;
        }

        System.out.println("LayerUI:  now dispatching to JPanel: id=" + e.getID() + " source=" + e.getSource().getClass() + " consumed=" + e.isConsumed());
        try {
            l.setUI(null); // prevent recursion on dispatch
            l.getView().dispatchEvent(e);
        } finally {
            l.setUI(this);
        }

        System.out.println("LayerUI: done dispatching to JPanel: id=" + e.getID() + " source=" + e.getSource().getClass() + " consumed=" + e.isConsumed());
        if ((e.getSource() == l || e.getSource() == l.getView()) && !e.isConsumed()) {
            System.out.println("LayerUI: WILL HANDLE!");
            e.consume();
        } else {
            System.out.println("LayerUI: [nothing to do]");
        }
    }
}
