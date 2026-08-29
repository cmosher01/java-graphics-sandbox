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

// From: https://docs.oracle.com/javase/tutorial/uiswing/examples/painting/
// with my corrections, additions, and refactorings (Chris Mosher)

package nu.mine.mosher.zoom.swing2;

import lombok.val;
import nu.mine.mosher.zoom.swingpanel.*;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class JavaTutorialSwingPaintDemo4 {
    public static void main(final String... args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(JavaTutorialSwingPaintDemo4::createAndShowGUI);
    }

    private static void createAndShowGUI() {
//        for (val ffn : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
//            System.out.println(ffn);
//        }
//        System.out.println("Created GUI on EDT? " + SwingUtilities.isEventDispatchThread());

        val f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TODO can we calculate min/max/speed based on bounds of image?
        val zp = new ZoomPan(1e-1, 1e+4, 5e-2);
        val panel = new MyPanel(zp);
        val panzoomPanel = new ZoomPanPanel(panel, zp);
        f.add(panzoomPanel);

        f.setSize(640, 480);
        f.setVisible(true);
    }
}
