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

public class InteractiveRect extends Rectangle2D.Double {
    private boolean selected;

    public InteractiveRect(final double x, final double y, final double width, final double height) {
        super(x, y, width, height);
    }

    public boolean selected() {
        return this.selected;
    }

    public void select(final boolean selected) {
        this.selected = selected;
    }



    public void move(final Point2D.Double d) {
        this.x += d.getX();
        this.y += d.getY();
    }



    // TODO potential optimization in drawing when zoomed far out
    public void paint(final Graphics2D g) {
        if (this.selected) {
            g.setColor(Solarized.MAGENTA);
        } else {
            g.setColor(Solarized.CYAN);
        }

        g.fill(this);
    }
}
