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

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static javax.swing.JOptionPane.*;

@SuppressWarnings({"ClassCanBeRecord", "OptionalUsedAsFieldOrParameterType"})
@RequiredArgsConstructor
public class DialogViews {
    // TODO make TIMEOUT_SECONDS a user preference
    private static final int TIMEOUT_SECONDS = 15;
    private static final boolean SINGLE_FILE_SELECTION = false;
    private static final boolean MULTIPLE_FILE_SELECTION = true;

    private final JFrame parent;

    public List<Path> chooseFilesToOpen() {
        val fd = new FileDialog(this.parent, "Open...", FileDialog.LOAD);
        fd.setMultipleMode(SINGLE_FILE_SELECTION);
        fd.setDirectory(UserPrefs.dir().toString());
        fd.setVisible(true);

        val d = parseDir(fd.getDirectory());
        d.ifPresent(UserPrefs::dir);

        val ret = Stream.of(fd.getFiles()).map(File::toPath).toList();

        fd.dispose();

        return ret;
    }

//    public List<Path> chooseFilesToOpenSwing() {
//        val fd = new JFileChooser(UserPrefs.dir().toFile());
//        fd.setMultiSelectionEnabled(SINGLE_FILE_SELECTION);
//
//        val answer = fd.showOpenDialog(this.parent);
//
//        val d = Optional.ofNullable(fd.getCurrentDirectory());
//        val ret = Stream.of(fd.getSelectedFiles()).map(File::toPath).toList();
//
//        d.ifPresent(file -> UserPrefs.dir(file.toPath()));
//
//        return ret;
//    }

    public Optional<Path> saveAsFileDialog(final Optional<Path> pathFile) {
        val fd = new FileDialog(this.parent, "Save As...", FileDialog.SAVE);
        if (pathFile.isPresent()) {
            fd.setFile(pathFile.get().toString());
        } else {
            fd.setDirectory(UserPrefs.dir().toString());
            fd.setFile("");
        }
        fd.setVisible(true);

        val d = parseDir(fd.getDirectory());
        d.ifPresent(UserPrefs::dir);

        Optional<Path> ret = Optional.empty();
        val f = parseFile(fd.getFile());
        if (f.isPresent()) {
            val dOrCurr = d.orElse(Path.of(""));
            ret = Optional.of(dOrCurr.resolve(f.get()));
        }
        return ret;
    }

    public enum QuitOptions {
        SAVE, DISCARD, CANCEL, TIMED_OUT
    }

    /**
     * @return QuitOptions: SAVE, DISCARD, CANCEL, TIMED_OUT
     */
    public QuitOptions askSaveDiscardCancel() {
        val save = new TimedOptionPane.TimerButton("Save (%ds)");
        val discard = "Discard";
        val cancel = "Cancel";
        val options = List.of(save, discard, cancel).toArray();

        val answer = TimedOptionPane.showTimedOptionDialog(
            TIMEOUT_SECONDS, this.parent, "Save?", "Quitting",
            YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, /*TODO icons*/null, options, save);

        final QuitOptions ret;
        if (answer == save) {
            ret = QuitOptions.SAVE;
        } else if (answer == discard) {
            ret = QuitOptions.DISCARD;
        } else if (answer == cancel || answer.equals(CLOSED_OPTION)) {
            ret = QuitOptions.CANCEL;
        } else {
            ret = QuitOptions.TIMED_OUT;
        }
        return ret;
    }



    private static Optional<Path> parseDir(final String dir) {
        Optional<Path> ret = Optional.empty();
        if (Objects.nonNull(dir) && !dir.isBlank()) {
            val p = Path.of(dir.strip());
            if (Files.isDirectory(p)) {
                ret = Optional.of(p);
            }
        }
        return ret;
    }

    private static Optional<Path> parseFile(final String file) {
        Optional<Path> ret = Optional.empty();
        if (Objects.nonNull(file) && !file.isBlank() && !file.equals(".") && !file.equals("..")) {
            val p = Path.of(file.strip());
            ret = Optional.of(p);
        }
        return ret;
    }
}
