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

package nu.mine.mosher.zoom.swing;

import java.awt.event.*;
import java.awt.geom.*;

class PanningHandler implements MouseListener, MouseMotionListener {
    private final PanAndZoomCanvas canvas;

    private AffineTransform initialTransform;
    private Point2D ptReference;
    private Point2D ptXformed;

    public PanningHandler(final PanAndZoomCanvas canvas) {
        this.canvas = canvas;
    }

    // capture the starting point
    @Override
    public void mousePressed(MouseEvent e) {
        // save the transformed starting point and the initial transform
        this.initialTransform = this.canvas.at();
        this.ptXformed = inv(this.initialTransform, e.getPoint());
        // CAML: or not:
//        this.ptXformed = e.getPoint();

        // transform the mouse point to the pan and zoom coordinates
        this.ptReference = this.ptXformed;

    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        // first transform the mouse point to the pan and zoom
        // coordinates. We must take care to transform by the
        // initial transform, not the updated transform, so that
        // both the initial reference point and all subsequent
        // reference points are measured against the same origin.
        this.ptXformed = inv(this.initialTransform, e.getPoint());
        // CAM: or not:
//        this.ptXformed = e.getPoint();

        // the size of the pan translations
        // are defined by the current mouse location subtracted
        // from the reference location
        final var delta = AwtUtil.subtract(this.ptXformed, this.ptReference);

        // make the reference point be the new mouse point.
        this.ptReference = this.ptXformed;

        // CAM:
        //
        // ----------------------------------------------
        //
        // this.canvas.translate(delta);
        // ---------
        // image moves too slowly, at any scale
        // ----------------------------------------------
        //
        // this.canvas.translate(AwtUtil.multiply(delta, 1/this.canvas.scale()));
        // this.canvas.translate(delta);
        // ---------
        // image moves at 1:1 OK
        // zoomed out, too quickly
        // zoomed in, too slowly
        // ----------------------------------------------
        //
        // this.canvas.translate(AwtUtil.multiply(delta, this.canvas.scale()));
        // this.canvas.translate(delta);
        // ---------
        // image moves at 1:1 OK
        // zoomed out, too slowly
        // zoomed in, too quickly
        // ----------------------------------------------
        //
        // this.canvas.translate(AwtUtil.multiply(delta, this.canvas.scale()*this.canvas.scale()));
        // this.canvas.translate(delta);
        // ---------
        // image moves at 1:1 OK
        // zoomed out, too slowly
        // zoomed in, too quickly
        // ----------------------------------------------
        //
        // this.canvas.translate(AwtUtil.multiply(delta, Math.sqrt(this.canvas.scale())));
        // this.canvas.translate(delta);
        // ---------
        // image moves at 1:1 OK
        // zoomed out, a little too slowly
        // zoomed in, a little too quickly
        // ----------------------------------------------
        this.canvas.translate(AwtUtil.multiply(delta, 1+Math.sqrt(this.canvas.scale())));

        this.canvas.mouse(AwtUtil.create(e.getX(), e.getY()));

        // schedule a repaint.
        this.canvas.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseMoved(MouseEvent e) {
        this.canvas.mouse(AwtUtil.create(e.getX(), e.getY()));
    }
    @Override
    public void mouseReleased(MouseEvent e) {}




    private static Point2D inv(final AffineTransform xf, final Point2D pt) {
        try {
            return xf.inverseTransform(pt, null);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
