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

public final class MainView extends Canvas {
    private static final Color BACKGROUND_COLOR = BASE__1_GRAY__BRT;



    private final ZoomPanModel modelZoomPan;
    private final ZoomPanView viewZoomPan;
    private final InteractiveRectsModel modelRects;
    private final InteractiveRectsView viewRects;
    private final DragSelectionView viewDragSelection;
    private final AxesView viewAxes;


    public MainView(final ZoomPanModel modelZoomPan, final ZoomPanView viewZoomPan, InteractiveRectsModel modelRects, final InteractiveRectsView viewRects, final DragSelectionView viewDragSelection, final AxesView viewAxes) {
        super(BACKGROUND_COLOR);
        this.modelZoomPan = modelZoomPan;
        this.viewZoomPan = viewZoomPan;
        this.modelRects = modelRects;
        this.viewRects = viewRects;
        this.viewDragSelection = viewDragSelection;
        this.viewAxes = viewAxes;
    }



    @Override
    public void paint(final Graphics2D g) {
        if (this.modelRects.isPresent()) {
            this.viewZoomPan.paint(g);
            this.viewRects.paintBackground(g);
//            this.viewAxes.paint(g, super.getWidth(), super.getHeight());
            this.viewRects.paint(g, clip());
            this.viewDragSelection.paint(g, this.modelZoomPan);
        }
    }



    /**
     * Calculates the clipping rectangle in canvas coordinates
     * @return clipping rect
     */
    public Rectangle2D.Double clip() {
        return this.modelZoomPan.viewportToCanvas(viewportClip());
    }
}
