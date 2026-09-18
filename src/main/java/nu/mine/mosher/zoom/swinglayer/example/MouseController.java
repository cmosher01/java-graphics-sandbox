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

/**
 * mouse press
 * if [SHIFT]
 *     (same action whether or not clicking on a plaque)
 *     clear selection
 *     begin selection rectangle (selecting all and only intersecting plaques)
 *     consume
 * else if hit plaque
 *     if plaque not selected, add it to selection
 *     consume
 * else
 *     pass through for panning
 *
 *
 *
 * mouse drag
 * if selecting
 *     continue selection rectangle (selecting all and only intersecting plaques)
 *     consume
 * else if hit plaque
 *     if selection exists, move all plaques/recalculate
 *     consume
 * else
 *     pass through for panning
 *
 *
 *
 * mouse release
 * if selecting
 *     clear selection rectangle (leaving selection intact)
 *     consume
 * else if hit plaque
 *     if plaque was selected before mouse press, and not dragged, remove it from selection
 *     consume
 * else if not dragged
 *     clear selection
 *     consume
 * else
 *     pass through for panning
 *
 *
 * mouse click
 * (click is press and release without intervening dragging)
 * this is a NOP; handle everything in press/(drag)/release event handlers
 *
 *
 *
 *
 * TEST CASES
 *
 * 24 nominal test cases:
 *     landed on: 1. bg, 2. selected indi, or 3. unselected indi
 *     draged, or not
 *     was [SHIFT] held down, or not
 *     were any other indi already selected, or not
 * [There are also additional, anomalous cases of fast-dragging or window losing focus,
 * which cause dropped mouse release events]
 *
 * click:
 *     PRESS, RELEASE
 *
 *     not on an individual:
 *         clear the Selection
 *     an individual in the Selection:
 *         remove from the Selection
 *     an individual not in the Selection:
 *         add to ths Selection
 *
 * drag:
 *     PRESS, DRAG, RELEASE
 *
 *     not on an individual:
 *         visually move (pan) the entire canvas around under the window
 *     an individual in the Selection:
 *         move all individuals in the Selection
 *     an individual not in the Selection:
 *         add that individual to the Selection and
 *         move all individuals in the Selection
 *
 * [SHIFT]drag:
 *     [SHIFT]PRESS, RELEASE
 *     [SHIFT]PRESS, DRAG, RELEASE
 *
 *     select all and only the individuals that intersect the rectangle
 *     described by click-position and current-mouse-position
 */
public class MouseController {
    public MouseController(
        final MouseView viewMouse,
        final MainView viewMain,
        final StatusBarView viewStatusBar,
        final ZoomPanModel modelZoomPan,
        final ZoomPanMouseController controllerZoomPan,
        final StatusBarModel modelStatus,
        final InteractiveRectsController controllerRects,
        final DragSelectionController controllerDragSelection) {
        val mouse = new MouseAdapter() {
            private boolean dragged;

            @Override
            public void mouseMoved(final MouseEvent e) {
                val at = pointOf(e);
                modelStatus.set(viewMain, at);
                viewStatusBar.refresh();
            }



            @Override
            public void mousePressed(final MouseEvent e) {
                val at = pointOf(e);
                val cnvAt = modelZoomPan.viewportToCanvas(at);

                this.dragged = false;
                if (e.isShiftDown()) {
                    controllerDragSelection.press(clipPoint(e.getPoint(), viewMouse));
                } else if (controllerRects.want(cnvAt)) {
                    controllerRects.press();
                } else {
                    controllerZoomPan.press(at);
                }

                viewMain.repaint();
                e.consume();
            }

            @Override
            public void mouseDragged(final MouseEvent e) {
                val at = pointOf(e);

                this.dragged = true;
                if (controllerDragSelection.selecting()) {
                    controllerDragSelection.drag(clipPoint(e.getPoint(), viewMouse));
                } else if (controllerRects.has()) {
                    val cnvAt = modelZoomPan.viewportToCanvas(at);
                    controllerRects.drag(cnvAt);
                } else {
                    controllerZoomPan.drag(at);
                }

                viewMain.repaint();
                modelStatus.set(viewMain, at);
                viewStatusBar.refresh();
                e.consume();
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                if (controllerDragSelection.selecting()) {
                    controllerDragSelection.release();
                } else if (controllerRects.has()) {
                    controllerRects.release(this.dragged);
                } else if (this.dragged) {
                    controllerZoomPan.release();
                } else {
                    controllerRects.release(this.dragged);
                }
                this.dragged = false;

                viewMain.repaint();
                e.consume();
            }



            @Override
            public void mouseClicked(final MouseEvent e) {
                e.consume();
            }



            @Override
            public void mouseWheelMoved(final MouseWheelEvent e) {
                val at = pointOf(e);

                controllerZoomPan.rotate(at, e.getWheelRotation());

                viewMain.repaint();
                modelStatus.set(viewMain, at);
                viewStatusBar.refresh();
                e.consume();
            }
        };

        viewMouse.addMouseListener(mouse);
        viewMouse.addMouseMotionListener(mouse);
        viewMouse.addMouseWheelListener(mouse);

        viewMouse.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                modelStatus.set(viewMain);
                viewStatusBar.refresh();
            }
        });



// SEE COMMENTS BELOW
//        viewMain.addWindowFocusListener(new WindowAdapter() {
//            @Override
//            public void windowLostFocus(final WindowEvent e) {
//                windowLostFocus();
//            }
//        });
//
//        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
//            if (e.getID() == MouseEvent.MOUSE_RELEASED) {
//                awtMouseReleased();
//            }
//        }, AWTEvent.MOUSE_EVENT_MASK);
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
