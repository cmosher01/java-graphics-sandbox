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
import java.awt.*;

import static java.awt.BorderLayout.*;

public class FrameView extends JFrame {
    // set to ture for testing purposes (the green should never be visible in the GUI)
//    private static final boolean DEBUG_GREEN_BACKGROUND = true;
    private static final boolean DEBUG_GREEN_BACKGROUND = false;

    private static final Component CENTER_ON_SCREEN = null;
    private static final Toolkit TK = Toolkit.getDefaultToolkit();

    private final DialogViews dialogs = new DialogViews(this);

    public FrameView(final String title, final MenuView viewMenu, final MouseView viewMouse, final MainView viewMain, final StatusBarView viewStatusBar) {
        super(title);

        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        val dim = TK.getScreenSize();
        setSize(new Dimension((int)(.8*dim.width), (int)(.8*dim.height)));
        setLocationRelativeTo(CENTER_ON_SCREEN);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setJMenuBar(viewMenu);
        getContentPane().add(compose(viewMouse, viewMain), CENTER);
        getContentPane().add(viewStatusBar, PAGE_END);


        if (DEBUG_GREEN_BACKGROUND) {
            setBackground(Solarized.GREEN);
        }
    }

    private static JPanel compose(final Component front, final Component back) {
        val p = new JPanel();
        p.setLayout(new OverlayLayout(p));
        p.add(back);
        p.add(front);
        return p;
    }

    public void display() {
        setVisible(true);
    }

    public DialogViews dialogs() {
        return this.dialogs;
    }
}
