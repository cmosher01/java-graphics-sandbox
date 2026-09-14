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

package nu.mine.mosher.zoom.swingquit;

import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;


/**
 * JOptionPane subclass that provides a timeout.
 */
public class TimedOptionPane extends JOptionPane {
    private static final KeyboardFocusManager KFM = KeyboardFocusManager.getCurrentKeyboardFocusManager();

    private final Timer timer = new Timer(1000, e -> updateTimeout());
    private final String messageTemplate;
    private JDialog dialog;
    private int secondsRemaining;



    // guaranteed not to collide with JOptionPane return values (YES_OPTION, NO_OPTION, etc.)
    public static final Object TIMEOUT = TimedOptionPane.class;



    @SuppressWarnings("MagicConstant")
    private TimedOptionPane(
        final int timeoutSeconds,
        final Object message,
        final int messageType,
        final int optionType,
        final Icon icon,
        final Object[] options,
        final Object initialValue
    ) {
        super(message, messageType, optionType, icon, options, initialValue);
        this.secondsRemaining = timeoutSeconds;
        if (initialValue instanceof TimerButton tb) {
            this.messageTemplate = tb.toString();
        } else {
            this.messageTemplate = "";
        }
    }

    public static Object showTimedOptionDialog(
        final int timeoutSeconds,
        final Component parentComponent,
        final Object message,
        final String title,
        final int optionType,
        final int messageType,
        final Icon icon,
        final Object[] options,
        final Object initialValue
    ) {
        final TimedOptionPane t = new TimedOptionPane(timeoutSeconds, message, messageType, optionType, icon, options, initialValue);
        t.dialog = t.createDialog(parentComponent, title);

        t.addPropertyChangeListener(VALUE_PROPERTY, e -> t.dialog.dispose());

        final KeyEventDispatcher detectKeypress = e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                // the user is here, so stop the countdown
                t.timer.stop();
            }
            return false;
        };

        t.updateTimeout();
        KFM.addKeyEventDispatcher(detectKeypress);
        t.timer.start();
        t.dialog.setVisible(true);
        t.timer.stop();
        KFM.removeKeyEventDispatcher(detectKeypress);
        return Optional.ofNullable(t.getValue()).orElse(CLOSED_OPTION);
    }



    private void updateTimeout() {
        if (this.initialValue instanceof TimerButton tb) {
            tb.set(this.messageTemplate, this.secondsRemaining);
            updateUI();
            selectInitialValue();
        }
        if (this.secondsRemaining <= 0) {
            setValue(TIMEOUT);
            this.dialog.dispose();
        }
        this.secondsRemaining--;
    }





    /**
     * Use this if you want to have a button that displays the timer countdown.
     * Use of this is optional; the TimedOptionPane will still work even without using a TimerButton.
     */
    public static class TimerButton {
        private String format;

        /**
         * @param s format string with "%d" that gets substituted with the seconds remaining
         */
        public TimerButton(final String s) {
            this.format = s;
        }
        @Override
        public String toString() {
            return this.format;
        }
        private void set(final String template, final int seconds) {
            this.format = String.format(template, seconds);
        }
    }
}
