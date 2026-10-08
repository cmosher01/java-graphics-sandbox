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

import lombok.val;

import java.awt.geom.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class InteractiveRectsModel {
//    private static final int ITEM_COUNT = 1_000_000;
//    private static final double MAX_COORD = 1.0e8D;
//    public static final double ITEM_WIDTH = 10_000.0D;
//    public static final double ITEM_HEIGHT = 3_000.0D;


    private static final double BOUNDS_OUTSET = 100D; // TODO

    /**
     * All interactive rectangles, in back-to-front Z-order.
     */
    private final ArrayList<InteractiveRectModel> sqs = new ArrayList<>();

    private Rectangle2D.Double bounds;
    private Rectangle2D.Double boundsOutset;

    private final Selectable<InteractiveRectModel> selection = new InteractiveRectsSelectionModel(this.sqs);



    public boolean isPresent() {
        return !this.sqs.isEmpty();
    }

    public void readFrom(final Path path) throws IOException, InputMismatchException {
        final Grid grid = new Grid(25,0);
//        grid.deactivate();// TODO

        try (val in = new Scanner(path.toFile())) {
            while (in.hasNextDouble()) {
                val x = in.nextDouble();
                val y = in.nextDouble();
                val n = in.nextLine().strip();
                val item = new InteractiveRectModel(x, y, n, grid);
                this.sqs.add(item);
            }
        }
        updateBounds();

        // TODO set grid based on points read
    }

    ArrayList<InteractiveRectModel> sqs() {
        return this.sqs;
    }

    public void updateBounds() {
        Rectangle2D.Double b = this.sqs.getFirst().rect();
        for (val sq : this.sqs) {
            Rectangle2D.union(sq.rect(), b, b);
        }
        this.bounds = (Rectangle2D.Double)b.getBounds2D();
        b = Swings.outset(b, BOUNDS_OUTSET);
        this.boundsOutset = (Rectangle2D.Double)b.getBounds2D();
    }

    public Rectangle2D.Double bounds() {
        return (Rectangle2D.Double)this.bounds.getBounds2D();
    }

    public Rectangle2D.Double boundsOutset() {
        return (Rectangle2D.Double)this.boundsOutset.getBounds2D();
    }

    public Optional<InteractiveRectModel> getAt(final Point2D.Double at) {
        for (val sq : this.sqs.reversed()) {
            if (sq.rect().contains(at)) {
                return Optional.of(sq);
            }
        }
        return Optional.empty();
    }

    public Selectable<InteractiveRectModel> selection() {
        return this.selection;
    }

    public boolean isModified() {
        for (val sq : this.sqs) {
            if (sq.isModified()) {
                return true;
            }
        }
        return false;
    }

    public void flip() {
        for (val sq : this.sqs) {
            sq.flip();
        }
    }

    public void clear() {
        this.sqs.clear();
        this.bounds = null;
        this.boundsOutset = null;
    }
}
