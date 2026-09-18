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

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.*;

import static java.awt.Desktop.Action.*;

public class DesktopController {
    public DesktopController(final CommandController command, final QuitController quit) {
        if (!Desktop.isDesktopSupported() || GraphicsEnvironment.isHeadless()) {
            return;
        }
        val desktop = Desktop.getDesktop();

        if (desktop.isSupported(APP_OPEN_FILE)) {
            desktop.setOpenFileHandler(e -> command.open(e.getFiles()));
        }
        if (desktop.isSupported(APP_PRINT_FILE)) {
            desktop.setPrintFileHandler(e -> command.print(e.getFiles()));
        }
        if (desktop.isSupported(APP_ABOUT)) {
            desktop.setAboutHandler(e -> command.about());
        }
        if (desktop.isSupported(APP_PREFERENCES)) {
            desktop.setPreferencesHandler(e -> command.preferences());
        }
        if (desktop.isSupported(APP_QUIT_HANDLER)) {
            desktop.setQuitHandler((e, response) ->
                SwingUtilities.invokeLater(() -> quit.quit(response)));
            desktop.disableSuddenTermination();
            desktop.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
        }
    }
}
