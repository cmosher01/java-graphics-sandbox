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
import java.awt.*;

import static java.awt.event.InputEvent.*;
import static java.awt.event.KeyEvent.*;
import static javax.swing.KeyStroke.*;

@SuppressWarnings("MagicConstant")
public final class MenuAccelerators {
    private static final Desktop DT = Desktop.getDesktop();
    private static final KeyStroke NO_ACCEL = null;
    private static final int NO_MODIFIERS = 0;
    private static final int CMD = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
    private static final int SHIFT_CMD = SHIFT_DOWN_MASK | CMD;



    // On Mac, Cmd-Q causes execution of BOTH this Quit menu item,
    // and the Application menu's Quit item.
    // So only add the accelerator if we don't have APP_QUIT_HANDLER:
    public static final KeyStroke ACCEL_QUIT =
        DT.isSupported(Desktop.Action.APP_QUIT_HANDLER)
            ? NO_ACCEL
            : getKeyStroke(VK_Q, CMD);

    public static final KeyStroke ACCEL_PREFERENCES =
        DT.isSupported(Desktop.Action.APP_PREFERENCES)
            ? NO_ACCEL
            : getKeyStroke(VK_COMMA, CMD);



    public static final KeyStroke ACCEL_NEW = getKeyStroke(VK_N, CMD);
    public static final KeyStroke ACCEL_OPEN = getKeyStroke(VK_O, CMD);
    public static final KeyStroke ACCEL_CLOSE = getKeyStroke(VK_W, CMD);
    public static final KeyStroke ACCEL_SAVE = getKeyStroke(VK_S, CMD);
    public static final KeyStroke ACCEL_SAVE_AS = getKeyStroke(VK_S, SHIFT_CMD);
    public static final KeyStroke ACCEL_PAGE_SETUP = getKeyStroke(VK_P, SHIFT_CMD);
    public static final KeyStroke ACCEL_PRINT = getKeyStroke(VK_P, CMD);

    public static final KeyStroke ACCEL_UNDO = getKeyStroke(VK_Z, CMD);
    public static final KeyStroke ACCEL_REDO = getKeyStroke(VK_Z, SHIFT_CMD);
    public static final KeyStroke ACCEL_CUT = getKeyStroke(VK_X, CMD);
    public static final KeyStroke ACCEL_COPY = getKeyStroke(VK_C, CMD);
    public static final KeyStroke ACCEL_PASTE = getKeyStroke(VK_V, CMD);
    public static final KeyStroke ACCEL_DELETE = getKeyStroke(VK_DELETE, NO_MODIFIERS);
    public static final KeyStroke ACCEL_SELECT_ALL = getKeyStroke(VK_A, CMD);
    public static final KeyStroke ACCEL_FIND = getKeyStroke(VK_F, CMD);
    public static final KeyStroke ACCEL_FIND_NEXT = getKeyStroke(VK_G, CMD);
    public static final KeyStroke ACCEL_FIND_PREVIOUS = getKeyStroke(VK_G, SHIFT_CMD);

    public static final KeyStroke ACCEL_HELP = getKeyStroke(VK_SLASH, SHIFT_CMD);
    public static final KeyStroke ACCEL_ABOUT = NO_ACCEL;
}
