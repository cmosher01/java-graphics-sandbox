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

package nu.mine.mosher.zoom.swing;

import javafx.scene.transform.Scale;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.geom.Point2D;

public class ScaleHandler implements ChangeListener, MouseWheelListener {
    private final PanAndZoomCanvas canvas;

//    private final Scale scaler = new Scale();

    public ScaleHandler(final PanAndZoomCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void stateChanged(final ChangeEvent e) {
//        final var slider = (JSlider)e.getSource();
//        final var zoomPercent = slider.getValue();
//        // make sure zoom never gets to actual 0, or else the objects will
//        // disappear and the matrix will be non-invertible.
//        this.canvas.scale(Math.max(1.0e-2D, zoomPercent / 1.0e+2D));
//        this.canvas.repaint();
    }

    private static final double INTENSITY = -1.0e-2D;

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        final var units = e.getUnitsToScroll();
        final var zoomRaw = e.getWheelRotation();//e.getPreciseWheelRotation();
        if (-0.11D < zoomRaw && zoomRaw < +0.11D) {
            dd(zoomRaw, "zoomRaw");
//            System.out.println(" [too small]");
            return;
        }
        final var zoomAdjusted = Math.exp(zoomRaw * INTENSITY);
        final var scaleOld = this.canvas.scale();
//        final var scaleOld = this.scaler.getX();
        final var scaleNewRaw = scaleOld * zoomAdjusted;
        final var scaleNew = Math.clamp(scaleNewRaw, 1.0e-2D, 1.0e+2D);

        dd(units, "units");
        dd(zoomRaw, "zoomRaw");
        dd(zoomAdjusted, "zoomAdjusted");
        dd(scaleOld, "scaleOld");
        dd(scaleNewRaw, "scaleNewRaw");
        dd(scaleNew, "scaleNew");
//        System.out.println();

        this.canvas.scale(scaleNew);
//        this.scaler.setX(scaleNew);
//        this.scaler.setY(scaleNew);
        this.canvas.pivot(AwtUtil.create(e.getX(), e.getY()));

        this.canvas.repaint();
    }

    private void dd(double value, String name) {
//        System.out.printf("%12s=%9.3f  | ", name, value);
    }
}
