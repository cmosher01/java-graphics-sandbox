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

import java.awt.event.*;

public class MenuController {
    public static final String CMD_SAVE = "Save";
    public static final String CMD_QUIT = "Quit";
    public static final String CMD_EXIT = "Exit";
    public static final String CMD_LINE = "Line";
    public static final String CMD_THROW = "Throw";

    public MenuController(final MenuView view, final CommandController command, final QuitController controllerQuit) {
        view.addActionListener(e -> {
            switch (e.getActionCommand()) {
                case CMD_SAVE -> command.save();
                case CMD_QUIT, CMD_EXIT -> controllerQuit.quit();
                case CMD_LINE -> System.out.println("-".repeat(64));
                // by default, EDT write stack trace to stderr on throw:
                case CMD_THROW -> throw new RuntimeException("User requested RuntimeException");
            }
        });
    }
}
