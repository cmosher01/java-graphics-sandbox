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

import lombok.*;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;



public class FamilyTreeXyEditorApplication {
    private static final String TITLE = "Family Tree XY Editor";

    public void run(final List<String> args) throws InterruptedException, InvocationTargetException {
        preSwingSetup();
        SwingUtilities.invokeAndWait(() -> mainMvc(args));
    }

    private void mainMvc(final List<String> args) {
        postSwingSetup();

        // MODEL
        val modelCommandLineArgs = new CommandLineArgsModel(args);
        val modelZoomPan = new ZoomPanModel();
        val modelInteractiveRects = new InteractiveRectsModel();
        val modelDragSelection = new DragSelectionModel();
        val modelDataBase = new DatabaseModel();
        val modelStatusBar = new StatusBarModel(modelZoomPan);

        // VIEW
        val viewMenu = new MenuView();
        val viewMouse = new MouseView();
        val viewAxes = new AxesView(modelZoomPan);
        val viewZoomPan = new ZoomPanView(modelZoomPan);
        val viewInteractiveRects = new InteractiveRectsView(modelInteractiveRects, modelZoomPan);
        val viewDragSelection = new DragSelectionView(modelDragSelection);
        val viewMain = new MainView(modelZoomPan, viewZoomPan, viewInteractiveRects, viewDragSelection, viewAxes);
        val viewStatusBar = new StatusBarView(modelStatusBar);
        val viewFrame = new FrameView(TITLE, viewMenu, viewMouse, viewMain, viewStatusBar);

        // CONTROLLER
        val controllerZoomPan = new ZoomPanMouseController(modelZoomPan);
        val controllerInteractiveRects = new InteractiveRectsController(modelInteractiveRects, modelZoomPan);
        val controllerDragSelection = new DragSelectionController(modelDragSelection, modelInteractiveRects, modelZoomPan);
        val controllerCommands = new CommandController(modelDataBase);
        val controllerStatusBar = new StatusBarController(modelStatusBar, viewStatusBar, viewMain);
        val controllerQuit = new QuitController(controllerCommands, viewFrame, modelInteractiveRects, controllerStatusBar);
        val controllerKill = new KillController(controllerCommands, controllerQuit);
        val controllerDesktop = new DesktopController(controllerCommands, controllerQuit);
        val controllerMenu = new MenuController(viewMenu, controllerCommands, controllerQuit);
        val controllerMouse = new MouseController(viewMouse, viewMain, viewStatusBar, modelZoomPan, controllerZoomPan, modelStatusBar, controllerInteractiveRects, controllerDragSelection);

        viewFrame.display();
    }



    private static void preSwingSetup() {
        System.setProperty("apple.awt.application.name", TITLE);
        System.setProperty("sun.awt.noerasebackground", "true");
        System.setProperty("swing.boldMetal", "false");
        System.setProperty("sun.java2d.opengl", "true");
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");
    }

    @SneakyThrows
    private static void postSwingSetup() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Throwable e) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
    }
}
