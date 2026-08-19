/*
 *     Copyright © 2026, Christopher Alan Mosher, New York, New York, USA, <cmosher01@gmail.com>.
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

package nu.mine.mosher.zoom.swingpanel;

import lombok.val;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;

class ZoomPanPanelEventHandler extends MouseAdapter {
    private final Component repaintable;
    private final ZoomPan zp;
    private Point pDragPivot;

    public ZoomPanPanelEventHandler(final ZoomPan zp, final Component repaintable) {
        this.repaintable = repaintable;
        this.zp = zp;
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        val p = e.getPoint();
        this.pDragPivot = p;
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        if (Objects.nonNull(this.pDragPivot)) {
            val p = e.getPoint();
            this.zp.pan(p.getX() - this.pDragPivot.x, p.getY() - this.pDragPivot.y);
            this.pDragPivot = p;
            this.repaintable.repaint();
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        if (Objects.nonNull(this.pDragPivot)) {
            this.pDragPivot = null;
        }
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        val p = e.getPoint();
        this.zp.zoom(e.getWheelRotation(), p.getX(), p.getY());
        this.repaintable.repaint();
    }
}
