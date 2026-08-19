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

import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Font;

public class StatusBar extends Pane {
    private final Label labelMouseViewport = new Label("window=()");
    private final Label labelMouseVpToCv = new Label("canvas=()");

    public StatusBar() {
        this.labelMouseViewport.setPadding(new Insets(5.0D));
        this.labelMouseViewport.setFont(Font.font("monospace"));
        this.labelMouseVpToCv.setPadding(new Insets(5.0D));
        this.labelMouseVpToCv.setFont(Font.font("monospace"));
        final var layout = new HBox(this.labelMouseViewport, this.labelMouseVpToCv);
        super.getChildren().add(layout);
    }

    public void updateViewPort(final Point2D coords) {
        this.labelMouseViewport.setText(displayCoords("window", coords));
    }

    public void updateVpToCv(final Point2D coords) {
        this.labelMouseVpToCv.setText(displayCoords("canvas", coords));
    }



    private static String displayCoords(String name6chars, Point2D coords) {
        return String.format("  %6s=(%7.1f,%7.1f)", name6chars, coords.getX(), coords.getY());
    }
}
