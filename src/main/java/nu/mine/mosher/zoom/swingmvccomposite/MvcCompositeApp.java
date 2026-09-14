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

import lombok.*;

import javax.swing.*;

/**
 * I prompted Gemini AI to create this application. I provided
 * many prompts for it to do refactorings. I subsequently reviewed
 * every line of code manually and refactored and fixed myself
 * without AI aid. All comments in the code are mine; I removed
 * all AI-generated comments.
 *
 * See "Composite Model-View-Controller architecture" diagram at
 * <a href="https://www.tldraw.com/p/dFnLn33eeS05HJAdDu_W8?d=v-804.-1783.3684.2407.page">tldraw</a>
 */
public class MvcCompositeApp {
    @SneakyThrows
    public static void main(String[] args) {
        SwingUtilities.invokeAndWait(() -> {
            val model = new ExampleModel();
            val modelImmutable = new ExampleModelImmutable(model);
            val view = new ExampleView(modelImmutable);
            val controller = new ExampleController(model, view);

            val frame = new JFrame("Assembled Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(view);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
