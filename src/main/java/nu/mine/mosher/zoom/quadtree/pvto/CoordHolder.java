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

package nu.mine.mosher.zoom.quadtree.pvto;

public class CoordHolder<T> {
    private final T o;
    private final QuadTree<T> quadTree;
    Quad<T> quad;
    double x, y;

    CoordHolder(final QuadTree<T> quadTree, final double x, final double y, final T o, final Quad<T> quad) {
        this.o = o;
        this.quadTree = quadTree;
        this.quad = quad;
        this.x = x;
        this.y = y;
    }

    public T unwrap() {
        return this.o;
    }

    public void relocate(final double x, final double y) {
        this.x = x;
        this.y = y;
        this.quad.replace(this, 0);
    }

    public void remove() {
        if (this.quad.items.remove(this)) {
            this.quadTree.decrementSize();
        }
    }

    public int depth() {
        int i = 0;
        Quad<T> q = this.quad;
        while (q != null) {
            i++;
            q = q.parent;
        }
        return i;
    }
}
