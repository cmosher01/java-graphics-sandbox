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

import javax.swing.*;

public class MenuController {
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    // ellipsis, "dot dot dot"
    private static final String DDD = "\u2026";



    public static final String CMD_NEW = "New";
    public static final String CMD_OPEN = "Open"+DDD;
    public static final String CMD_CLOSE = "Close";
    public static final String CMD_SAVE = "Save";
    public static final String CMD_SAVE_AS = "Save As"+DDD;
    public static final String CMD_PAGE_SETUP = "Page Setup";
    public static final String CMD_PRINT = "Print"+DDD;
    public static final String CMD_QUIT = "Quit";
    public static final String CMD_UNDO = "Undo";
    public static final String CMD_REDO = "Redo";
    public static final String CMD_CUT = "Cut";
    public static final String CMD_COPY = "Copy";
    public static final String CMD_PASTE = "Paste";
    public static final String CMD_DELETE = "Delete";
    public static final String CMD_SELECT_All = "Select All";
    public static final String CMD_FIND = "Find"+DDD;
    public static final String CMD_FIND_NEXT = "Find Next";
    public static final String CMD_FIND_PREVIOUS = "Find Previous";
    public static final String CMD_PREFERENCES = "Preferences";
    public static final String CMD_HELP = "Help";
    public static final String CMD_ABOUT = "About";
    //TODO can we use Action objects instead? How well would it fit with MVC architecture? Is Undo/Redo related?
//    public final Action actionNew = new Action() ???



    public MenuController(final MenuView view, final CommandController command) {
        view.addActionListener(e -> {
            switch (e.getActionCommand()) {
                case CMD_NEW -> command.newFile();
                case CMD_OPEN -> command.open();
                case CMD_CLOSE -> command.close();
                case CMD_SAVE -> command.save();
                case CMD_SAVE_AS -> command.saveAs();
                case CMD_PAGE_SETUP -> command.pageSetup();
                case CMD_PRINT -> command.print();
                case CMD_QUIT -> command.quit();
//                case CMD_QUIT -> controllerQuit.quit();
                case CMD_UNDO -> command.undo();
                case CMD_REDO -> command.redo();
                case CMD_CUT -> command.cut();
                case CMD_COPY -> command.copy();
                case CMD_PASTE -> command.paste();
                case CMD_DELETE -> command.delete();
                case CMD_SELECT_All -> command.selectAll();
                case CMD_FIND -> command.find();
                case CMD_FIND_NEXT -> command.findNext();
                case CMD_FIND_PREVIOUS -> command.findPrevious();
                case CMD_PREFERENCES -> command.preferences();
                case CMD_HELP -> command.help();
                case CMD_ABOUT -> command.about();
            }
        });
    }
}
