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
import java.awt.event.ActionListener;
import java.util.List;

import static nu.mine.mosher.zoom.swinglayer.example.MenuAccelerators.*;
import static nu.mine.mosher.zoom.swinglayer.example.MenuController.*;



public class MenuView extends JMenuBar {
    private static final Desktop DT = Desktop.getDesktop();



    private final JMenuItem newFile = new JMenuItem(CMD_NEW);
    private final JMenuItem open = new JMenuItem(CMD_OPEN);
    private final JMenuItem close = new JMenuItem(CMD_CLOSE);
    private final JMenuItem save = new JMenuItem(CMD_SAVE);
    private final JMenuItem saveAs = new JMenuItem(CMD_SAVE_AS);
    private final JMenuItem pageSetup = new JMenuItem(CMD_PAGE_SETUP);
    private final JMenuItem print = new JMenuItem(CMD_PRINT);
    private final JMenuItem quit = new JMenuItem(CMD_QUIT);
    private final JMenuItem undo = new JMenuItem(CMD_UNDO);
    private final JMenuItem redo = new JMenuItem(CMD_REDO);
    private final JMenuItem cut = new JMenuItem(CMD_CUT);
    private final JMenuItem copy = new JMenuItem(CMD_COPY);
    private final JMenuItem paste = new JMenuItem(CMD_PASTE);
    private final JMenuItem delete = new JMenuItem(CMD_DELETE);
    private final JMenuItem selectAll = new JMenuItem(CMD_SELECT_All);
    private final JMenuItem find = new JMenuItem(CMD_FIND);
    private final JMenuItem findNext = new JMenuItem(CMD_FIND_NEXT);
    private final JMenuItem findPrevious = new JMenuItem(CMD_FIND_PREVIOUS);
    private final JMenuItem preferences = new JMenuItem(CMD_PREFERENCES);
    private final JMenuItem help = new JMenuItem(CMD_HELP);
    private final JMenuItem about = new JMenuItem(CMD_ABOUT);
    private final List<AbstractButton> actions = List.of(
        newFile, open, close, save, saveAs, pageSetup, print, quit,
        undo, redo, cut, copy, paste, delete, selectAll, find, findNext, findPrevious, preferences,
        help, about);

    public void addActionListener(final ActionListener controller) {
        this.actions.forEach(a -> a.addActionListener(controller));
    }



    public MenuView(/*final InteractiveRectsModel.Immutable model*/) {
        this.newFile.setAccelerator(ACCEL_NEW);
        this.open.setAccelerator(ACCEL_OPEN);
        this.close.setAccelerator(ACCEL_CLOSE);
        this.save.setAccelerator(ACCEL_SAVE);
        this.saveAs.setAccelerator(ACCEL_SAVE_AS);
        this.pageSetup.setAccelerator(ACCEL_PAGE_SETUP);
        this.preferences.setAccelerator(ACCEL_PREFERENCES);
        if (!DT.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
            // On Mac, Cmd-Q causes execution of BOTH this Quit menu item,
            // and the Application menu's Quit item.
            // So only add the accelerator if we don't have APP_QUIT_HANDLER:
            this.quit.setAccelerator(ACCEL_QUIT);
        }
        this.undo.setAccelerator(ACCEL_UNDO);
        this.redo.setAccelerator(ACCEL_REDO);
        this.cut.setAccelerator(ACCEL_CUT);
        this.copy.setAccelerator(ACCEL_COPY);
        this.paste.setAccelerator(ACCEL_PASTE);
        this.delete.setAccelerator(ACCEL_DELETE);
        this.selectAll.setAccelerator(ACCEL_SELECT_ALL);
        this.find.setAccelerator(ACCEL_FIND);
        this.findNext.setAccelerator(ACCEL_FIND_NEXT);
        this.findPrevious.setAccelerator(ACCEL_FIND_PREVIOUS);
        if (!DT.isSupported(Desktop.Action.APP_PREFERENCES)) {
            // don't clash with Mac's Application menu preferences item
            this.preferences.setAccelerator(ACCEL_PREFERENCES);
        }
        this.help.setAccelerator(ACCEL_HELP);
        this.about.setAccelerator(ACCEL_ABOUT);



        val menuFile = new JMenu("File");
        menuFile.add(newFile);
        menuFile.add(open);
        menuFile.addSeparator();
        menuFile.add(close);
        menuFile.addSeparator();
        menuFile.add(save);
        menuFile.add(saveAs);
        menuFile.addSeparator();
        menuFile.add(pageSetup);
        menuFile.add(print);
        menuFile.addSeparator();
        menuFile.add(quit);

        menuFile.addMenuListener(MenuRefresher.refresh(() -> {
//            save.setEnabled(model.isDirty());
        }));

        add(menuFile);



        val menuEdit = new JMenu("Edit");
        menuEdit.add(undo);
        menuEdit.add(redo);
        menuEdit.addSeparator();
        menuEdit.add(cut);
        menuEdit.add(copy);
        menuEdit.add(paste);
        menuEdit.add(delete);
        menuEdit.add(selectAll);
        menuEdit.addSeparator();
        menuEdit.add(find);
        menuEdit.add(findNext);
        menuEdit.add(findPrevious);
        menuEdit.addSeparator();
        menuEdit.add(preferences);

        menuEdit.addMenuListener(MenuRefresher.refresh(() -> {
        }));

        add(menuEdit);



        val menuHelp = new JMenu("Help");
        menuHelp.add(help);
        menuHelp.addSeparator();
        menuHelp.add(about);

        menuHelp.addMenuListener(MenuRefresher.refresh(() -> {
        }));

        add(menuHelp);
    }
}
