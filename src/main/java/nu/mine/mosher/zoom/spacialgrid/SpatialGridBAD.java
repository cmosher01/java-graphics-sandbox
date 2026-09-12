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

package nu.mine.mosher.zoom.spacialgrid;

import java.util.*;

// Gemini AI generated, with lots of my prompting
public class SpatialGridBAD<T> {
    public static class Rectangle<T> {
        public double x, y;
        public final double width, height;
        public final T data;

        public Rectangle(double x, double y, double w, double h, T data) {
            this.x = x; this.y = y; this.width = w; this.height = h; this.data = data;
        }
        public T unwrap() { return data; }
    }

    private final double cellSize;
    private final Map<Long, List<Rectangle<T>>> grid = new HashMap<>();

    public SpatialGridBAD(double avgRectWidth) { this.cellSize = avgRectWidth; }

    public void insert(Rectangle<T> r) {
        forEachCell(r.x, r.y, r.width, r.height, k -> grid.computeIfAbsent(k, x -> new ArrayList<>(2)).add(r));
    }

    public void remove(Rectangle<T> r) {
        forEachCell(r.x, r.y, r.width, r.height, k -> {
            List<Rectangle<T>> b = grid.get(k);
            if (b != null && b.remove(r) && b.isEmpty()) grid.remove(k);
        });
    }

    public void move(Rectangle<T> r, double newX, double newY) {
        remove(r);
        r.x = newX; r.y = newY;
        insert(r);
    }

    public List<Rectangle<T>> query(double qx, double qy, double qw, double qh) {
        List<Rectangle<T>> res = new ArrayList<>();
        forEachCell(qx, qy, qw, qh, k -> {
            List<Rectangle<T>> b = grid.get(k);
            if (b != null) {
                for (Rectangle<T> r : b) {
                    boolean overlap = !(qx > r.x + r.width || qx + qw < r.x || qy > r.y + r.height || qy + qh < r.y);
                    if (overlap && !res.contains(r)) res.add(r);
                }
            }
        });
        return res;
    }

    // Simplifies coordinate parsing into a single reusable loop
    private void forEachCell(double x, double y, double w, double h, java.util.function.LongConsumer action) {
        int startC = (int) Math.floor(x / cellSize), endC = (int) Math.floor((x + w) / cellSize);
        int startR = (int) Math.floor(y / cellSize), endR = (int) Math.floor((y + h) / cellSize);
        for (int c = startC; c <= endC; c++) {
            for (int r = startR; r <= endR; r++) {
                action.accept((((long) c) << 32) | (r & 0xFFFFFFFFL));
            }
        }
    }
}
