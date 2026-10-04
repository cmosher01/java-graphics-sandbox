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
    private static final Font FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 7);
    private static final Color RECT_FILL_COLOR = Swings.mix(Solarized.MAGENTA, Solarized.BASE__2_BEIGE_DRK, .2);

    private final InteractiveRectModel model;
    private final ZoomPanModel modelZoomPan;



    public void paint(final Graphics2D g) {
        if (this.model.selected()) {
            g.setColor(RECT_FILL_COLOR);
        } else {
            if (showDetails()) {
                g.setColor(Solarized.BASE__3_BEIGE_BRT);
            } else {
                g.setColor(Solarized.BASE__1_GRAY__BRT);
            }
        }

        g.fill(this.model.rect());

        if (showDetails()) {
            g.setStroke(Swings.simpleStroke());
            g.setColor(Solarized.BASE__0_GRAY__LGT);
            g.draw(this.model.rect());

            g.setFont(FONT);
            if (this.model.selected()) {
                g.setColor(Solarized.MAGENTA);
            } else {
                g.setColor(Solarized.BASE_02_BLACK_BRT);
            }
            // TODO don't hard-code offsets:
            g.drawString(this.model.tag(), (float)this.model.rect().x+2F, (float)this.model.rect().y+18F);
        }
    }

    public InteractiveRectModel model() {
        return this.model;
    }



    private boolean showDetails() {
        return 0.1 <= this.modelZoomPan.zoomFactor();
    }
}
