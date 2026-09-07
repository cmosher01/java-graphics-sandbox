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

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
public final class StatusModel {
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    private static final String TIMES = "\u00d7";

    private static final Point2D.Double NAN = new Point2D.Double(Double.NaN, Double.NaN);
    private static final double MOUSE_INFO_DY = +1.0D;
    private static final double MOUSE_PANE_DY = +1.0D;



    private final ZoomPanModel zp;
    private final MainPane pane;

    private String s = " ";
    private Point2D.Double canvas_mouseEvntPrev = NAN;



    public StatusModel(final ZoomPanModel zp, final MainPane pane) {
        this.zp = zp;
        this.pane = pane;
    }



    public String get() {
        return this.s;
    }

    public void set() {
        set(Optional.empty());
    }

    public void set(final Point2D mouse) {
        set(Optional.of(new Point2D.Double(mouse.getX(), mouse.getY())));
    }



    private void set(final Optional<Point2D.Double> opt_vwport_mouseEvnt) {
        val mouse = mouse(opt_vwport_mouseEvnt);
        val clip = this.pane.clip();
        this.s = String.format("zoom=%012.8f%s window=(%.1f,%.1f)[%.1f%s%.1f] mouse=(%.2f,%.2f)",
            this.zp.zoomFactor(), TIMES,
            clip.getX(), clip.getY(), clip.getWidth(), TIMES, clip.getHeight(),
            mouse.getX(), mouse.getY());
    }

    // gets the current mouse position in canvas coordinates
    private Point2D.Double mouse(final Optional<Point2D.Double> opt_vwport_mouseEvnt) {
        Optional<Point2D.Double> opt_canvas_mouseEvnt = Optional.empty();
        if (opt_vwport_mouseEvnt.isPresent()) {
            this.canvas_mouseEvntPrev = this.zp.viewportToCanvas(opt_vwport_mouseEvnt.get());
            opt_canvas_mouseEvnt = Optional.of(this.canvas_mouseEvntPrev);
        }

        Optional<Point2D.Double> opt_canvas_mouseInfo = Optional.empty();
        {
            val pi = MouseInfo.getPointerInfo();
            if (Objects.nonNull(pi)) {
                val vwport_mouseInfoRaw = pi.getLocation();
                if (Objects.nonNull(vwport_mouseInfoRaw)) {
                    SwingUtilities.convertPointFromScreen(vwport_mouseInfoRaw, this.pane);
                    val vwport_mouseInfo = new Point2D.Double(vwport_mouseInfoRaw.getX(), vwport_mouseInfoRaw.getY()+MOUSE_INFO_DY);
                    opt_canvas_mouseInfo = Optional.of(this.zp.viewportToCanvas(vwport_mouseInfo));
                }
            }
        }

        Optional<Point2D.Double> opt_canvas_mousePane = Optional.empty();
        {
            val vwport_mousePaneRaw = this.pane.getMousePosition();
            if (Objects.nonNull(vwport_mousePaneRaw)) {
                val vwport_mousePane = new Point2D.Double(vwport_mousePaneRaw.getX(), vwport_mousePaneRaw.getY()+MOUSE_PANE_DY);
                opt_canvas_mousePane = Optional.of(this.zp.viewportToCanvas(vwport_mousePane));
            }
        }

        return opt_canvas_mouseEvnt.orElse(opt_canvas_mouseInfo.orElse(opt_canvas_mousePane.orElse(canvas_mouseEvntPrev)));
    }

    // will we ever need this?
//    public boolean inBounds() {
//        // TODO check for nulls; use doubles
//        val siz = this.pane.getSize();
//        val loc = this.pane.getLocationOnScreen();
//        val vp = new Rectangle(loc.x, loc.y, siz.width, siz.height);
//        val mousePosition = MouseInfo.getPointerInfo().getLocation();
//        return vp.contains(mousePosition);
//    }
}
