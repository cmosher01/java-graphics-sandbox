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

package nu.mine.mosher.zoom.javafx;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.transform.*;
import javafx.stage.*;

import java.awt.*;

public class GraphicsScalingApp extends Application {
    private static final double SCALE_DELTA = 0.005D;



    public static void main(final String... args) {
        Application.launch(args);
    }






    @Override
    public void start(final Stage stage) {
        final Group canvas = new Group(createStar(), createCurve());
        canvas.setAutoSizeChildren(false);

        final Node scroller = createScrollerFor(canvas);

        final MenuBar menubar = createMenuBar(stage, canvas);

        final BorderPane layout = new BorderPane(scroller, menubar, null, null, null);

        final Scene scene = new Scene(layout);

        stage.setTitle("Zoomy");
        stage.setScene(scene);
        stage.show();
    }



    /**
     *  question: https://stackoverflow.com/questions/29506156/javafx-8-zooming-relative-to-mouse-pointer
     *  answer: https://stackoverflow.com/a/58768816/3033324
     *  by: Ionut Lucian Nicolescu, https://stackoverflow.com/users/8639258/nicolescu-ionut-lucian
     *  Here for illustration only. Incorporated these ideas into the main code.
     * @param pane
     */
    private static void zoomIn(final Pane pane) {
        final var YOUR_ZOOM_FACTOR_VALUE = 1.5D;
        final var newScale = new Scale();
        newScale.setX(pane.getScaleX() + YOUR_ZOOM_FACTOR_VALUE);
        newScale.setY(pane.getScaleY() + YOUR_ZOOM_FACTOR_VALUE);
        newScale.setPivotX(pane.getScaleX());
        newScale.setPivotY(pane.getScaleY());
        pane.getTransforms().add(newScale);
    }



