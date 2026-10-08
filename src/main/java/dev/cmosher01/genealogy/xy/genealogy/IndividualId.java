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

package dev.cmosher01.genealogy.xy.genealogy;

import lombok.NonNull;
import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.GedcomLine;

import java.util.Objects;

public class IndividualId {
    private final TreeNode<GedcomLine> node;
    private final String pkid;



    public IndividualId(final @NonNull TreeNode<GedcomLine> node) {
        this.pkid = null;
        this.node = node;
    }

    public IndividualId(final @NonNull String pkid) {
        this.pkid = pkid;
        this.node = null;
    }



    public boolean isFtm() {
        return Objects.nonNull(this.pkid);
    }

    public @NonNull String pkid() {
        return Objects.requireNonNull(this.pkid);
    }

    public @NonNull TreeNode<GedcomLine> node() {
        return Objects.requireNonNull(this.node);
    }
}
