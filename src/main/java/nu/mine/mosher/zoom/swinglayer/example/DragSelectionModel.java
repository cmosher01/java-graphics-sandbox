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

import java.awt.*;
import java.awt.geom.*;

public class DragSelectionModel {
    private static final Color RECT_FILL_COLOR = new Color(38, 139, 210, 10);
    private static final Color RECT_STROKE_COLOR = new Color(38, 139, 210);

    private final Rectangle2D.Double bounds = new Rectangle2D.Double();

    public boolean selecting() {
        return !this.bounds.isEmpty();
    }

    public Rectangle2D.Double bounds() {
        return (Rectangle2D.Double)this.bounds.getBounds2D();
    }

    public void set(final Point2D a, final Point2D b) {
        this.bounds.setFrameFromDiagonal(a, b);
        if (this.bounds.width <= 0D) {
            this.bounds.width = 1D;
        }
        if (this.bounds.height <= 0D) {
            this.bounds.height = 1D;
        }
    }

    public void clear() {
        this.bounds.setRect( 0, 0, 0, 0);
    }



    public void paint(final Graphics2D g, ZoomPan zp) {
        if (selecting()) {
            g.setColor(RECT_FILL_COLOR);
            g.fill(zp.viewportToCanvas(this.bounds));

            g.setColor(RECT_STROKE_COLOR);
            g.draw(zp.viewportToCanvas(this.bounds));
        }
    }
}