    private static Node createScrollerFor(final Group scrollable) {
//        final StackPane zoomPane = new StackPane(scrollable);


//        final var scrollContent = new Group(zoomPane);
//        final var scroller = new ScrollPane(scrollContent);

        final var scroller = new BorderPane(scrollable);

        final var zoomPane = scroller;
        // for debugging, so we can see where the zoomPane is
        zoomPane.setBorder(new Border(new BorderStroke(
                Color.DARKSLATEBLUE,
                BorderStrokeStyle.DASHED,
                CornerRadii.EMPTY,
                new BorderWidths(1.0D)
        )));



//        scroller.setPrefViewportWidth(256.0D);
//        scroller.setPrefViewportHeight(256.0D);
//        scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
//        scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
//        scroller.viewportBoundsProperty().addListener((observable, oldBounds, newBounds) ->
//            zoomPane.setMinSize(newBounds.getWidth(), newBounds.getHeight()));




        {
            final var scaler = new Scale();
            final var xlater = new Translate();
            scrollable.getTransforms().addAll(scaler, xlater);

            // dump mouse coords, for debugging
            zoomPane.setOnMouseMoved((MouseEvent t) -> {
                // since event handler is on the zoomPane object,
                // the (x,y) coordinates of the event will be in
                // the zoomPane's coordinate system.
                final var zoompane_xyMouse = new Point2D(t.getX(), t.getY());
                dumpPoint(zoompane_xyMouse, "move (event coords)");
                // convert zoomPane coords to scrollable coords
//                final var canvas_xyMouse = canvasMouse(scrollable, t);
//                dumpPoint(canvas_xyMouse, "move");
            });


            zoomPane.setOnScroll(event -> {
                event.consume();

//                final var mouse = new Point2D(event.getX(), event.getY()); // scroller coords
                final var mouse = canvasMouse(scrollable, event); // scrollable coords
                if (event.getDeltaY() == 0.0D) { // TODO
                    return;
                }


                // note: event.getDeltaY() is like 1 or 2 for slow zoom in,
                // or -200 for fastest zoom out
                final double zoom = Math.exp(SCALE_DELTA * event.getDeltaY());
                final double scaleOld = scaler.getX();
                final double scaleNew = zoom * scaleOld;
                if (scaleNew < 1e-1D || 1e+1D < scaleNew) {
                    // limit of zoom in/out
                    System.out.println("hit min/max zoom");
                    return;
                }

                if (Math.abs(scaleNew-scaleOld) < 1e-5D) {
                    System.out.println("too small change in zoom factor, ignoring");
                    return;
                }

                if (scaleNew < scaleOld) {
                    dumpPoint(mouse, "zooming out (smaller scale)");
                } else {
                    dumpPoint(mouse, "zooming  in (larger  scale)");
                }

                System.out.printf("scale dy= %7.3f, factor=%7.3f, old=%7.3f, new=%7.3f\n", event.getDeltaY(), zoom, scaleOld, scaleNew);
                dumpPoint(mouse, "(after zoom)");

                // amount of scrolling in each direction in scrollContent coordinate units
//            final Point2D scrollOffset = figureScrollOffset(scrollContent, scroller);

///////////////////////////////////////////////////////
//                scrollable.setScaleX(scrollable.getScaleX() * scaleFactor);
//                scrollable.setScaleY(scrollable.getScaleY() * scaleFactor);
///////////
                scaler.setX(scaleNew);
                scaler.setY(scaleNew);
//                final var f = (scaleNew/scaleOld)-1.0D;
//                final var pivot = calcPivot(scrollable, mouse);//.multiply(f);
                scaler.setPivotX(mouse.getX());
                scaler.setPivotY(mouse.getY());

                Point mouseNewScreen = java.awt.MouseInfo.getPointerInfo().getLocation();
                final var mouseNew = scrollable.screenToLocal(mouseNewScreen.x, mouseNewScreen.y);
                dumpPoint(mouseNew, "(from mouse)");
                System.out.println("---------------------------------");

///////////////////////////////////////////////////////
                // move viewport so that old center remains in the center after the
                // scaling
//            repositionScroller(scrollContent, scroller, scaleFactor, scrollOffset);

            });




        // Panning via drag....
//        final ObjectProperty<Point2D> lastMouseCoordinates = new SimpleObjectProperty<>();
//        scrollContent.setOnMousePressed(event ->
//            lastMouseCoordinates.set(new Point2D(event.getX(), event.getY())));
//
//        scrollContent.setOnMouseDragged(event -> {
//            double deltaX = event.getX() - lastMouseCoordinates.get().getX();
//            double extraWidth = scroller.getContent().getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
//            double deltaH = deltaX * (scroller.getHmax() - scroller.getHmin()) / extraWidth;
//            double desiredH = safeGetH(scroller) - deltaH;
//            scroller.setHvalue(Math.max(0, Math.min(scroller.getHmax(), desiredH)));
//
//            double deltaY = event.getY() - lastMouseCoordinates.get().getY();
//            double extraHeight = scroller.getContent().getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
//            double deltaV = deltaY * (scroller.getVmax() - scroller.getVmin()) / extraHeight;
//            double desiredV = safeGetV(scroller) - deltaV;
//            scroller.setVvalue(Math.max(0, Math.min(scroller.getVmax(), desiredV)));
//        });

            final var propMouseOld = new SimpleObjectProperty<Point2D>();
            final var propTranslateOld = new SimpleObjectProperty<Translate>();
            zoomPane.setOnMousePressed(t -> {
                propTranslateOld.set(xlater);
//                final var mouse = canvasMouse(scrollable, t);
                final var mouse = new Point2D(t.getX(), t.getY());
                dumpPoint(mouse, "pressed (event coords)");
//                final var mouseNew = inv(propTranslateOld.get(), mouse);
//                dumpPoint(mouse, "        (xlated)");
                propMouseOld.set(mouse);
            });
            zoomPane.setOnMouseDragged(t -> {
//                final var mouseNew = new Point2D(t.getX(), t.getY());
//                dumpPoint(mouseNew, "drag");
//                final var mouseOld = propMouseOld.get();
//                propMouseOld.set(mouseNew);
//                final var mouseDelta = mouseNew.subtract(mouseOld);
//                final var xlaterOldX = xlater.getX();
//                final var xlaterOldY = xlater.getY();
//                xlater.setX(xlaterOldX + mouseDelta.getX());
//                xlater.setY(xlaterOldY + mouseDelta.getY());
                final var mouseNew = new Point2D(t.getX(), t.getY());
                dumpPoint(mouseNew, "dragged (event coords)");
//                final var mouseNew = inv(propTranslateOld.get(), mouse);
//                dumpPoint(mouse, "        (xlated)");
                final var delta = mouseNew.subtract(propMouseOld.get());
                propMouseOld.set(mouseNew);
                xlater.setX(xlater.getX() + delta.getX());
                xlater.setY(xlater.getY() + delta.getY());
//                xlater.setX(delta.getX());
//                xlater.setY(delta.getY());
            });
        }


        return scroller;
    }

