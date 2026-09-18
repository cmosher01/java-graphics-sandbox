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

import lombok.val;

import java.awt.geom.*;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.*;

public class InteractiveRectsModel {
    private static final int ITEM_COUNT = 1_000_000;
    private static final double MAX_COORD = 1.0e8D;
//    public static final double ITEM_WIDTH = 10_000.0D;
//    public static final double ITEM_HEIGHT = 3_000.0D;

    public static final double ITEM_WIDTH = 60.0D;
    public static final double ITEM_HEIGHT = 30.0D;

    private static final double BOUNDS_OUTSET = ITEM_WIDTH;



    /**
     * All interactive rectangles, in back-to-front Z-order.
     */
    private final ArrayList<InteractiveRectModel> sqs = new ArrayList<>();



    private Rectangle2D.Double bounds;
    private Rectangle2D.Double boundsOutset;



    private final InteractiveRectsSelectionModel selection = new InteractiveRectsSelectionModel(this.sqs);



    public InteractiveRectsModel() {
//        generateRandomObjects();
        readDemoFrom(Path.of(".", "Eaton.xy"));
        updateBounds();
    }

    private void readDemoFrom(final Path path) {
        int c = 0;
        try (Scanner scanner = new Scanner(path.toFile())) {
            // Check if there are still tokens (doubles) to read
            while (scanner.hasNextDouble()) {
                val x = scanner.nextDouble();
                val y = scanner.nextDouble();
                val n = scanner.nextLine().strip();
                for (int dx = 0; dx < 10; ++dx) {
                    for (int dy = 0; dy < 50; ++dy) {
                        val x2 = 31000*dx+x;
                        val y2 =  8000*dy+y;
                        val item = new InteractiveRectModel(x2, y2, ITEM_WIDTH, ITEM_HEIGHT, n);
                        this.sqs.add(item);
                        ++c;
                    }
                }
            }
        } catch (final FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
        System.out.printf("Total count of individuals in chart: %d\n", c);
    }


    ArrayList<InteractiveRectModel> sqs() {
        return this.sqs;
    }

    private void generateRandomObjects() {
        val rand = new Random();
        for (int i = 0; i < ITEM_COUNT; i++) {
            final double x = rand.nextDouble(-MAX_COORD, MAX_COORD);
            final double y = rand.nextDouble(-MAX_COORD, MAX_COORD);
            val item = new InteractiveRectModel(x, y, ITEM_WIDTH, ITEM_HEIGHT, "Test Test Test");
            this.sqs.add(item);
        }
    }



    public void updateBounds() {
        Rectangle2D.Double b = (Rectangle2D.Double)this.sqs.getFirst().getBounds2D();
        for (val sq : this.sqs) {
            Rectangle2D.union(sq.getBounds2D(), b, b);
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
            if (sq.contains(at)) {
                return Optional.of(sq);
            }
        }
        return Optional.empty();
    }




    public InteractiveRectsSelectionModel selection() {
        return this.selection;
    }








    public boolean isDirty() {
        // TODO
        return true;
    }
}
