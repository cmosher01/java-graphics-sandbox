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

import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

// StackPane, so it centers its content (i.e., its child Group of Nodes)
public class CanvasExample extends Pane {
    public CanvasExample() {
        super.getChildren().add(content);
    }

    private static Group content = new Group(createStar(), createCurve(), createCircle());

    private static Circle createCircle() {
        final var circle = new Circle(307D, 167D, 97D, Color.FIREBRICK);
        return circle;
    }

    private static SVGPath createCurve() {
        final var ellipticalArc = new SVGPath();
        ellipticalArc.setContent("M10,150 A15 15 180 0 1 70 140 A15 25 180 0 0 130 130 A15 55 180 0 1 190 120");
        ellipticalArc.setStroke(Color.LIGHTGREEN);
        ellipticalArc.setStrokeWidth(4);
        ellipticalArc.setFill(null);
        return ellipticalArc;
    }

    private static SVGPath createStar() {
        final var star = new SVGPath();
        star.setContent("M100,10 L100,10 40,180 190,60 10,60 160,180 z");
        star.setStrokeLineJoin(StrokeLineJoin.ROUND);
        star.setStroke(Color.BLUE);
        star.setFill(Color.DARKBLUE);
        star.setStrokeWidth(4);
        return star;
    }

    // TODO create a (dynamic?) grid to show local coordinate system
    // maybe use this: https://github.com/FXyz/FXyz/blob/master/FXyz-Core/src/main/java/org/fxyz3d/scene/CubeWorld.java
}
