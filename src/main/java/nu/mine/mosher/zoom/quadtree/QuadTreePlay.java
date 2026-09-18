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

package nu.mine.mosher.zoom.quadtree;

import lombok.*;
import nu.mine.mosher.zoom.quadtree.pvto.QuadTree;

import java.util.*;

public class QuadTreePlay {
    @AllArgsConstructor
    private static class Item {
        public double x;
        public double y;

        @Override
        public String toString() {
            return String.format("%8s: (%012.1f,%012.1f)", Integer.toHexString(hashCode()), x, y);
        }
    }

    private static final int ITEM_COUNT = 1_000_000;
    private static final double MAX_COORD = 1.0e8D;
    private static final List<Item> items = new ArrayList<>(ITEM_COUNT);

    public static void main(final String... args) {
        generateRandomItems();

        var qt = new QuadTree<Item>();
        qt.setMaxObjects(1000);
        for (val i : items) {
            qt.place(i.x, i.y, i);
        }
        System.out.println("QT size: "+qt.size());

        val ms = System.currentTimeMillis();
        var finds = qt.findAll(100, 100, 5000000, 1000000);
        val md = System.currentTimeMillis()-ms;
        System.out.println("search time millis: "+md);
        System.out.println("found items: "+finds.size()+":");
        for (val i : finds) {
            System.out.println(i.unwrap()+" depth: "+i.depth());
        }
    }

    private static void generateRandomItems() {
        val rand = new Random();
        for (int i = 0; i < ITEM_COUNT; i++) {
            final double x = rand.nextDouble(-MAX_COORD, MAX_COORD);
            final double y = rand.nextDouble(-MAX_COORD, MAX_COORD);
            items.add(new Item(x, y));
        }
        for (int i = 0; i < 32; ++i) {
            System.out.println(items.get(i));
        }
        System.out.println("-".repeat(60));
    }
}
