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

import static nu.mine.mosher.zoom.swinglayer.example.MenuAccelerators.*;



public class MenuView extends JMenuBar {
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    // ellipsis, "dot dot dot"
    private static final String DDD = Character.toString(0x2026);

    // TODO store as resources for internationalization purposes
    private static final String MENU_FILE = "File";
    private static final String CMD_NEW = "New";
    private static final String CMD_OPEN = "Open"+DDD;
    private static final String CMD_CLOSE = "Close";
    private static final String CMD_SAVE = "Save";
    private static final String CMD_SAVE_AS = "Save As"+DDD;
    private static final String CMD_PAGE_SETUP = "Page Setup";
    private static final String CMD_PRINT = "Print"+DDD;
    private static final String CMD_QUIT = "Quit";

    private static final String MENU_EDIT = "Edit";
    private static final String CMD_UNDO = "Undo";
    private static final String CMD_REDO = "Redo";
    private static final String CMD_CUT = "Cut";
    private static final String CMD_COPY = "Copy";
    private static final String CMD_PASTE = "Paste";
    private static final String CMD_DELETE = "Delete";
    private static final String CMD_SELECT_All = "Select All";
    private static final String CMD_FIND = "Find"+DDD;
    private static final String CMD_FIND_NEXT = "Find Next";
    private static final String CMD_FIND_PREVIOUS = "Find Previous";
    private static final String CMD_PREFERENCES = "Preferences";

    private static final String MENU_HELP = "Help";
    private static final String CMD_HELP = "Help";
    private static final String CMD_ABOUT = "About";



    private final ActiveMenu actNew = ActiveMenu.create(CMD_NEW, ACCEL_NEW);
    private final ActiveMenu actOpen = ActiveMenu.create(CMD_OPEN, ACCEL_OPEN);
    private final ActiveMenu actClose = ActiveMenu.create(CMD_CLOSE, ACCEL_CLOSE);
    private final ActiveMenu actSave = ActiveMenu.create(CMD_SAVE, ACCEL_SAVE);
    private final ActiveMenu actSaveAs = ActiveMenu.create(CMD_SAVE_AS, ACCEL_SAVE_AS);
    private final ActiveMenu actPageSetup = ActiveMenu.create(CMD_PAGE_SETUP, ACCEL_PASTE);
    private final ActiveMenu actPrint = ActiveMenu.create(CMD_PRINT, ACCEL_PRINT);
    private final ActiveMenu actQuit = ActiveMenu.create(CMD_QUIT, ACCEL_QUIT);
    private final ActiveMenu actUndo = ActiveMenu.create(CMD_UNDO, ACCEL_UNDO);
    private final ActiveMenu actRedo = ActiveMenu.create(CMD_REDO, ACCEL_REDO);
    private final ActiveMenu actCut = ActiveMenu.create(CMD_CUT, ACCEL_CUT);
    private final ActiveMenu actCopy = ActiveMenu.create(CMD_COPY, ACCEL_COPY);
    private final ActiveMenu actPaste = ActiveMenu.create(CMD_PASTE, ACCEL_PASTE);
    private final ActiveMenu actDelete = ActiveMenu.create(CMD_DELETE, ACCEL_DELETE);
    private final ActiveMenu actSelectAll = ActiveMenu.create(CMD_SELECT_All, ACCEL_SELECT_ALL);
    private final ActiveMenu actFind = ActiveMenu.create(CMD_FIND, ACCEL_FIND);
    private final ActiveMenu actFindNext = ActiveMenu.create(CMD_FIND_NEXT, ACCEL_FIND_NEXT);
    private final ActiveMenu actFindPrevious = ActiveMenu.create(CMD_FIND_PREVIOUS, ACCEL_FIND_PREVIOUS);
    private final ActiveMenu actPreferences = ActiveMenu.create(CMD_PREFERENCES, ACCEL_PREFERENCES);
    private final ActiveMenu actHelp = ActiveMenu.create(CMD_HELP, ACCEL_HELP);
    private final ActiveMenu actAbout = ActiveMenu.create(CMD_ABOUT, ACCEL_ABOUT);



