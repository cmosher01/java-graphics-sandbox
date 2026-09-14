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

package nu.mine.mosher.zoom.swingmvc;

import lombok.val;

import java.util.List;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ExampleView extends JFrame {
    // read-only access to <MODEL>
    private final ExampleImmutableModel model;

    // visual components of <VIEW>
    private final JLabel label = new JLabel("", SwingConstants.CENTER);

    // Visual aspects that can trigger <CONTROLLER> actions
    // Note: don't do anything in <VIEW> that <CONTROLLER> should be doing
    private final JMenuItem reset = new JMenuItem(ExampleActionController.CMD_RESET);
    private final JMenuItem exit = new JMenuItem(ExampleActionController.CMD_EXIT);
    private final JButton inc = new JButton(ExampleActionController.CMD_INCREMENT);
    private final List<AbstractButton> actions = List.of(inc, reset, exit);



    public ExampleView(final ExampleImmutableModel model) {
        super(model.name());
        this.model = model;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(300, 150);
        setLocationRelativeTo(null);

        setJMenuBar(createMenuBar());
        getContentPane().add(this.label, BorderLayout.CENTER);
        getContentPane().add(this.inc, BorderLayout.PAGE_END);
    }



    // <CONTROLLER> calls this (once) to hook itself in to the <VIEW>'s controls
    public void addActionListener(final ActionListener controller) {
        this.actions.forEach(a -> a.addActionListener(controller));
    }

    public void display() {
        setVisible(true);
    }

    // <CONTROLLER> calls this to notify <VIEW> when it needs to update
    // the display based on the new modification to <MODEL>
    public void refresh() {
        this.label.setText("Value: " + model.get());
    }



    private JMenuBar createMenuBar() {
        val file = new JMenu("File");
        {
            file.add(this.reset);
            file.addSeparator();
            file.add(this.exit);
        }

        val mb = new JMenuBar();
        {
            mb.add(file);
        }
        return mb;
    }
}
