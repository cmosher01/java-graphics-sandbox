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

import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

import static java.awt.event.KeyEvent.*;
import static nu.mine.mosher.zoom.swingquit.MenuController.*;

@SuppressWarnings({"MagicConstant", "SpellCheckingInspection"})
public class MenuView extends JMenuBar {
    private static final Desktop DT = Desktop.getDesktop();
    private static final Toolkit TK = Toolkit.getDefaultToolkit();
    private static final int CMD = TK.getMenuShortcutKeyMaskEx();

    private final JMenuItem save = new JMenuItem(CMD_SAVE);
    private final JMenuItem quit = new JMenuItem(CMD_QUIT);
    private final JMenuItem exit = new JMenuItem(CMD_EXIT);
    private final JMenuItem line = new JMenuItem(CMD_LINE);
    private final JMenuItem thrw = new JMenuItem(CMD_THROW);
    private final List<AbstractButton> actions = List.of(save, quit, exit, line, thrw);

    public void addActionListener(final ActionListener controller) {
        this.actions.forEach(a -> a.addActionListener(controller));
    }

    public MenuView() {
        this.save.setAccelerator(KeyStroke.getKeyStroke(VK_S, CMD));
        if (!DT.isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
            // On Mac, Cmd-Q causes execution of BOTH this Quit menu item,
            // and the Application menu's Quit item.
            // So only add the accelerator if we don't have APP_QUIT_HANDLER:
            this.quit.setAccelerator(KeyStroke.getKeyStroke(VK_Q, CMD)); // ***
        }
        this.exit.setAccelerator(KeyStroke.getKeyStroke(VK_E, CMD)); // ***
        this.line.setAccelerator(KeyStroke.getKeyStroke(VK_L, CMD));

        val file = new JMenu("File");
        file.add(save);
        file.addSeparator();
        file.add(quit);
        file.add(exit);
        file.addSeparator();
        file.add(line);
        file.add(thrw);
        add(file);
    }
}
