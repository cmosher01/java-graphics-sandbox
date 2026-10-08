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

package dev.cmosher01.genealogy.xy;

import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.awt.desktop.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static java.awt.Desktop.Action.*;

public class DesktopController {
    public DesktopController(final CommandController controllerCommand, final DocumentController controllerDocument, final QuitController quit) {
        if (!Desktop.isDesktopSupported() || GraphicsEnvironment.isHeadless()) {
            return;
        }
        val desktop = Desktop.getDesktop();

        if (desktop.isSupported(APP_OPEN_FILE)) {
            desktop.setOpenFileHandler(e -> controllerDocument.open(toPaths(e.getFiles())));
        }
        if (desktop.isSupported(APP_PRINT_FILE)) {
            desktop.setPrintFileHandler(e -> controllerCommand.print(toPaths(e.getFiles())));
        }
        if (desktop.isSupported(APP_ABOUT)) {
            desktop.setAboutHandler(e -> controllerCommand.about());
        }
        if (desktop.isSupported(APP_PREFERENCES)) {
            desktop.setPreferencesHandler(e -> controllerCommand.preferences());
        }
        if (desktop.isSupported(APP_QUIT_HANDLER)) {
            desktop.setQuitHandler((e, response) ->
                SwingUtilities.invokeLater(() -> quit.quit(response)));
            desktop.disableSuddenTermination();
            desktop.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
        }
    }

    private static List<Path> toPaths(final List<File> files) {
        return files.stream().map(File::toPath).toList();
    }
}
