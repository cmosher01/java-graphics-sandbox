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

public class SwingQuitView extends JFrame {
    private static final Component CENTER = null;
    private static final Toolkit TK = Toolkit.getDefaultToolkit();

    private final FakeModel.Immutable model;

    private final DialogViews dialogs = new DialogViews(this);
    private final MenuView menu = new MenuView();

    public SwingQuitView(final FakeModel.Immutable model) {
        this.model = model;

        val dim = TK.getScreenSize();
        setSize(new Dimension((int)(.8*dim.width), (int)(.8*dim.height)));
        setLocationRelativeTo(CENTER);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setJMenuBar(this.menu);
    }

    public void display() {
        setVisible(true);
    }

    public DialogViews dialogs() {
        return this.dialogs;
    }

    public MenuView menu() {
        return this.menu;
    }

    public void refresh() {
        // this would read from the model and set visible Swing elements
        setTitle(this.model.getTitle());
        System.out.println("Refresh view from model: "+this.model.getTitle()+" [dirty: "+this.model.isDirty()+"]");
    }
}
