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

import lombok.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SwingQuitApp {
    @SneakyThrows
    public static void main(final String... args) {
        val app = new SwingQuitApp(args);
        app.preSwingSetup();
        SwingUtilities.invokeAndWait(app::mainMvc);
    }



    private static final Toolkit TK = Toolkit.getDefaultToolkit();
    private static final Desktop DT = Desktop.getDesktop();
    private final List<String> args;

    private SwingQuitApp(final String... args) {
        this.args = List.of(args);
    }

    private void preSwingSetup() {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.macos.useScreenMenuBar", "true");
    }

    private void mainMvc() {
        val model = new FakeModel();
        val view = new SwingQuitView(model.readOnly());
        val controller = new SwingQuitController(model, view);

        controller.mainMvc(this.args);
    }
}
