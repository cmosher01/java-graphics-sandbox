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

package nu.mine.mosher.zoom.swinglayer;

import lombok.val;

import java.awt.*;
import java.awt.geom.*;

/**
 * Implements zooming and panning algorithms.
 */
public class ZoomPan {
    // minimum visible dimension size in pixels
    private static final double DIMENSION_MIN = 10;

    private Rectangle2D bounds;
    private double zoomOutMin = 1.0e-1D;
    private double zoomInMax  = 1.0e+2D;
    private double zoomFactor = 1.0D;

    // TODO: do we need these?
    private double dxZoomOffset = +0.0D;
    private double dyZoomOffset = +0.0D;

    private double depthFactor = 5e-2;

    private double panX;
    private double panY;

    /**
     * <p>Zooms the wrapped panel.</p>
     *
     * ZOOM <i>OUT</i>:
     * <ul>
     *     <li><i>positive</i> depth</li>
     *     <li>two-finger drag touchpad <i>towards</i> user</li>
     *     <li><i>smaller</i> image</li>
     * </ul>
     * ZOOM <i>IN</i>:
     * <ul>
     *     <li><i>negative</i> depth</li>
     *     <li>two-finger drag touchpad <i>away from</i> user</li>
     *     <li><i>larger</i> image</li>
     * </ul>
     *
     * @param depth zoom amount (positive for zoom out, negative for zoom in)
     * @param x x coord of pivot point
     * @param y y coord of pivot point
     */
    public void zoom(final int depth, final double x, final double y) {
        val oldZoom = this.zoomFactor;
        this.zoomFactor /= Math.exp(depthFactor*depth);
        clampZoom();
        val z = 1 - this.zoomFactor/oldZoom;
        pan(z * (x+dxZoomOffset-this.panX), z * (y+dyZoomOffset-this.panY));
    }

    private void clampZoom() {
        this.zoomFactor = Math.clamp(this.zoomFactor, zoomOutMin, zoomInMax);
    }

    /**
     * Pans the wrapped panel.
     *
     * @param dx x distance to pan (positive values move image to the right)
     * @param dy y distance to pan (positive values move image down)
     */
    public void pan(final double dx, final double dy) {
        this.panX += dx;
        this.panY += dy;
    }

    public void paint(final Graphics2D g) {
        g.translate(this.panX, this.panY);
        g.scale(this.zoomFactor, this.zoomFactor);
    }

    public Point2D.Double canvasToViewport(final Point2D.Double p) {
        return new Point2D.Double(zoomFactor*p.getX()+panX, zoomFactor*p.getY()+panY);
    }

    public Point2D.Double viewportToCanvas(final Point2D.Double p) {
        return new Point2D.Double((p.getX()-panX)/zoomFactor, (p.getY()-panY)/zoomFactor);
    }

    public Rectangle2D.Double viewportToCanvas(final Rectangle2D.Double v) {
        final var vTL = new Point2D.Double(v.x, v.y);
        final var cTL = viewportToCanvas(vTL);
        final var vBR = new Point2D.Double(v.x+v.width, v.y+v.height);
        final var cBR = viewportToCanvas(vBR);
        return new Rectangle2D.Double(cTL.x, cTL.y, cBR.x-cTL.x, cBR.y-cTL.y);
    }

    public void setCanvasBounds(final Rectangle2D b) {
        this.bounds = b.getBounds2D();
        this.zoomOutMin = DIMENSION_MIN / Math.min(b.getWidth(), b.getHeight());
//        System.out.printf("zoom out min: %f\n", this.zoomOutMin);
        clampZoom();
    }

    public Rectangle2D bounds() {
        return this.bounds.getBounds2D();
    }

    public double zoomFactor() {
        return this.zoomFactor;
    }


    private static int rnd(double d) {
        d = Math.rint(d);
        return (int)Math.round(d);
    }
}
