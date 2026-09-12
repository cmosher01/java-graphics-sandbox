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
import java.awt.event.*;

public class SwingQuitView extends JFrame implements Disposable {
    private static final String UNTITLED_NAME = "untitled";
    private static final Component CENTER = null;
    private static final Toolkit TK = Toolkit.getDefaultToolkit();

    public SwingQuitView(final MenuController controllerMenu, final QuitController quit) {
        super(UNTITLED_NAME);

        val dim = TK.getScreenSize();
        setSize(new Dimension((int)(.8*dim.width), (int)(.8*dim.height)));
        setLocationRelativeTo(CENTER);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                quit.quit();
            }
        });

        setJMenuBar(controllerMenu);
    }

    public void display() {
        setVisible(true);
    }
}
