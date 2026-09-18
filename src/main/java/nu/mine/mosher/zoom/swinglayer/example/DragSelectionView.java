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

import lombok.RequiredArgsConstructor;

import java.awt.*;

@RequiredArgsConstructor
public class DragSelectionView {
    private static final Color RECT_FILL_COLOR = new Color(38, 139, 210, 10);
    private static final Color RECT_STROKE_COLOR = new Color(38, 139, 210);

    private final DragSelectionModel model;

    public void paint(final Graphics2D g, ZoomPanModel zp) {
        if (this.model.selecting()) {
            g.setColor(RECT_FILL_COLOR);
            g.fill(zp.viewportToCanvas(this.model.bounds()));

            g.setColor(RECT_STROKE_COLOR);
            g.draw(zp.viewportToCanvas(this.model.bounds()));
        }
    }
}
