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

import java.awt.*;
import java.awt.desktop.QuitResponse;
import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.List;

@RequiredArgsConstructor
public class CommandController {
    private final DatabaseModel db;





    public void newFile() {
    }

    public void open() {
        open(chooseFilesToOpen());
    }

    public void open(final List<File> files) {
        files.forEach(this::open);
    }

    public void open(final File file) {
        this.db.open(file.toPath());
    }

    public void close() {
        this.db.close();
    }

    public void save() {

    }

    public void saveAs() {

    }

    public void pageSetup() {

    }

    public void print() {
        log("print");
    }


    public void print(final List<File> files) {
        log("print-files");
    }



    public void undo() {
    }

    public void redo() {
    }

    public void cut() {
    }

    public void copy() {
    }

    public void paste() {
    }

    public void delete() {
    }

    public void selectAll() {
    }

    public void find() {
    }

    public void findNext() {
    }

    public void findPrevious() {
    }





    public void help() {
        log("help");
    }

    public void about() {
        log("about");
    }

    public void preferences() {
        log("preferences");
    }

    public void quit() {
        log("quit");
        // TODO fix quit sequence (as in SwingQuitApp)
        System.exit(0);
    }

    public void quit(final QuitResponse r) {
        log("quit-desktop");
    }






    private static final boolean SINGLE_FILE_SELECTION = false;
    private static final boolean MULTIPLE_FILE_SELECTION = true;

    private static List<File> chooseFilesToOpen() {
        val fd = new FileDialog((Frame)null, "Open...", FileDialog.LOAD);
        fd.setMultipleMode(SINGLE_FILE_SELECTION);
        fd.setDirectory(UserPrefs.dir().toString());
        fd.setVisible(true);

        val d = fd.getDirectory();
        val ret = List.of(fd.getFiles());
        fd.dispose();

        if (Objects.nonNull(d) && !d.isBlank()) {
            UserPrefs.dir(Path.of(d));
        }
        return ret;
    }





    private static void log(final String s) {
        System.out.println(s);
    }
}
