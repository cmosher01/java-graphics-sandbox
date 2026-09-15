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

@SuppressWarnings("ClassCanBeRecord")
@RequiredArgsConstructor
public class CommandController {
    private final SwingQuitView view;
    private final FakeModel model;

    public void save() {
        save(true);
    }

    public void save(final boolean attended) {
        // For an unattended save: auto back up of existing file, or creation on new file for untitled documents
        // Can allow an app-specific user settings for unattended auto save behavior
        // make sure unattended saves do not do anything with the view
        // because it could be happening during an app shutdown sequence

        System.out.println("[save "+(attended?"":" UN")+"ATTENDED]: "+this.model.getTitle());

        this.model.setDirty(false);

        if (attended) {
            this.view.refresh();
        }
    }
}
