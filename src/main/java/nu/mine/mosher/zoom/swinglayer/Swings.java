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

import java.awt.*;
import java.awt.geom.*;

public final class Swings {
    @Deprecated
    private Swings() {
        throw new UnsupportedOperationException();
    }

    public static Point2D.Double pt2d(final Point ptInt) {
        return new Point2D.Double(ptInt.x, ptInt.y);
    }

    public static Rectangle2D.Double outset(final Rectangle2D r, final double d) {
        return new Rectangle2D.Double(r.getX()-d, r.getY()-d, r.getWidth()+2*d, r.getHeight()+2*d);
    }
}
