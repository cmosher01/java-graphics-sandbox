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
import nu.mine.mosher.zoom.swinglayer.ZoomPan;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.*;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType"})
class Status {
    private static final String TIMES = "\u00d7";
    private static final Point2D.Double NAN = new Point2D.Double(Double.NaN, Double.NaN);

    private final ZoomPan zp;
    private final MyPanel pane;

    private Point2D.Double cnv_mouseEvntPrev = NAN;

    public Status(ZoomPan zp, MyPanel pane) {
        this.zp = zp;
        this.pane = pane;
    }

    public String set() {
        return set(Optional.empty());
    }

    public String set(final Point mouse) {
        return set(Optional.of(new Point2D.Double(mouse.getX(), mouse.getY())));
    }

    private static final double MOUSE_INFO_DY = +1.0D;
    private static final double MOUSE_PANE_DY = +1.0D;

    // gets the current mouse position in canvas coordinates
    private Point2D.Double mouse(final Optional<Point2D.Double> opt_pan_mouseEvnt) {
        Optional<Point2D.Double> opt_cnv_mouseEvnt = Optional.empty();
        if (opt_pan_mouseEvnt.isPresent()) {
            this.cnv_mouseEvntPrev = this.zp.viewportToCanvas(opt_pan_mouseEvnt.get());
            opt_cnv_mouseEvnt = Optional.of(this.cnv_mouseEvntPrev);
        }

        Optional<Point2D.Double> opt_cnv_mouseInfo = Optional.empty();
        {
            val pi = MouseInfo.getPointerInfo();
            if (Objects.nonNull(pi)) {
                val pan_mouseInfoRaw = pi.getLocation();
                if (Objects.nonNull(pan_mouseInfoRaw)) {
                    SwingUtilities.convertPointFromScreen(pan_mouseInfoRaw, this.pane);
                    val pan_mouseInfo = new Point2D.Double(pan_mouseInfoRaw.getX(), pan_mouseInfoRaw.getY()+MOUSE_INFO_DY);
                    opt_cnv_mouseInfo = Optional.of(this.zp.viewportToCanvas(pan_mouseInfo));
                }
            }
        }

        Optional<Point2D.Double> opt_cnv_mousePane = Optional.empty();
        {
            val pan_mousePaneRaw = this.pane.getMousePosition();
            if (Objects.nonNull(pan_mousePaneRaw)) {
                val pan_mousePane = new Point2D.Double(pan_mousePaneRaw.getX(), pan_mousePaneRaw.getY()+MOUSE_PANE_DY);
                opt_cnv_mousePane = Optional.of(this.zp.viewportToCanvas(pan_mousePane));
            }
        }

        return opt_cnv_mouseEvnt.orElse(opt_cnv_mouseInfo.orElse(opt_cnv_mousePane.orElse(cnv_mouseEvntPrev)));
    }

    public String set(final Optional<Point2D.Double> opt_pan_mouseEvnt) {
        val mouse = mouse(opt_pan_mouseEvnt);
        val clip = this.pane.clip();
        return String.format("zoom=%08.4f%s window=(%.1f,%.1f)[%.1f%s%.1f] mouse=(%.3f,%.3f)",
            this.zp.zoomFactor(), TIMES,
            clip.getX(), clip.getY(), clip.getWidth(), TIMES, clip.getHeight(),
            mouse.getX(), mouse.getY());
    }

    // will we ever need this?
    public boolean inBounds() {
        // TODO check for nulls; use doubles
        val siz = this.pane.getSize();
        val loc = this.pane.getLocationOnScreen();
        val vp = new Rectangle(loc.x, loc.y, siz.width, siz.height);
        val mousePosition = MouseInfo.getPointerInfo().getLocation();
        return vp.contains(mousePosition);
    }
}
