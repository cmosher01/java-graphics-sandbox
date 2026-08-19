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

package nu.mine.mosher.zoom.math;

import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.*;

import java.util.LinkedList;

import static nu.mine.mosher.zoom.math.JavaFxUtil.*;

public class GenericZoomTestJavaFxApp extends Application {
    private static final String TITLE = "Zoom Test Application";

    public static void main(final String... args) {
        Application.launch(args);
    }

    @Override
    public void start(final Stage stage) {
        final var scene = new Scene(createSceneRoot());

        setWindowLocation(stage, getInitialStageLocation());
        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.show();
    }

    private static Pane createSceneRoot() {
        final var mb = createMenuBar();
        final var sb = createStatusBar();
        final var vp = createViewPort(sb);

        final var layout = new BorderPane();
        layout.setCenter(vp);
        layout.setTop(mb);
        layout.setBottom(sb);

        return layout;
    }

    private static StatusBar createStatusBar() {
        return new StatusBar();
    }

    private static Node createViewPort(final StatusBar sb) {
        final var canvas = new CanvasExample();
//        canvas.setOnMouseMoved(e -> sb.updateCanvas(new Point2D(e.getX(), e.getY())));
        // TODO for identification:
//        canvas.setBackground(Background.fill(Color.DARKGREEN));
//        canvas.setBorder(new Border(new BorderStroke(
//                Color.GREEN,
//                BorderStrokeStyle.DASHED,
//                CornerRadii.EMPTY,
//                new BorderWidths(3D)
//        )));

        final var scroller = scrollerFor(canvas);

        final var viewport = new StackPane(scroller);
        viewport.setMinSize(0,0);
        viewport.setOnMouseMoved(e -> {
            sb.updateViewPort(new Point2D(e.getX(), e.getY()));
            sb.updateVpToCv(canvas.parentToLocal(new Point2D(e.getX(), e.getY())));
        });
        viewport.setOnMouseDragged(e -> {
            sb.updateViewPort(new Point2D(e.getX(), e.getY()));
            sb.updateVpToCv(canvas.parentToLocal(new Point2D(e.getX(), e.getY())));
        });
        // TODO for identification:
//        viewport.setBackground(Background.fill(Color.DARKRED));
//        viewport.setBorder(new Border(new BorderStroke(
//                Color.RED,
//                BorderStrokeStyle.DASHED,
//                CornerRadii.EMPTY,
//                new BorderWidths(3D)
//        )));
        clipToChildren(viewport);

        return viewport;
    }


    private static void clipToChildren(final Pane pane) {
        final var clip = new Rectangle();
        clip.widthProperty().bind(pane.widthProperty());
        clip.heightProperty().bind(pane.heightProperty());
        pane.setClip(clip);
    }

    private static final double FACTOR_ZOOM_IN = 1.04D;
    private static final double FACTOR_ZOOM_OUT = 2.0D-FACTOR_ZOOM_IN;
    private static final double CANVAS_MIN_SIZE = 20.0D;
    private static final double MAX_SCALE = 1.0e2D;

    private static Scroller scrollerFor(final Pane canvas) {
        return Scroller.create(canvas);
    }

    private static Pane scrollerForXXX(final Pane canvas) {
        final var scroller = new Pane(canvas);



        // translate on dragged
        final var offset = new LinkedList<Point2D>();
        scroller.setOnMousePressed(t -> {
            t.consume();
            final var ptMouse = new Point2D(t.getSceneX(), t.getSceneY());
            final var ptCanvas = new Point2D(canvas.getLayoutX(), canvas.getLayoutY());
            final var dptOffset = ptMouse.subtract(ptCanvas);
            offset.clear();
            offset.offer(dptOffset);
        });
        scroller.setOnMouseDragged(t -> {
            final var ptMouse = new Point2D(t.getSceneX(), t.getSceneY());
            final var dptOffset = offset.peek();
            final var delta = ptMouse.subtract(dptOffset);
            canvas.setLayoutX(delta.getX());
            canvas.setLayoutY(delta.getY());
        });
        scroller.setOnMouseReleased(t -> {
            t.consume();
            offset.clear();;
        });



        // scale on scrolled
        scroller.setOnScroll(t -> {
            t.consume();

            /*
                How far the user scrolled (panned, moved mouse wheel, etc.).

                Positive values for scrolled "away" from user, or up,
                which mean "zoom in" (larger scale, larger canvas).

                Negative values for scrolled "towards" user, or down,
                which mean "zoom out" (smaller scale, smaller canvas).
             */
            final var dy = t.getDeltaY();
            if (Math.abs(dy) < 0.01D) {
                return;
            }

            final var zoomOut = dy < 0D;
            final var zoomIn = !zoomOut;

            final double z;
            if (zoomIn) {
                z = FACTOR_ZOOM_IN;
            } else {
                z = FACTOR_ZOOM_OUT;
            }


            // calculate width and height of canvas in viewport coordinates
            // to see how small it is. If either dimension is less than 20,
            // prevent zoom in.
            // Clamp the scale to prevent extreme zooming
            final var sz = canvas.getBoundsInParent();
            if (zoomOut && (sz.getWidth() <= CANVAS_MIN_SIZE || sz.getHeight() <= CANVAS_MIN_SIZE)) {
                return;
            }

            final double s = canvas.getScaleX();
            if (zoomIn && MAX_SCALE <= s) {
                return;
            }

            final var p = pt(t.getSceneX(), t.getSceneY());
            final var l = pt(canvas.getLayoutX(), canvas.getLayoutY());
            final var f = pt(canvas.getWidth(), canvas.getHeight()).multiply(-0.5D);
            final var m = p.add(f);
            final var lp = m.subtract(m.subtract(l).multiply(z));
            canvas.setLayoutX(lp.getX());
            canvas.setLayoutY(lp.getY());

            final var sp = z*s;
            canvas.setScaleX(sp);
            canvas.setScaleY(sp);
        });


        return scroller;
    }

