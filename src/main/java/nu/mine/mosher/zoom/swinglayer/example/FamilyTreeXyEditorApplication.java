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
    public void run(final @NonNull List<String> args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() -> mainMvc(args));
    }

    private void mainMvc(final @NonNull List<String> args) {
        postSwingSetup();

        // MODEL
        val modelCommandLineArgs = new CommandLineArgsModel(args);
        val modelZoomPan = new ZoomPanModel();
        val modelStatusBar = new StatusBarModel(modelZoomPan);
        val modelDragSelection = new DragSelectionModel();
        val modelInteractiveRects = new InteractiveRectsModel();
        val modelDocument = new DocumentModel(modelInteractiveRects);

        // VIEW
        val viewMouse = new MouseView();
        val viewStatusBar = new StatusBarView(modelStatusBar);
        val viewAxes = new AxesView(modelZoomPan);
        val viewZoomPan = new ZoomPanView(modelZoomPan);
        val viewInteractiveRects = new InteractiveRectsView(modelInteractiveRects, modelZoomPan);
        val viewDragSelection = new DragSelectionView(modelDragSelection);
        val viewMain = new MainView(modelZoomPan, viewZoomPan, modelInteractiveRects, viewInteractiveRects, viewDragSelection, viewAxes);
        val viewMenu = new MenuView(modelDocument);
        val viewFrame = new FrameView(modelDocument, viewMenu, viewMouse, viewMain, viewStatusBar);

        // CONTROLLER
        val controllerZoomPan = new ZoomPanMouseController(modelZoomPan);
        val controllerInteractiveRects = new InteractiveRectsController(modelInteractiveRects, modelZoomPan);
        val controllerDragSelection = new DragSelectionController<>(modelDragSelection, modelInteractiveRects.selection(), modelZoomPan);
        val controllerStatusBar = new StatusBarController(modelStatusBar, viewStatusBar, viewMain);
        val controllerCommands = new CommandController(viewFrame, viewMenu);
        val controllerDocument = new DocumentController(modelDocument, modelZoomPan, viewFrame, viewMenu, viewMain, viewInteractiveRects);
        val controllerQuit = new QuitController(controllerDocument, viewFrame, modelDocument, controllerStatusBar);
        val controllerKill = new KillController(controllerDocument, controllerQuit);
        val controllerDesktop = new DesktopController(controllerCommands, controllerDocument, controllerQuit);
        val controllerMenu = new MenuController(viewMenu, controllerDocument, controllerCommands, controllerQuit);
        val controllerMouse = new MouseController(viewMouse, viewMain, viewFrame, viewStatusBar, modelZoomPan, controllerZoomPan, modelStatusBar, controllerInteractiveRects, controllerDragSelection);

        viewFrame.display();
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
