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

import javafx.geometry.*;
import javafx.stage.Window;

public final class JavaFxUtil {
    @Deprecated
    private JavaFxUtil() {
        throw new UnsupportedOperationException();
    }


    /**
     * Builds a rectangle that is "to" fractional size relative to size of "source",
     * to/from the center point.
     * For example, sizeRelative(r, 0.75D) returns a rectangle that is 75% the size of r.
     * @param source
     * @param to
     * @return
     */
    public static Rectangle2D sizeRelative(final Rectangle2D source, final double to) {
        final var f  = (1D-to)/2D;

        final var dw = f * source.getWidth();
        final var l  = dw;
        final var r  = dw;

        final var dh = f * source.getHeight();
        final var t  = dh;
        final var b  = dh;

        return inset(source, new Insets(t,r,b,l));
    }

    public static Rectangle2D inset(final Rectangle2D source, final Insets i) {
        double x = source.getMinX() + i.getLeft();
        double y = source.getMinY() + i.getTop();

        double w = source.getWidth()  - (i.getLeft() + i.getRight());
        double h = source.getHeight() - (i.getTop()  + i.getBottom());

        return new Rectangle2D(x, y, w, h);
    }


    public static void setWindowLocation(final Window window, final Rectangle2D location) {
        window.setX(location.getMinX());
        window.setY(location.getMinY());

        window.setWidth(location.getWidth());
        window.setHeight(location.getHeight());
    }
}
