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

package nu.mine.mosher.zoom.swinglayer.example;

import lombok.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JavaTutorialSwingPaintDemo4 {
    @SneakyThrows
    public static void main(final String... args) {
        System.setProperty("sun.awt.noerasebackground", "true");
        System.setProperty("swing.boldMetal", "false");
        System.setProperty("sun.java2d.opengl", "true");

        SwingUtilities.invokeAndWait(JavaTutorialSwingPaintDemo4::createAndShowGUI);
    }



    @SneakyThrows
    private static void createAndShowGUI() {
        val zp = new ZoomPanModel();
        val zpm = new ZoomPanMouse(zp);

        val modelInteractiveRects = new InteractiveRectsModel();
        val cntlrInteractiveRects = new InteractiveRectsController(modelInteractiveRects, zp);

        val modelDragSelection = new DragSelectionModel();
        val cntlrDragSelection = new DragSelectionController(modelDragSelection, modelInteractiveRects, zp);

        val axes = new AxesModel(zp);

        val panel = new MainPane(modelInteractiveRects, zp, modelDragSelection, axes);

        val status = new StatusModel(zp, panel);
        val sb = new StatusBarView(status);



        val f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

//        JFrame.setDefaultLookAndFeelDecorated(true);
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        // top-level view is the content pane
        val view = f.getContentPane();
        view.add(panel, BorderLayout.CENTER);
        view.add(sb, BorderLayout.PAGE_END);

        // top-level controller is the glass pane
        val controller = new MainController(panel, sb, zp, zpm, status, cntlrInteractiveRects, cntlrDragSelection);
        f.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(final WindowEvent e) {
                controller.windowLostFocus();
            }
        });
        f.setGlassPane(controller);
        controller.setVisible(true);



        // resize main frame window to 80% of screen size, and center it
        val screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setSize(pct80(screenSize.getWidth()), pct80(screenSize.getHeight()));
        f.setLocationRelativeTo(null);

        f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // uncomment this for testing purposes
        // (the green should never be visible in the GUI)
//        f.setBackground(Solarized.GREEN);

        f.setVisible(true);
    }

    private static int pct80(final double d) {
        return (int)Math.round(Math.rint(0.80D * d));
    }
}
