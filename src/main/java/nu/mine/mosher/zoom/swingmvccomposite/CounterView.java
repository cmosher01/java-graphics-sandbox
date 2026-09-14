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

package nu.mine.mosher.zoom.swingmvccomposite;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class CounterView extends JPanel {
    private final JLabel countLabel = new JLabel("");
    private final JButton incrementBtn = new JButton("Add");
    private final CounterModelImmutable model;

    public CounterView(CounterModelImmutable model) {
        super(new FlowLayout());
        this.model = model;
        add(this.countLabel);
        add(this.incrementBtn);
    }

    public void addIncrementListener(ActionListener listener) {
        this.incrementBtn.addActionListener(listener);
    }

    public void refresh() {
        this.countLabel.setText("Count: " + this.model.getCount());
    }
}
