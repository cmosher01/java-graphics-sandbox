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
import nu.mine.mosher.zoom.swinglayer.ZoomPan;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;


@RequiredArgsConstructor
public class DragSelectionMouse {
    private final DragSelectionModel model;
    private final RedSquaresModel modelItems;
    private final ZoomPan zp;



    private Point2D.Double ptOrig;
    private Point2D.Double ptCurr;



    public void press(final Point2D.Double at) {
        this.ptOrig = at;
        this.ptCurr = at;

        this.model.set(this.ptOrig, this.ptCurr);
        this.modelItems.setSelectionFromRectangle(this.zp.viewportToCanvas(this.model.bounds()));
    }

    public void drag(final Point2D.Double at) {
        if (!at.equals(this.ptCurr)) {
            this.ptCurr = at;

            this.model.set(this.ptOrig, this.ptCurr);
            this.modelItems.setSelectionFromRectangle(this.zp.viewportToCanvas(this.model.bounds()));
        }
    }

    public void release() {
        this.model.clear();
    }

    public boolean selecting() {
        return this.model.selecting();
    }
}
