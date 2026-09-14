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

package nu.mine.mosher.zoom.swingmvc;

import java.awt.event.*;

public class ExampleActionController {
    // "actions" (e.g., menu item actions, button actions)
    public static final String CMD_INCREMENT = "Increment";
    public static final String CMD_RESET = "Reset";
    public static final String CMD_EXIT = "Quit";

    public ExampleActionController(final ExampleCommandController command, final ExampleView view) {
        view.addActionListener(e -> {
            switch (e.getActionCommand()) {
                case CMD_INCREMENT -> command.increment();
                case CMD_RESET -> command.reset();
                case CMD_EXIT -> command.quit();
            }
        });
    }
}
