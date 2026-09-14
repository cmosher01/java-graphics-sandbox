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
import nu.mine.mosher.zoom.swingquit.TimedOptionPane;

import javax.swing.*;

import java.util.List;

import static javax.swing.JOptionPane.*;
import static javax.swing.WindowConstants.*;

/**
 * Demo of using TimedOptionPane.
 */
public class TimedDialogDemo {
    // switch between test cases (nominal vs. TimedOptionPane)
//    private static final boolean TEST_CASE = false;
    private static final boolean TEST_CASE = true;

    // timeout in seconds
    private static final int TIMEOUT_SECONDS = 5;



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
        final long ianswer;
        final Object save;
        final Object discard;
        final Object cancel;
        if (TEST_CASE) {
            // Experimental showTimedOptionsDialog test case. ****
            save = new TimedOptionPane.TimerButton("Save (%ds)"); // <--- ****
            discard = "Discard";
            cancel = "Cancel";
            val options = List.of(save, discard, cancel).toArray();
            answer = TimedOptionPane.showTimedOptionDialog(
                TIMEOUT_SECONDS, // <--- ****
                frame, "Save?", "Quitting", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, options, save);
        } else {
            // Nominal showOptionDialog use case, for comparison, and "control" test case.
            save = "Save";
            discard = "Discard";
            cancel = "Cancel";
            val options = List.of(save, discard, cancel).toArray();
            answer = JOptionPane.showOptionDialog(
                frame, "Save?", "Quitting", YES_NO_CANCEL_OPTION, QUESTION_MESSAGE, null, options, save);
        }

        if (answer instanceof Number n) {
            ianswer = n.longValue();
        } else {
            ianswer = -999; // not a number
        }

        if (answer == TimedOptionPane.TIMEOUT) {
            System.out.println("Result: [no button was pressed before the dialog timed out]");
        } else if (answer.equals(CLOSED_OPTION) || ianswer == CLOSED_OPTION) {
            System.out.println("Result: [dialog box terminated (ESC or closed window)]");
        } else if (answer == save || ianswer == YES_OPTION) {
            System.out.println("Result: [default button: SAVE]");
        } else if (answer == discard || ianswer == NO_OPTION) {
            System.out.println("Result: [DISCARD]");
        } else if (answer == cancel || ianswer == CANCEL_OPTION) {
            System.out.println("Result: [CANCEL]");
        } else {
            System.out.println("Result: [unknown returned value: " + answer+"]");
        }
        System.out.flush();



        frame.dispose();
    }
}
