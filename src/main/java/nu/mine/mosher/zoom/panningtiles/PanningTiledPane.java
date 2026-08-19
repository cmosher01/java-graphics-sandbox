/*
 *     Copyright © 2026, Christopher Alan Mosher, New York, New York, USA, <cmosher01@gmail.com>.
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

package nu.mine.mosher.zoom.panningtiles;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 *
 * Pane that holds a two-dimensional array of tiles, which
 * are wrappers for ImageViews. The pane can be panned by
 * dragging the mouse, and the tiles will be reused to display
 * new images.
 *
 * The PanningTiledPane requires a factory for creating the images
 * to be displayed in the tiles, which is represented by an ImageSupplier.
 * ImageSupplier is a FunctionalInterface defining a method taking the
 * location in the array of the tile (as two ints) and returning the
 * Image. This method will be invoked on a background thread when a new
 * tile is required, and the tile will be updated with the resulting
 * image when it completes.
 *
 */
public class PanningTiledPane extends Pane {

    private final int tileWidth ;
    private final int tileHeight ;

    // amount scrolled left, in pixels:
    private final DoubleProperty xOffset = new DoublePropertyBase() {

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "xOffset";
        }

        @Override
        protected void invalidated() {
            requestLayout();
        }

        @Override
        public String toString() {
            return ""+this.doubleValue();
        }
    };

    // amount scrolled up, in pixels:
    private final DoubleProperty yOffset = new DoublePropertyBase() {

        @Override
        public Object getBean() {
            return this;
        }

        @Override
        public String getName() {
            return "yOffset";
        }

        @Override
        protected void invalidated() {
            requestLayout();
        }

        @Override
        public String toString() {
            return ""+this.doubleValue();
        }
    };

    // number of whole tiles shifted to left:
    private final ReadOnlyIntegerWrapper tileXOffset = new ReadOnlyIntegerWrapper();
    // number of whole tiles shifted up:
    private final ReadOnlyIntegerWrapper tileYOffset = new ReadOnlyIntegerWrapper();

    // for enabling dragging:
    private double mouseAnchorX;
    private double mouseAnchorY;


    private final ImageSupplier imageSupplier ;

    private final Map<GridLocation, Tile> tileMap = Collections.synchronizedMap(new HashMap<>());
    private final Set<Tile> availableTiles = Collections.synchronizedSet(new HashSet<>());

    /**
     *
     * @param tileWidth The (fixed) width of each tile.
     * @param tileHeight The (fixed) height of each tile.
     * @param imageSupplier A factory for creating images for a given tile, which will
     * be invoked when needed on a background thread - consequently, this function should
     * not directly access nodes that are part of a scene graph that is displayed.
     */
    public PanningTiledPane(int tileWidth, int tileHeight, ImageSupplier imageSupplier) {
        super();
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;

        this.imageSupplier = imageSupplier ;


        // update number of tiles offset when number of pixels offset changes:
        tileXOffset.bind(xOffset.divide(tileWidth));
        tileYOffset.bind(yOffset.divide(tileHeight));

        // enable panning on pane (just update offsets when dragging):

        setOnMousePressed(e -> {
            mouseAnchorX = e.getSceneX();
            mouseAnchorY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            double deltaX = e.getSceneX() - mouseAnchorX;
            double deltaY = e.getSceneY() - mouseAnchorY;
            xOffset.set(xOffset.get() + deltaX);
            yOffset.set(yOffset.get() + deltaY);
            mouseAnchorX = e.getSceneX();
            mouseAnchorY = e.getSceneY();
        });
    }

    @Override
    protected synchronized void layoutChildren() {

        int minColIndex = (int) Math.floor(-xOffset.get() / tileWidth);
        int maxColIndex = (int) Math.floor((-xOffset.get() + getWidth()) / tileWidth);
        int minRowIndex = (int) Math.floor(-yOffset.get() / tileHeight);
        int maxRowIndex = (int) Math.floor((-yOffset.get() + getHeight()) / tileHeight);

        {
            // [CAM]

            System.out.printf("cols(x): %d-%d, rows(y): %d-%d\n", minRowIndex, maxRowIndex, minColIndex, maxColIndex);
            final var parentCoordsOfOurOrigin = localToParent(Point2D.ZERO);
            System.out.printf("top-left: (%.1f,%.1f)\n", parentCoordsOfOurOrigin.getX(), parentCoordsOfOurOrigin.getY());
        }

        for (final var it = tileMap.entrySet().iterator(); it.hasNext();) {
            final var entry = it.next();
            if (!entry.getKey().inRangeInclusive(minColIndex, maxColIndex, minRowIndex, maxRowIndex)) {
                it.remove();
                removeTile(entry.getValue());
                availableTiles.add(entry.getValue());
            }
        }

        for (int col = minColIndex ; col <= maxColIndex ; col++) {
            double layoutX = xOffset.get() % tileWidth + (col+tileXOffset.get()) * tileWidth ;

            for (int row = minRowIndex ; row <= maxRowIndex ; row++) {
                double layoutY = yOffset.get() % tileHeight + (row+tileYOffset.get()) * tileHeight ;

                GridLocation loc = new GridLocation(col, row);
                Tile tile = tileMap.computeIfAbsent(loc, location -> {
                    Tile t ;
                    if (availableTiles.isEmpty()) {
                        t = createTile(loc.getColumn(), loc.getRow());
                    } else {
                        t = availableTiles.iterator().next();
                        availableTiles.remove(t);
                        t.setLocation(loc.getColumn(), loc.getRow());
                    }
                    getChildren().add(t.getView());
                    return t ;
                });

                tile.getView().setLayoutX(layoutX);
                tile.getView().setLayoutY(layoutY);
            }
        }

        // prune available tiles:
        int maxAvailableToKeep = maxColIndex - minColIndex + maxRowIndex - minRowIndex + 3 ;
        int toRemove = availableTiles.size() - maxAvailableToKeep ;
        int removed = 0 ;
        for (Iterator<Tile> it = availableTiles.iterator() ; it.hasNext() && removed < toRemove ; removed++) {
            it.next(); it.remove();
        }
    }


    private Tile createTile(int col, int row) {
        return new Tile(
                col - tileXOffset.get(), row - tileYOffset.get(),
                this.tileWidth, this.tileHeight,
                imageSupplier);

    }

    private void removeTile(Tile tile) {
        getChildren().remove(tile.getView());
    }

    /**
     * Move the tile is specified by the given tile coordinates
     * to the specified position in the pane.
     * @param tileX
     * @param tileY
     */
    public void moveTo(int tileX, int tileY, Pos pos) {

        int xPos ;
        switch (pos.getHpos()) {
            case LEFT: xPos = 0 ; break;
            case CENTER: xPos = 1 ; break ;
            case RIGHT: xPos = 2 ; break ;
            default: xPos = 0 ;
        }
        int yPos ;
        switch (pos.getVpos()) {
            case TOP: yPos = 0 ; break ;
            case CENTER: yPos = 1 ; break ;
            case BASELINE:
            case BOTTOM: yPos = 2 ;  break ;
            default: yPos = 0 ;
        }

        xOffset.set(tileX * -tileWidth + (getWidth() - tileWidth) * xPos / 2);
        yOffset.set(tileY * -tileHeight + (getHeight() - tileHeight) * yPos / 2);
    }



    private static class GridLocation {
        private final int column ;
        private final int row ;
        // cache hashcode as we know we are using these in hashmaps
        private final int hash ;
        public GridLocation(int column, int row) {
            super();
            this.column = column;
            this.row = row;
            this.hash = 31 * (column + 31 * row);
        }

        public int getColumn() {
            return column;
        }

        public int getRow() {
            return row;
        }

        @Override
        public boolean equals(Object obj) {
            if (! (obj instanceof GridLocation other)) {
                return false ;
            }
            return other.column == column && other.row == row ;
        }

        @Override
        public int hashCode() {
            return hash ;
        }

        public boolean inRangeInclusive(int minCol, int maxCol, int minRow, int maxRow) {
            return column >= minCol && column <= maxCol
                    && row >= minRow && row <= maxRow ;
        }
    }

    /**
     *
     * A function that maps the location to an image.
     * The Tile class will call this function in a background thread,
     * throwing InterruptedExceptions will allow the Tile class
     * to cancel invocations.
     *
     */
    @FunctionalInterface
    public interface ImageSupplier {
        Image getImage(AtomicInteger x, AtomicInteger y) throws InterruptedException;
    }
}
