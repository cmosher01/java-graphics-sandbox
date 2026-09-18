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

/**
 * Sets itself as a listener of actions on the menu view,
 * in order to forward them to the command controller
 */
public class MenuController {
    public MenuController(final MenuView view, final CommandController command, final QuitController quit) {
        view.setNew(command::newFile);
        view.setOpen(command::open);
        view.setClose(command::close);
        view.setSave(command::save);
        view.setSaveAs(command::saveAs);
        view.setPageSetup(command::pageSetup);
        view.setPrint(command::print);
        view.setQuit(quit::quit);
        view.setUndo(command::undo);
        view.setRedo(command::redo);
        view.setCut(command::cut);
        view.setCopy(command::copy);
        view.setPaste(command::paste);
        view.setDelete(command::delete);
        view.setSelectAll(command::selectAll);
        view.setFind(command::find);
        view.setFindNext(command::findNext);
        view.setFindPrevious(command::findPrevious);
        view.setPreferences(command::preferences);
        view.setHelp(command::help);
        view.setAbout(command::about);
    }
}
