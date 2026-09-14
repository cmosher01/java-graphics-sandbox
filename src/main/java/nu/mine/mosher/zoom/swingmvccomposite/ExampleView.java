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

import lombok.Getter;

import javax.swing.*;
import java.awt.*;

@Getter
public class ExampleView extends JPanel {
    private final CounterView independentView;
    private final CounterView targetView;
    private final ResetView resetView;

    public ExampleView(ExampleModelImmutable model) {
        super(new GridLayout(1, 0, 10, 10));

        this.independentView = new CounterView(model.getIndependent());
        add(this.independentView);

        this.targetView = new CounterView(model.getTarget());
        add(this.targetView);

        this.resetView = new ResetView();
        add(this.resetView);
    }
}