    private static Point2D inv(final Transform xf, final Point2D pt) {
        try {
            return xf.inverseTransform(pt);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }
//    private static Point2D figureScrollOffset(Node scrollContent, ScrollPane scroller) {
//        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
//        double hScrollProportion = (safeGetH(scroller) - scroller.getHmin()) / (scroller.getHmax() - scroller.getHmin());
//        double scrollXOffset = hScrollProportion * Math.max(0, extraWidth);
//        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
//        double vScrollProportion = (safeGetV(scroller) - scroller.getVmin()) / (scroller.getVmax() - scroller.getVmin());
//        double scrollYOffset = vScrollProportion * Math.max(0, extraHeight);
//        return new Point2D(scrollXOffset, scrollYOffset);
//    }
//
//    private static void repositionScroller(Node scrollContent, ScrollPane scroller, double scaleFactor, Point2D scrollOffset) {
//        double scrollXOffset = scrollOffset.getX();
//        double scrollYOffset = scrollOffset.getY();
//        double extraWidth = scrollContent.getLayoutBounds().getWidth() - scroller.getViewportBounds().getWidth();
//        if (extraWidth > 0) {
//            double halfWidth = scroller.getViewportBounds().getWidth() / 2 ;
//            double newScrollXOffset = (scaleFactor - 1) *  halfWidth + scaleFactor * scrollXOffset;
//            scroller.setHvalue(scroller.getHmin() + newScrollXOffset * (scroller.getHmax() - scroller.getHmin()) / extraWidth);
//        } else {
//            scroller.setHvalue(scroller.getHmin());
//        }
//        double extraHeight = scrollContent.getLayoutBounds().getHeight() - scroller.getViewportBounds().getHeight();
//        if (extraHeight > 0) {
//            double halfHeight = scroller.getViewportBounds().getHeight() / 2 ;
//            double newScrollYOffset = (scaleFactor - 1) * halfHeight + scaleFactor * scrollYOffset;
//            scroller.setVvalue(scroller.getVmin() + newScrollYOffset * (scroller.getVmax() - scroller.getVmin()) / extraHeight);
//        } else {
//            scroller.setHvalue(scroller.getHmin());
//        }
//    }



    private static Point2D canvasMouse(final Group scrollable, final MouseEvent event) {
        final var mouse = new Point2D(event.getX(), event.getY());
        return canvasMouse(scrollable, mouse);
    }
    private static Point2D canvasMouse(final Group scrollable, final GestureEvent event) {
        final var mouse = new Point2D(event.getX(), event.getY());
        return canvasMouse(scrollable, mouse);
    }
    private static Point2D canvasMouse(final Group scrollable, final Point2D mouse) {
        return scrollable.parentToLocal(mouse);
    }

    //    private static Point2D calcPivot(final Group canvas, final Point2D mouse) {
//        //maxX = right overhang, maxY = lower overhang
//        double maxX = canvas.getBoundsInParent().getMaxX() - canvas.localToParent(canvas.prefWidth(0), canvas.prefHeight(0)).getX();
//        double maxY = canvas.getBoundsInParent().getMaxY() - canvas.localToParent(canvas.prefHeight(0), canvas.prefHeight(0)).getY();
//
//        // minX = left overhang, minY = upper overhang
//        double minX = canvas.localToParent(0,0).getX() - canvas.getBoundsInParent().getMinX();
//        double minY = canvas.localToParent(0,0).getY() - canvas.getBoundsInParent().getMinY();
//
//        // adding the overhangs together, as we only consider the width of canvas itself
//        double subX = maxX + minX;
//        double subY = maxY + minY;
//
//        // subtracting the overall overhang from the width and only the left and upper overhang from the upper left point
//        double dx = (mouse.getX() - ((canvas.getBoundsInParent().getWidth()-subX)/2 + (canvas.getBoundsInParent().getMinX()+minX)));
//        double dy = (mouse.getY() - ((canvas.getBoundsInParent().getHeight()-subY)/2 + (canvas.getBoundsInParent().getMinY()+minY)));
//
//        return new Point2D(dx,dy);
//    }






    private static SVGPath createCurve() {
        SVGPath ellipticalArc = new SVGPath();
        ellipticalArc.setContent("M10,150 A15 15 180 0 1 70 140 A15 25 180 0 0 130 130 A15 55 180 0 1 190 120");
        ellipticalArc.setStroke(Color.LIGHTGREEN);
        ellipticalArc.setStrokeWidth(4);
        ellipticalArc.setFill(null);
        return ellipticalArc;
    }

    private static SVGPath createStar() {
        SVGPath star = new SVGPath();
        star.setContent("M100,10 L100,10 40,180 190,60 10,60 160,180 z");
        star.setStrokeLineJoin(StrokeLineJoin.ROUND);
        star.setStroke(Color.BLUE);
        star.setFill(Color.DARKBLUE);
        star.setStrokeWidth(4);
        return star;
    }

    private static MenuBar createMenuBar(final Stage stage, final Group scrollable) {
        Menu fileMenu = new Menu("_File");
        MenuItem exitMenuItem = new MenuItem("E_xit");
        exitMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
            }
        });
        fileMenu.getItems().setAll(exitMenuItem);



