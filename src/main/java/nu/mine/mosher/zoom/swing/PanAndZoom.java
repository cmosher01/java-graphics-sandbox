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

import javax.swing.*;
import java.awt.*;

public class PanAndZoom {
    public PanAndZoom() {
        final var canvas = new PanAndZoomCanvas();
        {
            final var panner = new PanningHandler(canvas);
            canvas.addMouseListener(panner);
            canvas.addMouseMotionListener(panner);
            canvas.setBorder(BorderFactory.createLineBorder(Color.black));

            final var scaler = new ScaleHandler(canvas);
            canvas.addMouseWheelListener(scaler);
        }

        // code for handling zooming
//        final var zoomSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 100);
//        {
//            zoomSlider.setMajorTickSpacing(25);
//            zoomSlider.setMinorTickSpacing(5);
//            zoomSlider.setPaintTicks(true);
//            zoomSlider.setPaintLabels(true);
//            zoomSlider.addChangeListener(new ScaleHandler(canvas));
//        }

        // Add the components to the canvas
        final var frame = new JFrame();
//        frame.getContentPane().add(zoomSlider, BorderLayout.NORTH);
        frame.getContentPane().add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
