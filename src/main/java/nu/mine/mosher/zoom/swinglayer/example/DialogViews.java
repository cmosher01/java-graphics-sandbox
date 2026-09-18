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
import java.util.List;

import static javax.swing.JOptionPane.*;

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class DialogViews {
    // TODO make TIMEOUT_SECONDS a user preference
    private static final int TIMEOUT_SECONDS = 15;

    private final Component parent;



    /**
     * @return
     * JFileChooser.APPROVE_OPTION
     * JFileChooser.CANCEL_OPTION
     * JFileChooser.ERROR_OPTION
     */
    public int saveFileDialog() {
        return new JFileChooser().showSaveDialog(this.parent);
    }



    public enum QuitOptions {
        SAVE, DISCARD, CANCEL, TIMED_OUT
    }

    /**
     * @return QuitOptions: SAVE, DISCARD, CANCEL, TIMED_OUT
     */
    // ***
    public QuitOptions askSaveDiscardCancel() {
        val save = new TimedOptionPane.TimerButton("Save (%ds)");
        val discard = "Discard";
        val cancel = "Cancel";
        val options = List.of(save, discard, cancel).toArray();

        val answer = TimedOptionPane.showTimedOptionDialog(
            TIMEOUT_SECONDS, this.parent, "Save?", "Quitting",
            YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, options, save);

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
}
