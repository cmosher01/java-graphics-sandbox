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

import javax.swing.event.*;

/**
 * Use refresh() to facilitate menu state updates.
 * Don't use any other methods in this interface.
 */
@FunctionalInterface
public interface MenuView extends MenuListener {
    /**
     * Pass result to JMenu.addMenuListener().
     * Upon menu selection, the lambda will be called.
     * The lambda is intended to set the menu state appropriately
     * (enabled/disabled items, etc.)
     * @param lambda
     * @return
     */
    static MenuView refresh(final Runnable lambda) {
        return lambda::run;
    }

    void run();

    @Override
    default void menuSelected(MenuEvent e) {
        run();
    }

    @Override
    default void menuDeselected(MenuEvent e) {
    }

    @Override
    default void menuCanceled(MenuEvent e) {
    }
}
