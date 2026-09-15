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
    private static final String title = "Family Tree XY Editor";

    @SneakyThrows
    public static void main(final String... args) {
        System.setProperty("apple.awt.application.name", title);
        System.setProperty("sun.awt.noerasebackground", "true");
        System.setProperty("swing.boldMetal", "false");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");

        SwingUtilities.invokeAndWait(JavaTutorialSwingPaintDemo4::createAndShowGUI);
    }



    @SneakyThrows
    private static void createAndShowGUI() {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // MODEL
        val zp = new ZoomPanModel();
        val axes = new AxesModel(zp);
        val modelInteractiveRects = new InteractiveRectsModel();
        val modelDragSelection = new DragSelectionModel();
        val db = new DatabaseModel();
        val status = new StatusModel(zp);


        // VIEW
        val viewMain = new MainPane(modelInteractiveRects, zp, modelDragSelection, axes);
        val sb = new StatusBarView(status);











        val f = new JFrame(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // see comments in MouseController
//        f.addWindowFocusListener(new WindowAdapter() {
//            @Override
//            public void windowLostFocus(final WindowEvent e) {
//                controller.windowLostFocus();
//            }
//        });

        val viewMouse = new MouseView();

        val paneComposite = new JPanel();
        paneComposite.setLayout(new OverlayLayout(paneComposite));
        paneComposite.add(viewMouse);
        paneComposite.add(viewMain);

        val view = f.getContentPane();
        view.add(paneComposite, BorderLayout.CENTER);
        view.add(sb, BorderLayout.PAGE_END);

        val viewMenu = new MenuView();
        f.setJMenuBar(viewMenu);

        // resize main frame window to 80% of screen size, and center it
        f.setSize(Swings.scale(.8, Toolkit.getDefaultToolkit().getScreenSize()));
        f.setLocationRelativeTo(null);

        f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // uncomment this for testing purposes
        // (the green should never be visible in the GUI)
//        f.setBackground(Solarized.GREEN);

        f.setVisible(true);



        // CONTROLLER
        val controllerZoomPan = new ZoomPanMouseController(zp);
        val controllerInteractiveRects = new InteractiveRectsController(modelInteractiveRects, zp);
        val controllerDragSelection = new DragSelectionController(modelDragSelection, modelInteractiveRects, zp);
        val controllerCommands = new CommandController(db);
        val controllerDesktop = new DesktopController(controllerCommands);
        val controllerMenu = new MenuController(viewMenu, controllerCommands);
        val controller = new MouseController(viewMouse, viewMain, sb, zp, controllerZoomPan, status, controllerInteractiveRects, controllerDragSelection);
    }

}
