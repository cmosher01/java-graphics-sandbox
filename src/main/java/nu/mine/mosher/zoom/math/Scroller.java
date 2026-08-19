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

package nu.mine.mosher.zoom.math;

import javafx.geometry.Point2D;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;

import java.util.LinkedList;

public class Scroller extends Pane {
    private static final double SCALE_DELTA = 0.005D;
    private static final double MIN_SIZE_CANVAS = 20.0D;
    private static final double MAX_SCALE = 1.0e2D;

    private final CanvasWrapper canvas;
    private final TranslateHandler translate = new TranslateHandler();
    private final ScaleHandler scale = new ScaleHandler();

    private Scroller(final Pane canvas) {
        super(canvas);
        this.canvas = new CanvasWrapper(canvas);
    }

    public static Scroller create(final Pane canvas) {
        final var ret = new Scroller(canvas);
        ret.setOnMousePressed(ret.translate::onMousePressed);
        ret.setOnMouseDragged(ret.translate::onMouseDragged);
        ret.setOnMouseReleased(ret.translate::onMouseReleased);
        ret.setOnScroll(ret.scale::onScroll);
        return ret;
    }



    private class TranslateHandler {
        private final LinkedList<Point2D> offset = new LinkedList<>();

        public void onMousePressed(final MouseEvent t) {
            t.consume();
            final var ptMouse = pt(t.getSceneX(), t.getSceneY());
            final var ptCanvas = canvas.layout();
            final var dptOffset = ptMouse.subtract(ptCanvas);
            this.offset.clear();
            this.offset.offer(dptOffset);
        }

        public void onMouseDragged(final MouseEvent t) {
            final var ptMouse = pt(t.getSceneX(), t.getSceneY());
            final var dptOffset = this.offset.peek();
            final var delta = ptMouse.subtract(dptOffset);
            Scroller.this.canvas.layout(delta);
        }

        public void onMouseReleased(final MouseEvent t) {
            t.consume();
            this.offset.clear();;
        }
    }



    private class ScaleHandler {
        public void onScroll(final ScrollEvent t) {
            t.consume();

            /*
                How far the user scrolled (dragged with center mouse
                button, dragged two fingers on touchpad, turned mouse
                wheel, etc.).

                Positive values for scrolled "away" from user, or up,
                which mean "zoom in" (larger scale, larger canvas).

                Negative values for scrolled "towards" user, or down,
                which mean "zoom out" (smaller scale, smaller canvas).
             */
            final var dy = t.getDeltaY();
            final var zoomOut = dy < 0D;
            final var zoomIn = !zoomOut;

            if (
                (Math.abs(dy) < 1e-2D) ||
                (zoomOut && canvas.tooSmall()) ||
                (zoomIn && canvas.tooLarge())) {
                return;
            }

            final var z = Math.exp(dy * SCALE_DELTA);

            // scale to zoom in or out
            canvas.scaleBy(z);

            // translate canvas so zoom pivots around
            // mouse instead of center of scroller pane
            final var p = pt(t.getSceneX(), t.getSceneY());
            final var l = canvas.layout();
            final var f = canvas.size().multiply(-1D/2D);
            final var m = p.add(f);
            final var lp = m.subtract(m.subtract(l).multiply(z));
            canvas.layout(lp);
        }
    }


    /**
     * Simplified interface to the canvas Pane. Contains only
     * what Scroller needs. Also provides nicer methods, in terms
     * of Point2D objects rather than pairs of setBlahX() and setBlahY() methods.
     */
    private static class CanvasWrapper {
        private final Pane canvas;

        private CanvasWrapper(final Pane canvas) {
            this.canvas = canvas;
        }

        public boolean tooLarge() {
            return MAX_SCALE <= this.canvas.getScaleX();
        }

        public boolean tooSmall() {
            final var sz = this.canvas.getBoundsInParent();
            return
                sz.getWidth()  <= MIN_SIZE_CANVAS ||
                sz.getHeight() <= MIN_SIZE_CANVAS;
        }

        public Point2D size() {
            return pt(this.canvas.getWidth(), this.canvas.getHeight());
        }

        public Point2D layout() {
            return pt(this.canvas.getLayoutX(), this.canvas.getLayoutY());
        }

        public void layout(final Point2D layout) {
            this.canvas.setLayoutX(layout.getX());
            this.canvas.setLayoutY(layout.getY());
        }

        public void scaleBy(final double z) {
            final var scale = z * this.canvas.getScaleX();
            this.canvas.setScaleX(scale);
            this.canvas.setScaleY(scale);
        }
    }




    private static Point2D pt(final double x, final double y) {
        return new Point2D(x,y);
    }
}
