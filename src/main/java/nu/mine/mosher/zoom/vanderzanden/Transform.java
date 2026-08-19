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

package nu.mine.mosher.zoom.vanderzanden;

import java.awt.*;
import javax.swing.*;

/* This class shows examples of how to use the rotate, scale, and
   translate transformations in Java. The class displays a rectangle
   with two selection handles. One of the selection handles appears
   rotated and the other is scaled to twice its normal size in the
   x direction. The class also draws the x- and y-axes so that the
   user can see the coordinate space set up by the current transform.

   The user can interact with the application in one of four ways:

   1) Rotate the rectangle
   2) Translate the rectangle
   3) Scale (zoom) the rectangle
   4) Click somewhere in the main window and have the mouse coordinates be
      transformed relative to the upper left corner of the rectangle. The
      (X,Y) coordinates appear in the left controls window. For some reason,
      if you click in the window before a transformation has been applied,
      the affine transform object is messed up and the wrong result gets
      displayed. After you perform the first transformation, the mouse
      coordinates are correctly inverted.
*/

public class Transform extends JFrame {
    TransPanel mainCanvas;
    TransformationControlsPanel controlsCanvas;

    /* The current transformation amounts */
    double angle = 0.0;
    double scaleX = 1.0;
    double scaleY = 1.0;
    double translateX = 0.0;
    double translateY = 0.0;

    public Transform() {
        mainCanvas = new TransPanel(this);
        controlsCanvas = new TransformationControlsPanel(this);
        getContentPane().add(mainCanvas, BorderLayout.CENTER);
        getContentPane().add(controlsCanvas, BorderLayout.WEST);
    }

    public static void main(final String... argv) {
        Transform t = new Transform();
        t.pack();
        t.setVisible(true);
    }
}
