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

package nu.mine.mosher.zoom.swinglayer.playground.oldlayeruizoompan;

import lombok.val;
import nu.mine.mosher.zoom.swinglayer.example.ZoomPanModel;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.Objects;

import static java.awt.event.MouseEvent.*;
import static java.awt.event.MouseEvent.MOUSE_DRAGGED;
import static java.awt.event.MouseEvent.MOUSE_WHEEL;
import static nu.mine.mosher.zoom.swinglayer.example.Swings.*;

public class ZoomPanUiEventHandler {
    private final ZoomPanModel zp;
    private Point2D.Double pDragPivot;



    public ZoomPanUiEventHandler(final ZoomPanModel zp) {
        this.zp = zp;
    }



    public <V extends JPanel> void processMouseEvent(final MouseEvent e, final JLayer<V> l) {
        dispatch(e, l);
        if (mine(e, l)) {
            if (e.getID() == MOUSE_PRESSED) {
                val p = pt2d(e.getPoint());
                this.pDragPivot = p;
                l.repaint();
                e.consume();
            } else if (e.getID() == MOUSE_RELEASED) {
                if (Objects.nonNull(this.pDragPivot)) {
                    this.pDragPivot = null;
                    e.consume();
                }
            } else if (e.getID() == MOUSE_DRAGGED) {
                if (Objects.nonNull(this.pDragPivot)) {
                    val p = pt2d(e.getPoint());
                    this.zp.pan(p.getX() - this.pDragPivot.x, p.getY() - this.pDragPivot.y);
                    this.pDragPivot = p;
                    l.repaint();
                    e.consume();
                }
            }
        }
    }

    public <V extends JPanel> void processMouseWheelEvent(final MouseWheelEvent e, final JLayer<V> l) {
        dispatch(e, l);
        if (mine(e, l)) {
            if (e.getID() == MOUSE_WHEEL) {
                val p = pt2d(e.getPoint());
                this.zp.zoom(e.getWheelRotation(), p.getX(), p.getY());
                l.repaint();
                e.consume();
            }
        }
    }



    private static <V extends JPanel> void dispatch(final MouseEvent e, final JLayer<V> l) {
        final LayerUI<? super V> ui = l.getUI();
        try {
            l.setUI(null); // prevent recursion on dispatch
            l.getView().dispatchEvent(e);
        } finally {
            l.setUI(ui);
        }
    }


    private static <V extends JPanel> boolean mine(final MouseEvent e, final JLayer<V> l) {
        return
            (e.getSource() == l || e.getSource() == l.getView()) &&
            !e.isConsumed();
    }
}
