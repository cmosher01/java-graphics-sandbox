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

import java.awt.event.ActionListener;

import static nu.mine.mosher.zoom.swinglayer.example.MenuAccelerators.*;

public class MenuController {
    @SuppressWarnings("UnnecessaryUnicodeEscape")
    // ellipsis, "dot dot dot"
    private static final String DDD = "\u2026";



    public static JMenuBar createMenuBar(final CommandController command, final String title, final DatabaseModel db) {
        // i = item, m = menu

        val iNew = item("New", ACCEL_NEW, e -> command.filenew());
        val iOpen = item("Open"+DDD, ACCEL_OPEN, e -> command.open());
        val iClose = item("Close", ACCEL_CLOSE, e -> command.close());
        val iSave = item("Save", ACCEL_SAVE, e -> command.save());
        val iSaveAs = item("Save As"+DDD, ACCEL_SAVE_AS, e -> command.saveAs());
        val iPageSetup = item("Page Setup", ACCEL_PAGE_SETUP, e -> command.pageSetup());
        val iPrint = item("Print"+DDD, ACCEL_PRINT, e -> command.print());
        val iQuit = item("Quit", ACCEL_QUIT, e -> command.quit());

        val mFile = new JMenu("File");
        mFile.add(iNew);
        mFile.add(iOpen);
        mFile.addSeparator();
        mFile.add(iClose);
        mFile.addSeparator();
        mFile.add(iSave);
        mFile.add(iSaveAs);
        mFile.addSeparator();
        mFile.add(iPageSetup);
        mFile.add(iPrint);
        mFile.addSeparator();
        mFile.add(iQuit);

        mFile.addMenuListener(MenuView.refresh(() -> {
            iOpen.setEnabled(!db.isOpen());
            iClose.setEnabled(db.isOpen());
        }));





        val iUndo = item("Undo", ACCEL_UNDO, e -> command.undo());
        val iRedo = item("Redo", ACCEL_REDO, e -> command.redo());
        val iCut = item("Cut", ACCEL_CUT, e -> command.cut());
        val iCopy = item("Copy", ACCEL_COPY, e -> command.copy());
        val iPaste = item("Paste", ACCEL_PASTE, e -> command.paste());
        val iDelete = item("Delete", ACCEL_DELETE, e -> command.delete());
        val iSelectAll = item("Select All", ACCEL_SELECT_ALL, e -> command.selectAll());
        val iFind = item("Find"+DDD, ACCEL_FIND, e -> command.find());
        val iFindNext = item("Find Next", ACCEL_FIND_NEXT, e -> command.findNext());
        val iFindPrevious = item("Find Previous", ACCEL_FIND_PREVIOUS, e -> command.findPrevious());
        val iPreferences = item("Preferences", ACCEL_PREFERENCES, e -> command.preferences());

        val mEdit = new JMenu("Edit");
        mEdit.add(iUndo);
        mEdit.add(iRedo);
        mEdit.addSeparator();
        mEdit.add(iCut);
        mEdit.add(iCopy);
        mEdit.add(iPaste);
        mEdit.add(iDelete);
        mEdit.add(iSelectAll);
        mEdit.addSeparator();
        mEdit.add(iFind);
        mEdit.add(iFindNext);
        mEdit.add(iFindPrevious);
        mEdit.addSeparator();
        mEdit.add(iPreferences);

        mEdit.addMenuListener(MenuView.refresh(() -> {
        }));




        val iHelp = item("Help", ACCEL_HELP, e -> command.help());
        val iAbout = item("About "+title, null, e -> command.about());

        val mHelp = new JMenu("Help");
        mHelp.add(iHelp);
        mHelp.addSeparator();
        mHelp.add(iAbout);

        mHelp.addMenuListener(MenuView.refresh(() -> {
        }));




        val b = new JMenuBar();
        b.add(mFile);
        b.add(mEdit);
        b.add(mHelp);
        return b;
    }





    private static JMenuItem item(final String text, final KeyStroke accel, final ActionListener action) {
        val ret = new JMenuItem(text);
        ret.setAccelerator(accel);
        ret.addActionListener(action);
        return ret;
    }
}