    private static Point2D pt(final double x, final double y) {
        return new Point2D(x,y);
    }

    private static MenuBar createMenuBar() {
        final var itemFileExit = new MenuItem("Exit");
        itemFileExit.setOnAction(e -> Platform.exit());

        final var menuFile = new Menu("File");
        menuFile.getItems().add(itemFileExit);

        final var mb = new MenuBar(menuFile);

        mb.setUseSystemMenuBar(true);
        return mb;
    }



    private static final double FRACT_SIZE_STAGE_OF_SCREEN = 0.8D;

    private static Rectangle2D getInitialStageLocation() {
        final var locScreen = Screen.getPrimary().getVisualBounds();
        return sizeRelative(locScreen, FRACT_SIZE_STAGE_OF_SCREEN);
    }








//    private static Group createCanvas() {
//        final Group canvas = new Group(new StackPane(new CanvasExample()));
//        canvas.setAutoSizeChildren(false);
//        return canvas;
//    }

//                final var inv = 1.0D - zoomFactor;
//                final var cScroller = new Point2D(
//                    scroller.getBoundsInLocal().getCenterX(),
//                    scroller.getBoundsInLocal().getCenterY());
//                final var ptMouse = canvas.parentToLocal(ptMouseScroller);
//                final var dOld = ptMouseScroller.subtract(cScroller);
//                final var dNew = dOld.multiply(zoomFactor);
//                final var layoutDeltaX = inv*ptMouseScroller.getX();
//                final var layoutNewX = layoutOldX+layoutDeltaX;
//                final var layoutDeltaY = inv*ptMouseScroller.getY();
//                final var layoutNewY = layoutOldY+layoutDeltaY;
//                    final var c = cScroller;
//                    final var d = m.subtract(c);
//                    final var dp = d.multiply(z);
//                    final var cp = m.add(dp.multiply(-1D));
//                    final var mp = cp.add(dp);
//                canvas.setLayoutX(layoutNewX);
//                canvas.setLayoutY(layoutNewY);
//                canvas.setScaleX(scaleNew);
//                canvas.setScaleY(scaleNew);
//                    System.out.printf("s=%.5f z=%.5f i=%.5f zp=%.5f sp=%.5f\n", s, z, 1D-z, 1D/z, sp);
//                    System.out.printf("c =(%9.3f,%9.3f)   l =(%9.3f,%9.3f)   d =(%9.3f,%9.3f)   m =(%9.3f,%9.3f)\n", c .getX(), c .getY(), l .getX(), l .getY(), d .getX(), d .getY(), m .getX(), m .getY());
//                    System.out.printf("cp=(%9.3f,%9.3f)   lp=(%9.3f,%9.3f)   dp=(%9.3f,%9.3f)   mp=(%9.3f,%9.3f)\n", cp.getX(), cp.getY(), lp.getX(), lp.getY(), dp.getX(), dp.getY(), mp.getX(), mp.getY());
//                    System.out.printf("-----------------------------------------------------------------------------------------\n");


//    private static Node createViewPort(final StatusBar sb) {
//        final var canvas = createCanvas();
//        canvas.setOnMouseMoved(e -> sb.updateCanvas(new Point2D(e.getX(), e.getY())));
//
//        final var viewport = new BorderPane(scrollerFor(canvas));
//        viewport.setOnMouseMoved(e -> sb.updateViewPort(new Point2D(e.getX(), e.getY())));
//
//        return viewport;
//    }



    //    private static void dumpSizes(final Region region, final String name) {
//        final var wMin = region.getMinWidth();
//        final var hMin = region.getMinHeight();
//        final var wPrf = region.getPrefWidth();
//        final var hPrf = region.getPrefHeight();
//        final var wMax = region.getMaxWidth();
//        final var hMax = region.getMaxHeight();
//        System.out.printf("min=(%5.1f,%5.1f) pref=(%5.1f,%5.1f) max=(%5.1f,%5.1f) %s\n",
//            wMin, hMin, wPrf, hPrf, wMax, hMax, name);
//    }
}
