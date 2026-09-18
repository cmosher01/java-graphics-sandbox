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

import lombok.*;

import java.awt.geom.*;
import java.util.*;

@RequiredArgsConstructor
public class InteractiveRectsSelectionModel {
    private final ArrayList<InteractiveRectModel> sqs;

    /**
     * Set of currently selected items. Redundant with InteractiveRectModel::selected property.
     */
    private final Set<InteractiveRectModel> selection = Collections.newSetFromMap(new IdentityHashMap<>());


    public void clear() {
        this.selection.forEach(sq -> sq.select(false));
        this.selection.clear();
    }

    public void select(final InteractiveRectModel sq, final boolean select) {
        sq.select(select);
        if (select) {
            this.selection.add(sq);
        } else {
            this.selection.remove(sq);
        }
    }

    public void setFromRectangle(final Rectangle2D.Double r) {
        for (val sq : this.sqs.reversed()) {
            val hit = sq.intersects(r);
            select(sq, hit);
        }
    }

    public void move(final Point2D.Double delta) {
        this.selection.forEach(sq -> sq.move(delta));
    }
}
