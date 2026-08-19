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

import java.awt.*;

/**
 * Implements zooming and panning algorithms.
 */
public class ZoomPan {
    private final double zoomOutMin;
    private final double zoomInMax;
    private final double depthFactor;

    public double zoomFactor = 1D;
    public double panX;
    public double panY;

    public ZoomPan(final double zoomOutMin, final double zoomInMax, final double depthFactor) {
        this.zoomOutMin = zoomOutMin;
        this.zoomInMax = zoomInMax;
        this.depthFactor = depthFactor;
    }

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
        this.zoomFactor = Math.clamp(this.zoomFactor/Math.exp(depthFactor*depth), zoomOutMin, zoomInMax);
        val z = 1 - this.zoomFactor/oldZoom;
        pan(z * (x-this.panX), z * (y-this.panY));
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

    public Point viewportToCanvas(final Point p) {
        return new Point(
            (int)Math.round(Math.rint((p.x-panX)/zoomFactor)),
            (int)Math.round(Math.rint((p.y-panY)/zoomFactor)));
    }
}
