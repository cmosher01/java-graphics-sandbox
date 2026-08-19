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


import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/*
Taken from github by me, Christopher Alan Mosher, New York. [CAM]




https://gist.github.com/james-d/a249470377fb3c58784a9349a22641c4

Example of (essentially) infinite panning in JavaFX using a fixed set of ImageViews and updating
their images on panning. Prototype for Google Maps type of interface.



igoriuz commented on Jul 16, 2018

Thank you very much for sharing this. Unfortunately i have some problems with this implementation:

If you resize the window a few times, some tiles are misplaced (in regards to the coordinates) or
even exist twice at wrong positions. Do you have any idea how it is possible to fix it? It has to
do something with the behavior during resizing because if you move in the pane back and forth to
refresh the broken tiles then everything is at its correct position. [

In this example you forgot to add a Pos enum to the moveTo method. [CAM: FIXED]

The moveTo method does not calculate the correct position because both tileX and tileY should be
multiplied with negative tileWidth and tileHeight [CAM: FIXED]

 */
public class PanningTilesExample extends Application {

    private static final int TILE_WIDTH = 100;
    private static final int TILE_HEIGHT = 100;

    private final Random rng = new Random();

    @Override
    public void start(Stage primaryStage) {
        PanningTiledPane tiledPane = new PanningTiledPane(TILE_WIDTH, TILE_HEIGHT, this::getImage);

        tiledPane.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                tiledPane.moveTo(0, 0, Pos.CENTER);
            }
        });

        Scene scene = new Scene(new StackPane(tiledPane), 800, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }



    // get a new image for tile represented by column, row
    // this implementation just snapshots a label, but this could be
    // retrieved from a file, server, or database, etc
    private Image getImage(AtomicInteger column, AtomicInteger row) throws InterruptedException {

        // simulate slow loading from database, etc:
        Random rng = new Random();
        Thread.sleep(rng.nextInt(1000));

        // little hack to create image from snapshot and return it when we're running
        // on background thread (snapshot must be called from FX Application Thread):

        FutureTask<Image> runOnFXThread = new FutureTask<>(() -> {
            Label label = new Label(String.format("Tile [%d,%d]", column.get(), row.get()));
            label.setPrefSize(TILE_WIDTH, TILE_HEIGHT);
            label.setMaxSize(TILE_WIDTH, TILE_HEIGHT);
            label.setAlignment(Pos.CENTER);
            label.setStyle(String.format("-fx-background: %s; "
                            + "-fx-background-color: -fx-background;",
                    randomColorString()));

            // must add label to a scene for background to work:
            new Scene(label);
            return label.snapshot(null, null);
        });
        Platform.runLater(runOnFXThread);
        try {
            return runOnFXThread.get() ;
        } catch (ExecutionException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Error creating image", e);
            return null ;
        }
    }

    private String randomColorString() {
        return String.format("#%02x%02x%02x", rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
