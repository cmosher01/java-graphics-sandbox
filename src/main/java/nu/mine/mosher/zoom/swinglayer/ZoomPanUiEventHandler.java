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
import java.util.Objects;

import static java.awt.event.MouseEvent.*;
import static java.awt.event.MouseEvent.MOUSE_DRAGGED;
import static java.awt.event.MouseEvent.MOUSE_WHEEL;

public class ZoomPanUiEventHandler {
    private final ZoomPan zp;
    private Point pDragPivot;

    public ZoomPanUiEventHandler(final ZoomPan zp) {
        this.zp = zp;
    }



    private <V extends JPanel> void dispatch(final MouseEvent e, final JLayer<V> l) {
        final LayerUI<? super V> ui = l.getUI();
        try {
            l.setUI(null); // prevent recursion on dispatch
            l.getView().dispatchEvent(e);
        } finally {
            l.setUI(ui);
        }
    }

    private boolean mine(final MouseEvent e, final JLayer<? extends JPanel> l) {
        return
            (e.getSource() == l || e.getSource() == l.getView()) &&
            !e.isConsumed();
    }



    public void processMouseEvent(final MouseEvent e, final JLayer<? extends JPanel> l) {
        dispatch(e, l);
        if (mine(e, l)) {
            if (e.getID() == MOUSE_PRESSED) {
                val p = e.getPoint();
                this.pDragPivot = p;
                e.consume();
            } else if (e.getID() == MOUSE_RELEASED) {
                if (Objects.nonNull(this.pDragPivot)) {
                    this.pDragPivot = null;
                }
                e.consume();
            }
        }
    }

    public void processMouseMotionEvent(final MouseEvent e, final JLayer<? extends JPanel> l) {
        dispatch(e, l);
        if (mine(e, l)) {
            if (e.getID() == MOUSE_DRAGGED) {
                if (Objects.nonNull(this.pDragPivot)) {
                    val p = e.getPoint();
                    this.zp.pan(p.getX() - this.pDragPivot.x, p.getY() - this.pDragPivot.y);
                    this.pDragPivot = p;
                    l.repaint();
                    e.consume();
                }
            }
        }
    }

    public void processMouseWheelEvent(final MouseWheelEvent e, final JLayer<? extends JPanel> l) {
        dispatch(e, l);
        if (mine(e, l)) {
            if (e.getID() == MOUSE_WHEEL) {
                val p = e.getPoint();
                this.zp.zoom(e.getWheelRotation(), p.getX(), p.getY());
                l.repaint();
                e.consume();
            }
        }
    }
}
