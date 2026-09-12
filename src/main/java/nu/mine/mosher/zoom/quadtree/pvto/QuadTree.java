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


import java.util.*;


/**
 * This implements a versatile QuadTree structure.  Not thread-safe.
 * <p>
 * Contains the following dynamic parameters:
 * <p>
 * - LEAF_MAX_OBJECTS  (default 10) is the maximum number of items stored in one leaf (splitting occurs with overflow);
 * - DYNAMIC_MAX_OBJECTS  (default false) tells whether adjusting LEAF_MAX_OBJECTS automatically is enabled;
 * - MAX_OBJ_TARGET_EXPONENT  (default 0.33333) is used in dynamically adjusting LEAF_MAX_OBJECTS, if that is enabled –
 * LEAF_MAX_OBJECTS is calculated by the formula SIZE ^ MAX_OBJ_TARGET_EXPONENT,
 * and a lower minimum of 7 is applied on top of that.
 *
 * @author pvto https://github.com/pvto
 */
public class QuadTree<T> {
    private static final int MIN_BUCKET_SIZE = 10;
    private static final boolean DYNAMIC_MAX_OBJECTS = false;
    private static final double MAX_OBJ_TARGET_EXPONENT = 1.0 / 3.0;

    private int LEAF_MAX_OBJECTS = 10;
    private Quad<T> root;
    private int size = 0;



    public CoordHolder<T> place(final double x, final double y, final T o) {
        if (root == null) {
            root = new Quad<T>(this, null, x, y, x, y);
        }
        var h = root.place(x, y, o);
        this.size++;
        if (DYNAMIC_MAX_OBJECTS && this.size % 100 == 0) {
            adjustMaxObjects();
        }
        return h;
    }

    public void setMaxObjects(final int n) {
        this.LEAF_MAX_OBJECTS = n;
    }

    public void adjustMaxObjects() {
        this.LEAF_MAX_OBJECTS = Math.max(MIN_BUCKET_SIZE, (int)Math.round(Math.rint(Math.pow(this.size, MAX_OBJ_TARGET_EXPONENT))));
    }

    public List<CoordHolder<T>> findAll(final double x1, final double y1, final double x2, final double y2) {
        List<CoordHolder<T>> ret = new ArrayList<>();
        root.findAll(x1, y1, x2, y2, ret);
        return ret;
    }



    public int size() {
        return this.size;
    }



    void setNewRoot(final Quad<T> root) {
        this.root = root;
    }

    int getMaxLeafSize() {
        return this.LEAF_MAX_OBJECTS;
    }

    void decrementSize() {
        this.size--;
    }
}
