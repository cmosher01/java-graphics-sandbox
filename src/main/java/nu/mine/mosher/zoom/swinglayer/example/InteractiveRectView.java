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
public class InteractiveRectView {
    private final InteractiveRectModel model;
    private final ZoomPanModel modelZoomPan;

    private static final Stroke STROKE = new BasicStroke(100f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 7);

    public void paint(final Graphics2D g) {
        if (this.model.selected()) {
            g.setColor(Solarized.MAGENTA);
        } else {
//            g.setColor(Color.WHITE);
            if (0.1D <= this.modelZoomPan.zoomFactor()) {
                g.setColor(Solarized.BASE__2_BEIGE_DRK);
            } else {
                g.setColor(Solarized.BASE__1_GRAY__BRT);
            }
        }

        // fill and fillRect seem equally fast, and sufficient:
        // there is only the slightest perceptible lagging when quickly
        // panning a million rectangles within the clipping region
        g.fill(this.model);
//        g.fillRect((int)this.model().x, (int)this.model().y, (int)this.model().width, (int)this.model().height);




        if (0.1D <= this.modelZoomPan.zoomFactor()) {
            g.setStroke(Swings.simpleStroke());
            g.setColor(Solarized.BASE_02_BLACK_BRT);
            g.draw(this.model);
//        g.drawRect((int)this.model().x, (int)this.model().y, (int)this.model().width, (int)this.model().height);

            g.setFont(FONT);
            g.setColor(Solarized.BASE_02_BLACK_BRT);
            g.drawString(this.model.tag(), (int) this.model.x + 5, (int) this.model.y + 18);
        }
    }

    public InteractiveRectModel model() {
        return this.model;
    }
}
