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

import lombok.val;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

import static nu.mine.mosher.zoom.swinglayer.example.Solarized.BASE_3;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class MainPane extends JPanel {
    private static final LayoutManager NO_LAYOUT_MANAGER = null;

    private static final boolean BACKGROUND_FILL = true;
    private static final Color BACKGROUND_COLOR = BASE_3;

    private static final boolean AXES = true;



    private final InteractiveRectsModel model;
    private final ZoomPanModel zp;
    private final DragSelectionModel modelDragSelection;
    private final AxesModel axes;


    public MainPane(final InteractiveRectsModel model, final ZoomPanModel zp, final DragSelectionModel modelDragSelection, final AxesModel axes) {
        super(NO_LAYOUT_MANAGER);

        this.model = model;
        this.zp = zp;
        this.modelDragSelection = modelDragSelection;
        this.axes = axes;

        setOpaque(false);
    }



    @Override
    public void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);

        if (graphics instanceof final Graphics2D g) {
            g.setStroke(Swings.simpleStroke());

            if (BACKGROUND_FILL) {
                g.setColor(BACKGROUND_COLOR);
                g.fill(viewportClip());
            }

            this.zp.paint(g);

            val clip = clip();
            g.setClip(clip);

            this.model.paintBackground(g, clip);

            if (AXES) {
                this.axes.paint(g, super.getWidth(), super.getHeight());
            }

            this.model.paint(g, clip);
            this.modelDragSelection.paint(g, this.zp);
        }
    }



    /**
     * Calculates the clipping rectangle in viewport coordinates
     * @return viewport
     */
    public Rectangle2D.Double viewportClip() {
        return new Rectangle2D.Double(
            super.getX(), super.getY(),
            Math.max(0, super.getWidth()), Math.max(0, super.getHeight()));
    }

    /**
     * Calculates the clipping rectangle in canvas coordinates
     * @return clipping rect
     */
    public Rectangle2D.Double clip() {
        return this.zp.viewportToCanvas(viewportClip());
    }
}
