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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

import static nu.mine.mosher.zoom.swinglayer.example.Swings.pointOf;

public class MouseController {
    public int STATUS_REFRESH_MILLIS = 500;

    public MouseController(final MouseView viewMouse, final MainPane paneMain, final StatusBarView sb, ZoomPanModel zp, final ZoomPanMouseController zpMouse, final StatusModel status, final InteractiveRectsController modelMouse, final DragSelectionController mouseCtlrDragSelection) {
        val mouse = new MouseAdapter() {
            private boolean dragged;

            @Override
            public void mouseMoved(final MouseEvent e) {
                val at = pointOf(e);
                status.set(paneMain, at);
                sb.refresh();
            }



            @Override
            public void mousePressed(final MouseEvent e) {
                val at = pointOf(e);
                val cnvAt = zp.viewportToCanvas(at);

                this.dragged = false;
                if (e.isShiftDown()) {
                    mouseCtlrDragSelection.press(clipPoint(e.getPoint(), viewMouse));
                } else if (modelMouse.want(cnvAt)) {
                    modelMouse.press();
                } else {
                    zpMouse.press(at);
                }

                paneMain.repaint();
                e.consume();
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                val at = pointOf(e);

                this.dragged = true;
                if (mouseCtlrDragSelection.selecting()) {
                    mouseCtlrDragSelection.drag(clipPoint(e.getPoint(), viewMouse));
                } else if (modelMouse.has()) {
                    val cnvAt = zp.viewportToCanvas(at);
                    modelMouse.drag(cnvAt);
                } else {
                    zpMouse.drag(at);
                }

                paneMain.repaint();
                status.set(paneMain, at);
                sb.refresh();
                e.consume();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (mouseCtlrDragSelection.selecting()) {
                    mouseCtlrDragSelection.release();
                } else if (modelMouse.has()) {
                    modelMouse.release(this.dragged);
                } else if (this.dragged) {
                    zpMouse.release();
                } else {
                    modelMouse.release(this.dragged);
                }
                this.dragged = false;

                paneMain.repaint();
                e.consume();
            }



            @Override
            public void mouseClicked(final MouseEvent e) {
                e.consume();
            }



            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                val at = pointOf(e);

                zpMouse.rotate(at, e.getWheelRotation());

                paneMain.repaint();
                status.set(paneMain, at);
                sb.refresh();
                e.consume();
            }
        };

        viewMouse.addMouseListener(mouse);
        viewMouse.addMouseMotionListener(mouse);
        viewMouse.addMouseWheelListener(mouse);

//        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
//            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
//                awtMouseReleased();
//            }
//        }, AWTEvent.MOUSE_EVENT_MASK);

        viewMouse.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                status.set(paneMain);
                sb.refresh();
            }
        });



        new Timer(STATUS_REFRESH_MILLIS, e -> {
            status.set(paneMain);
            sb.refresh();
        }).start();
    }



    // TODO With these two methods as NOPs, selection functionality works nominally
    // except for a couple corner cases:
    // 1. fast dragging and releasing out of the window, sometimes causing dropped MOUSE_RELEASE messages
    // 2. window losing focus (e.g., alt-tab) sometimes causing the same
    // A dropped MOUSE_RELEASE can cause the selection rectangle to stay on the screen.
    // It's proving difficult to fix these without ruining the nominal behavior.

//    public void windowLostFocus() {
//    }
//
//    private void awtMouseReleased() {
//    }





    private static Point2D.Double clipPoint(final Point p, final JComponent view) {
        return new Point2D.Double(
            Math.clamp(p.x, 1, view.getWidth()-1),
            Math.clamp(p.y, 1, view.getHeight()-1));
    }
}