    public MenuView(/*final InteractiveRectsModel.Immutable model*/) {
        val menuFile = new JMenu(MENU_FILE);
        menuFile.add(this.actNew.menu());
        menuFile.add(this.actOpen.menu());
        menuFile.addSeparator();
        menuFile.add(this.actClose.menu());
        menuFile.addSeparator();
        menuFile.add(this.actSave.menu());
        menuFile.add(this.actSaveAs.menu());
        menuFile.addSeparator();
        menuFile.add(this.actPageSetup.menu());
        menuFile.add(this.actPrint.menu());
        menuFile.addSeparator();
        menuFile.add(this.actQuit.menu());

        menuFile.addMenuListener(MenuRefresher.refresh(() -> {
//            this.actSave.setEnabled(this.model.isDirty());
            this.actNew.setEnabled(false);
        }));

        add(menuFile);



        val menuEdit = new JMenu(MENU_EDIT);
        menuEdit.add(this.actUndo.menu());
        menuEdit.add(this.actRedo.menu());
        menuEdit.addSeparator();
        menuEdit.add(this.actCut.menu());
        menuEdit.add(this.actCopy.menu());
        menuEdit.add(this.actPaste.menu());
        menuEdit.add(this.actDelete.menu());
        menuEdit.add(this.actSelectAll.menu());
        menuEdit.addSeparator();
        menuEdit.add(this.actFind.menu());
        menuEdit.add(this.actFindNext.menu());
        menuEdit.add(this.actFindPrevious.menu());
        menuEdit.addSeparator();
        menuEdit.add(this.actPreferences.menu());

        menuEdit.addMenuListener(MenuRefresher.refresh(() -> {
        }));

        add(menuEdit);



        val menuHelp = new JMenu(MENU_HELP);
        menuHelp.add(this.actHelp.menu());
        menuHelp.addSeparator();
        menuHelp.add(this.actAbout.menu());

        menuHelp.addMenuListener(MenuRefresher.refresh(() -> {
        }));

        add(menuHelp);
    }



    public void setNew(final Runnable lambda) {
        this.actNew.setAction(lambda);
    }
    public void setOpen(final Runnable lambda) {
        this.actOpen.setAction(lambda);
    }
    public void setClose(final Runnable lambda) {
        this.actClose.setAction(lambda);
    }
    public void setSave(final Runnable lambda) {
        this.actSave.setAction(lambda);
    }
    public void setSaveAs(final Runnable lambda) {
        this.actSaveAs.setAction(lambda);
    }
    public void setPageSetup(final Runnable lambda) {
        this.actPageSetup.setAction(lambda);
    }
    public void setPrint(final Runnable lambda) {
        this.actPrint.setAction(lambda);
    }
    public void setQuit(final Runnable lambda) {
        this.actQuit.setAction(lambda);
    }
    public void setUndo(final Runnable lambda) {
        this.actUndo.setAction(lambda);
    }
    public void setRedo(final Runnable lambda) {
        this.actRedo.setAction(lambda);
    }
    public void setCut(final Runnable lambda) {
        this.actCut.setAction(lambda);
    }
    public void setCopy(final Runnable lambda) {
        this.actCopy.setAction(lambda);
    }
    public void setPaste(final Runnable lambda) {
        this.actPaste.setAction(lambda);
    }
    public void setDelete(final Runnable lambda) {
        this.actDelete.setAction(lambda);
    }
    public void setSelectAll(final Runnable lambda) {
        this.actSelectAll.setAction(lambda);
    }
    public void setFind(final Runnable lambda) {
        this.actFind.setAction(lambda);
    }
    public void setFindNext(final Runnable lambda) {
        this.actFindNext.setAction(lambda);
    }
    public void setFindPrevious(final Runnable lambda) {
        this.actFindPrevious.setAction(lambda);
    }
    public void setPreferences(final Runnable lambda) {
        this.actPreferences.setAction(lambda);
    }
    public void setHelp(final Runnable lambda) {
        this.actHelp.setAction(lambda);
    }
    public void setAbout(final Runnable lambda) {
        this.actAbout.setAction(lambda);
    }
}
