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

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents an image "Tile" in a two-dimensional array, with
 * support for background loading of the images. Images are created
 * via a user-supplied function, which is executed in a background thread.
 *
 */
class Tile {

    private final AtomicInteger x = new AtomicInteger();
    private final AtomicInteger y = new AtomicInteger();
    private final ImageView view ;
    private final Service<Image> imageService ;

    /**
     *
     * Creates a new tile, with the given location and (fixed) dimensions. The
     * imageSupplier will be invoked in a background thread and the resulting image
     * displayed in the view on successful completion.
     *
     * @param x Initial x-coordinate of tile.
     * @param y Initial y-coordinate of tile.
     * @param width Width of tile in pixels (fixed).
     * @param height Height of tile in pixels (fixed).
     * @param imageSupplier Function for creating images, which will be invoked on a background thread.
     *
     */
    public Tile(int x, int y, double width, double height, PanningTiledPane.ImageSupplier imageSupplier) {
        this.x.set(x);
        this.y.set(y);
        this.view = new ImageView();
        view.setFitWidth(width);
        view.setFitHeight(height);


        this.imageService = new Service<Image>() {
            @Override
            protected Task<Image> createTask() {
                return new Task<Image>() {
                    @Override
                    protected Image call() throws Exception {
                        return imageSupplier.getImage(Tile.this.x, Tile.this.y);
                    }
                };
            }
        };
        this.imageService.stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                view.setImage(imageService.getValue());
            } else {
                view.setImage(null);
            }
        });
        imageService.start();
    }


    /**
     * Sets the location and updates the image with an existing image.
     * @param x The new x-coordinate in the grid.
     * @param y The new y-coordinate in the grid.
     * @param image The new image. If this is null, a new image will be loaded in the background.
     */
    public void setLocationAndImage(int x, int y, Image image) {
        this.x.set(x);
        this.y.set(y);
        if (image == null) {
            imageService.restart();
        } else {
            imageService.cancel();
            view.setImage(image);
        }
    }

    /**
     * Sets the location and loads a new image in the background.
     * @param x
     * @param y
     */
    public void setLocation(int x, int y) {
        setLocationAndImage(x, y, null);
    }

    /**
     *
     * @return The current image.
     */
//    public Image getImage() {
//        return view.getImage();
//    }

    /**
     *
     * @return The view for this tile.
     */
    public Node getView() {
        return view ;
    }

    /**
     *
     * @return The current x-coordinate.
     */
//    public int getX() {
//        return x ;
//    }

    /**
     *
     * @return The current y-coordinate.
     */
//    public int getY() {
//        return y ;
//    }
}
