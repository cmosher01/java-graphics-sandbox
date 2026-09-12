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

package nu.mine.mosher.zoom.swingdialogtimeout;

import lombok.*;

import javax.swing.*;

import static javax.swing.JOptionPane.*;
import static javax.swing.WindowConstants.*;

/**
 * Demo of using TimedOptionPane.
 */
public class TimedDialogDemo {
    // switch between test cases (nominal vs. TimedOptionPane)
//    private static final boolean TEST_CASE = false;
    private static final boolean TEST_CASE = true;



    @SneakyThrows
    public static void main(String[] args) {
        SwingUtilities.invokeAndWait(TimedDialogDemo::gui);
    }

    @SneakyThrows
    private static void gui() {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        val frame = new JFrame();
        frame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        frame.setSize(640, 480);
        frame.setVisible(true);



        final Object answer;
        final Object save;
        if (TEST_CASE) {
            // Experimental showTimedOptionsDialog test case.
            save = new TimedOptionPane.TimerButton("Save (%ds)");
            answer = TimedOptionPane.showTimedOptionsDialog(5, frame, "Save?", "Quitting",
                YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, new Object[]{save, "Discard", "Cancel"}, save);
        } else {
            // Normal showOptionDialog use case, for comparison, and "control" test case.
            save = "Save";
            answer = JOptionPane.showOptionDialog(frame, "Save?", "QUIT", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, new Object[]{save, "Discard", "Cancel"}, save);
        }

        if (answer == TimedOptionPane.TIMEOUT) {
            System.out.println("Result: [no button was pressed before the dialog timed out]");
        } else if (answer == save) {
            System.out.println("Result: [default button was pressed] SAVE!");
        } else {
            System.out.println("Result: " + answer);
        }
        System.out.flush();



        frame.dispose();
    }
}
