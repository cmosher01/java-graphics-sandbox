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

import static nu.mine.mosher.zoom.swinglayer.example.Solarized.*;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class MainView extends JPanel {
    private static final LayoutManager NO_LAYOUT_MANAGER = null;

    private static final boolean BACKGROUND_FILL = true;
    private static final Color BACKGROUND_COLOR = BASE__1_GRAY__BRT;

    private static final boolean AXES = true;



    private final ZoomPanModel modelZoomPan;
    private final ZoomPanView viewZoomPan;
    private final InteractiveRectsView viewRects;
    private final DragSelectionView viewDragSelection;
    private final AxesView viewAxes;


    public MainView(final ZoomPanModel modelZoomPan, final ZoomPanView viewZoomPan, final InteractiveRectsView viewRects, final DragSelectionView viewDragSelection, final AxesView viewAxes) {
        super(NO_LAYOUT_MANAGER);
        this.modelZoomPan = modelZoomPan;
        this.viewZoomPan = viewZoomPan;
        this.viewRects = viewRects;
        this.viewDragSelection = viewDragSelection;
        this.viewAxes = viewAxes;

        setOpaque(false);
    }



    @Override
    public void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);

        if (graphics instanceof final Graphics2D g) {
            g.addRenderingHints(Swings.GLOBAL_RENDERING_HINTS);
            g.setStroke(Swings.simpleStroke());
            g.setClip(viewportClip());

            if (BACKGROUND_FILL) {
                g.setColor(BACKGROUND_COLOR);
                g.fill(viewportClip());
            }



            this.viewZoomPan.paint(g);

            val clip = clip();

            this.viewRects.paintBackground(g);

            if (AXES) {
                this.viewAxes.paint(g, super.getWidth(), super.getHeight());
            }

            this.viewRects.paint(g, clip);
            this.viewDragSelection.paint(g, this.modelZoomPan);
        }
    }



    /**
     * Calculates the clipping rectangle in viewport coordinates
     * @return viewport
     */
    public Rectangle2D.Double viewportClip() {
        return new Rectangle2D.Double(
            super.getX(), super.getY(),
            Math.max(1, super.getWidth()), Math.max(1, super.getHeight()));
    }

    /**
     * Calculates the clipping rectangle in canvas coordinates
     * @return clipping rect
     */
    public Rectangle2D.Double clip() {
        return this.modelZoomPan.viewportToCanvas(viewportClip());
    }
}
