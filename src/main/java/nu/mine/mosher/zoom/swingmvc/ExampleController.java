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

@SuppressWarnings({"InstantiationOfUtilityClass"})
public class ExampleController {
    private final ExampleView view;

    public ExampleController(final ExampleModel model, final ExampleView view) {
        this.view = view;

        val command = new ExampleCommandController(model, view);
        new ExampleActionController(command, view);
        new ExampleMouseController(command, view);
    }




    @SuppressWarnings("ConfusingMainMethod")
    public void main(@SuppressWarnings("unused") final List<String> args) {
        // can process args here (to update the model, for example)
        this.view.refresh();
        this.view.display();
    }

}
