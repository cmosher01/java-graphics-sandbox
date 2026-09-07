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

import java.awt.geom.Point2D;
import java.util.Optional;

@RequiredArgsConstructor
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class RedSquaresMouse {
    // slight performance improvement (only slight) by setting this
    // to false, which causes the chart-bounding-rectangle to be updated
    // only AFTER dragging items off the edge is COMPLETE (i.e., on mouse release)
    private static final boolean DYNAMIC_BOUNDS_UPDATING = true;

    private final RedSquaresModel model;
    private final ZoomPan zp;

    private Optional<InteractiveRect> optItem = Optional.empty();
    private Optional<Point2D.Double> optptDragPivot = Optional.empty();
    private boolean wasSelected;



    public boolean want(final Point2D.Double at) {
        this.optItem = this.model.getAt(at);
        val want = this.optItem.isPresent();
        if (want) {
            this.optptDragPivot = Optional.of(at);
        }
        return want;
    }

    public boolean has() {
        return this.optItem.isPresent();
    }

    public void press() {
        if (has()) {
            val item = optItem.get();
            this.wasSelected = item.selected();
            if (!this.wasSelected) {
                this.model.select(item, true);
            }
        }
    }

    public void drag(final Point2D.Double at) {
        if (has()) {
            val d = Swings.delta(this.optptDragPivot.get(), at);
            if (d.x != 0 && d.y != 0) {
                this.model.moveSelection(d);
                if (DYNAMIC_BOUNDS_UPDATING) {
                    this.model.updateBounds();
                    this.zp.setZoomOutMinFromBounds(this.model.bounds());
                }
                this.optptDragPivot = Optional.of(at);
            }
        }
    }

    public void release(final boolean dragged) {
        if (has()) {
            if (dragged) {
                this.model.updateBounds();
                this.zp.setZoomOutMinFromBounds(this.model.bounds());
            } else if (this.wasSelected) {
                val item = optItem.get();
                this.model.select(item, false);
            }
            this.optItem = Optional.empty();
            this.optptDragPivot = Optional.empty();
        } else if (!dragged) {
            this.model.clearSelection();
        }
    }
}
