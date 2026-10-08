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

package dev.cmosher01.genealogy.xy;

import lombok.Setter;

public class Grid {
    @Setter
    private double grid;
    @Setter
    private double offset;
    private boolean active;



    public Grid() {
        this(1, 0);
        this.active = false;
    }

    public Grid(final double grid) {
        this(grid, 0);
    }

    public Grid(final double grid, final double offset) {
        this.grid = grid;
        this.offset = offset;
        this.active = true;
    }



    public double snap(final double u) {
        final double ret;
        if (this.active) {
            ret = round(u) * this.grid + this.offset;
        } else {
            ret = u;
        }
        return ret;
    }



    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }





    private long round(final double u) {
        return Math.round(Math.rint((u - this.offset) / this.grid));
    }
}
