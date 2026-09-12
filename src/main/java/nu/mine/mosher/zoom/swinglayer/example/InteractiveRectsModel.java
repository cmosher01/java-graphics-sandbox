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
import nu.mine.mosher.zoom.spacialgrid.SpatialGrid;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;

import static nu.mine.mosher.zoom.swinglayer.example.Solarized.*;

public class InteractiveRectsModel {
    private static final int ITEM_COUNT = 1_000_000;
    private static final double MAX_COORD = 1.0e8D;
    public static final double ITEM_WIDTH = 10_000.0D;
    public static final double ITEM_HEIGHT = 3_000.0D;

    private static final double BOUNDS_OUTSET = ITEM_WIDTH;



    /**
     * All interactive rectangles, in back-to-front Z-order.
     */
    private static final boolean USE_SPATIAL_TREE = false; // doesn't work when zooming out; no noticeable improvement anyway
    private final ArrayList<InteractiveRect> sqs = new ArrayList<>();
    private final SpatialGrid<InteractiveRect> sqq = new SpatialGrid<>(ITEM_WIDTH);



    private Rectangle2D.Double bounds;
    private Rectangle2D.Double boundsOutset;

    /**
     * Set of currently selected items. Redundant with InteractiveRect::selected() property.
     */
    private final Set<InteractiveRect> selection = Collections.newSetFromMap(new IdentityHashMap<>());



    public InteractiveRectsModel() {
        generateRandomObjects();
        updateBounds();
    }

    private void generateRandomObjects() {
        val rand = new Random();
        for (int i = 0; i < ITEM_COUNT; i++) {
            final double x = rand.nextDouble(-MAX_COORD, MAX_COORD);
            final double y = rand.nextDouble(-MAX_COORD, MAX_COORD);
            val item = new InteractiveRect(x, y, ITEM_WIDTH, ITEM_HEIGHT);
            this.sqs.add(item);
            if (USE_SPATIAL_TREE) {
                this.sqq.insert(new SpatialGrid.Rectangle<>(x, y, ITEM_WIDTH, ITEM_HEIGHT, item));
            }
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
        if (USE_SPATIAL_TREE) {
            var found = this.sqq.query(at.getX()-1, at.getY()-1, 2, 2);
            if (!found.isEmpty()) {
                return Optional.of(found.getLast().unwrap());
            }
        } else {
            for (val sq : this.sqs.reversed()) {
                if (sq.contains(at)) {
                    return Optional.of(sq);
                }
            }
        }
        return Optional.empty();
    }




    // TODO move all selection related method/vars to separate class
    public void clearSelection() {
        this.selection.forEach(sq -> sq.select(false));
        this.selection.clear();
    }

    public void selectOne(final InteractiveRect sq, final boolean select) {
        sq.select(select);
        if (select) {
            this.selection.add(sq);
        } else {
            this.selection.remove(sq);
        }
    }

    public void setSelectionFromRectangle(final Rectangle2D.Double r) {
        // TODO potential optimization in searching for hits
        if (USE_SPATIAL_TREE) {
            var found = this.sqq.query(r.getX(), r.getY(), r.getWidth(), r.getHeight());
            clearSelection();
            found.forEach(h -> selectOne(h.unwrap(), true));
        } else {
            for (val sq : this.sqs.reversed()) {
                val hit = sq.intersects(r);
                selectOne(sq, hit);
            }
        }
    }

    public void moveSelection(final Point2D.Double delta) {
        this.selection.forEach(sq -> {
            sq.move(delta);
            if (USE_SPATIAL_TREE) {
                throw new UnsupportedOperationException("need to update spatial tree");
            }
        });
    }







    private static final boolean BOUNDS_FILL = true;
    private static final Color BOUNDS_FILL_COLOR = BASE__3_BEIGE_BRT;

    private static final boolean BOUNDS_DRAW = true;
    private static final Color BOUNDS_DRAW_COLOR = BASE_01_GRAY__DRK;
    private static final Stroke BOUNDS_DRAW_STROKE = Swings.simpleStroke(100);



    public void paintBackground(final Graphics2D g) {
        if (BOUNDS_FILL) {
            g.setColor(BOUNDS_FILL_COLOR);
            g.fill(boundsOutset());
        }

        if (BOUNDS_DRAW) {
            g.setStroke(BOUNDS_DRAW_STROKE);
            g.setColor(BOUNDS_DRAW_COLOR);
            g.draw(boundsOutset());
        }
    }

    public void paint(final Graphics2D g, final Rectangle2D clip, ZoomPanModel zp) {
        if (USE_SPATIAL_TREE && 0.0001 < zp.zoomFactor()) {
            var found = this.sqq.query(clip.getX(), clip.getY(), clip.getWidth(), clip.getHeight());
            found.forEach(h -> h.unwrap().paint(g));
        } else {
            this.sqs.forEach(sq -> {
                if (sq.intersects(clip)) {
                    sq.paint(g);
                }
            });
        }
    }
}
