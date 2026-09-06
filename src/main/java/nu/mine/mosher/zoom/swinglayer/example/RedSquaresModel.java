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

import com.sun.javafx.font.directwrite.RECT;
import lombok.val;
import nu.mine.mosher.zoom.swinglayer.Swings;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static java.awt.BasicStroke.*;
import static nu.mine.mosher.zoom.swinglayer.Solarized.*;

public class RedSquaresModel {
    private static final int ITEM_COUNT = 1_000_000;
    private static final double MAX_COORD = 1.0e8D;
    public static final double ITEM_WIDTH = 10_000.0D;
    public static final double ITEM_HEIGHT = 3_000.0D;

    private static final double BOUNDS_OUTSET = ITEM_WIDTH;

    private final ArrayList<InteractiveRect> sqs = new ArrayList<>();
    private Rectangle2D.Double bounds;
    private Rectangle2D.Double boundsOutset;
    private final Set<InteractiveRect> selection = Collections.newSetFromMap(new IdentityHashMap<>());



    public RedSquaresModel() {
        generateRandomObjects();
        updateBounds();
    }

    private void generateRandomObjects() {
        val rand = new Random();
        for (int i = 0; i < ITEM_COUNT; i++) {
            final double x = rand.nextDouble(-MAX_COORD, MAX_COORD);
            final double y = rand.nextDouble(-MAX_COORD, MAX_COORD);
            this.sqs.add(new InteractiveRect(x, y, ITEM_WIDTH, ITEM_HEIGHT));
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



    public Optional<InteractiveRect> getAt(final Point2D.Double at) {
        // TODO potential optimization in searching for hits
        for (val sq : this.sqs.reversed()) {
            if (sq.contains(at)) {
                return Optional.of(sq);
            }
        }
        return Optional.empty();
    }




    // TODO move all selection related method/vars to separate class
    public void clearSelection() {
        this.selection.forEach(sq -> sq.select(false));
        this.selection.clear();
    }

    public void select(final InteractiveRect sq, final boolean select) {
        sq.select(select);
        if (select) {
            this.selection.add(sq);
        } else {
            this.selection.remove(sq);
        }
    }

    public void setSelectionFromRectangle(final Rectangle2D.Double r) {
        // TODO potential optimization in searching for hits
        for (val sq : this.sqs.reversed()) {
            val hit = sq.intersects(r);
            select(sq, hit);
        }
    }

    public void moveSelection(final Point2D.Double delta) {
        this.selection.forEach(sq -> sq.move(delta));
    }







    private static final boolean BOUNDS_FILL = false;
    private static final Color COLOR_CANVAS_BG = BASE_3;

    private static final boolean BOUNDS_DRAW = true;
    private static final Stroke BOUNDS_STROKE = new BasicStroke(10.0F, CAP_BUTT, JOIN_BEVEL);



    public void paintBackground(final Graphics2D g, final Rectangle2D clip) {
        if (BOUNDS_FILL) {
            g.setColor(COLOR_CANVAS_BG);
            // some glitching at zoom in greater than ~30:
            g.fill(bounds());
        }

        if (BOUNDS_DRAW) {
            g.setStroke(BOUNDS_STROKE);
            g.setColor(BASE_03);
            g.draw(boundsOutset());
        }
    }

    public void paint(final Graphics2D g, final Rectangle2D clip) {
        // TODO potential optimization in searching for clip region
        this.sqs.forEach(sq -> {
            if (sq.intersects(clip)) {
                sq.paint(g);
            }
        });
    }
}
