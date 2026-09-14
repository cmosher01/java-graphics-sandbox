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

package nu.mine.mosher.zoom.swingquit;

import lombok.*;

import javax.swing.*;
import java.awt.*;

public class SwingQuitApp {
    private static final Toolkit TK = Toolkit.getDefaultToolkit();
    private static final Desktop DT = Desktop.getDesktop();

    @SneakyThrows
    public static void main(final String... args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");
        SwingUtilities.invokeAndWait(SwingQuitApp::create);
    }

    @SuppressWarnings("InstantiationOfUtilityClass")
    private static void create() {
        val model = new FakeModel();

        val viewMenu = new MenuView();
        val view = new SwingQuitView(viewMenu);

        val controllerCommand = new CommandController();
        val controllerQuit = new QuitController(controllerCommand, view, model);
        val controllerDesktop = new DesktopController(controllerQuit);
        val controllerKill = new KillController(controllerCommand, controllerQuit);
        val controllerMenu = new MenuController(viewMenu, controllerCommand, controllerQuit);

        view.display();
    }
}
