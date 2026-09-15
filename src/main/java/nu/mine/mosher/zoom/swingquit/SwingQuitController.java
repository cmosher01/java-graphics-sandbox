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

import java.util.List;

@SuppressWarnings("ClassCanBeRecord")
public class SwingQuitController {
    private final FakeModel model;
    private final SwingQuitView view;



    @SuppressWarnings({"InstantiationOfUtilityClass", "unused"})
    public SwingQuitController(final FakeModel model, final SwingQuitView view) {
        this.model = model;
        this.view = view;

        val controllerCommand = new CommandController(view, model);
        val controllerQuit = new QuitController(controllerCommand, view, model);
        val controllerDesktop = new DesktopController(controllerQuit);
        val controllerKill = new KillController(controllerCommand, controllerQuit);
        val controllerMenu = new MenuController(view.menu(), controllerCommand, controllerQuit);
    }



    public void mainMvc(final List<String> args) {
        // can process args here (to open files, for example)
        if (!args.isEmpty()) {
            // something silly, just for demo purposes
            this.model.setName(args.getFirst());
            this.model.setDirty(false);
        }

        this.view.refresh();
        this.view.display();
    }
}
