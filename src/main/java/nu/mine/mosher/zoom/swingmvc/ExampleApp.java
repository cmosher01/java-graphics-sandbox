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

import lombok.*;

import javax.swing.*;
import java.util.List;

/**
 * See "Simple Java Swing  Model-View-Controller architecture" at
 * <a href="https://www.tldraw.com/p/dFnLn33eeS05HJAdDu_W8?d=v-995.-3608.3040.1986.page">tldraw</a>
 */
public class ExampleApp {
    @SneakyThrows
    public static void main(final String... args) {
        val commandLineArguments = List.of(args);
        SwingUtilities.invokeAndWait(() -> mainMvc(commandLineArguments));
    }

    private static void mainMvc(final List<String> args) {
        val model = new ExampleModel();
        val view = new ExampleView(new ExampleImmutableModel(model));
        val controller = new ExampleController(model, view);

        controller.main(args);
    }
}