//        Menu zoomMenu = new Menu("_Zoom");
//        MenuItem zoomResetMenuItem = new MenuItem("Zoom _Reset");
//        zoomResetMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.ESCAPE));
//        /*
//        Note from Christopher Alan Mosher: I probably won't have menu items for
//        these actions, but if I do, then make sure not call methods on the
//        scrollable group itself, but go through the same processing algorithm
//        used for the OnScroll handler.
//         */
//        zoomResetMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                scrollable.setScaleX(1);
//                scrollable.setScaleY(1);
//            }
//        });
//        MenuItem zoomInMenuItem = new MenuItem("Zoom _In");
//        zoomInMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.I));
//        zoomInMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                scrollable.setScaleX(scrollable.getScaleX() * 1.5);
//                scrollable.setScaleY(scrollable.getScaleY() * 1.5);
//            }
//        });
//        MenuItem zoomOutMenuItem = new MenuItem("Zoom _Out");
//        zoomOutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O));
//        zoomOutMenuItem.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                scrollable.setScaleX(scrollable.getScaleX() * 1 / 1.5);
//                scrollable.setScaleY(scrollable.getScaleY() * 1 / 1.5);
//            }
//        });
//        zoomMenu.getItems().setAll(zoomResetMenuItem, zoomInMenuItem, zoomOutMenuItem);



        return new MenuBar(fileMenu/*, zoomMenu*/);
    }







    private static double safeGetH(final ScrollPane scroller) {
        var h = scroller.getHvalue();
        if (Double.isNaN(h) || Double.isInfinite(h)) {
            h = 0.0D;
        }
        return h;
    }

    private static double safeGetV(final ScrollPane scroller) {
        var v = scroller.getVvalue();
        if (Double.isNaN(v) || Double.isInfinite(v)) {
            v = 0.0D;
        }
        return v;
    }



    private static void dumpPoint(final Point2D pt, final String move) {
//        final var boundsView = calcView(); //view of the scrolling region (canvas/chart) visible via the viewport, in _XY coordinates
//        final var ptViewCenter = new Point2D(boundsView.getCenterX(), boundsView.getCenterY());
//        final var posMouseOffCenter = pt.subtract(ptViewCenter);

        System.out.printf(" point ( x, y) = (%6.1f,%6.1f) %s\n", pt.getX(), pt.getY(), move);
//        System.out.printf("center ( x, y) = (%6.1f,%6.1f)\n", ptViewCenter.getX(), ptViewCenter.getY());
//        System.out.printf("offset (dx,dy) = (%6.1f,%6.1f)\n", posMouseOffCenter.getX(), posMouseOffCenter.getY());
//        System.out.printf("scrollbar(h,v) = (%5.3f,%5.3f)\n", getHvalue(), getVvalue());
//        System.out.printf("scale          = %10.6f\n", this.scale);
//        System.out.println("----------");
    }
}
