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

import lombok.*;

import java.awt.*;
import java.awt.geom.*;

import static nu.mine.mosher.zoom.swinglayer.example.Solarized.BASE_1;



@RequiredArgsConstructor
public class Axes {
    private static final Point2D.Double ORIGIN = new Point2D.Double();
    private static final Color AXES_COLOR = BASE_1;
    private static final Stroke AXES_STROKE = Swings.simpleStroke();

    private final ZoomPan zp;

    public void paint(final Graphics2D g, final double w, final double h) {
        g.setStroke(AXES_STROKE);
        g.setColor(AXES_COLOR);
        axesClipped(g, w, h);
    }

    private void axesClipped(final Graphics2D g, final double w, final double h) {
        // L = left, R = right, T = top, B = bottom
        final double y = zp.canvasToViewport(ORIGIN).y;
        val c_L = zp.viewportToCanvas(new Point2D.Double(0, y));
        val c_R = zp.viewportToCanvas(new Point2D.Double(w, y));
        final double x = zp.canvasToViewport(ORIGIN).x;
        val c_T = zp.viewportToCanvas(new Point2D.Double(x, 0));
        val c_B = zp.viewportToCanvas(new Point2D.Double(x, h));

        g.draw(new Line2D.Double(c_L.x, c_L.y, c_R.x, c_R.y));
        g.draw(new Line2D.Double(c_T.x, c_T.y, c_B.x, c_B.y));
    }

// with proper clipping (using doubles everywhere and no ints), this basically works, but it's a little glitchy at high zoom:
//    private void axesSimple(final Graphics2D g) {
//        g.draw(new Line2D.Double(-1e8, 0, 1e8, 0));
//        g.draw(new Line2D.Double(0, -1e8, 0, 1e8));
//    }
}
