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

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;

import static nu.mine.mosher.zoom.swinglayer.example.Solarized.*;

public class InteractiveRectsView {
    private final InteractiveRectsModel model;
    private final ArrayList<InteractiveRectView> views = new ArrayList<>();

    private static final boolean BOUNDS_FILL = true;
    private static final Color BOUNDS_FILL_COLOR = BASE__3_BEIGE_BRT;

    private static final boolean BOUNDS_DRAW = true;
    private static final Color BOUNDS_DRAW_COLOR = BASE_01_GRAY__DRK;
    private static final Stroke BOUNDS_DRAW_STROKE = Swings.simpleStroke(100);

    public InteractiveRectsView(final InteractiveRectsModel model) {
        this.model = model;
        this.model.sqs().forEach(modelRect -> this.views.add(new InteractiveRectView(modelRect)));
    }


    public void paintBackground(final Graphics2D g) {
        if (BOUNDS_FILL) {
            g.setColor(BOUNDS_FILL_COLOR);
            g.fill(this.model.boundsOutset());
        }

        if (BOUNDS_DRAW) {
            g.setStroke(BOUNDS_DRAW_STROKE);
            g.setColor(BOUNDS_DRAW_COLOR);
            g.draw(this.model.boundsOutset());
        }
    }

    public void paint(final Graphics2D g, final Rectangle2D clip) {
        this.views.forEach(sq -> {
            if (sq.model().intersects(clip)) {
                sq.paint(g);
            }
        });
    }
}
