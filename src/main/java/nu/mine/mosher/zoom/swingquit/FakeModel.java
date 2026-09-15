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

import java.util.UUID;

public class FakeModel {
    private static final String UNTITLED_NAME = "untitled";

    private final UUID uuid = UUID.randomUUID();

    @Setter
    private String name = UNTITLED_NAME+": "+uuid;

    @Getter
    @Setter
    private boolean dirty = true;



    public String getTitle() {
        return this.name+(this.dirty?" *":"");
    }



    public Immutable readOnly() {
        return new Immutable();
    }

    // this is used by the views, so they can't modify the state
    public class Immutable {
        public String getTitle() {
            return FakeModel.this.getTitle();
        }
        public boolean isDirty() {
            return FakeModel.this.isDirty();
        }
    }
}
