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

package nu.mine.mosher.zoom.swinglayer2;

import lombok.val;

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Objects;

import static java.awt.AWTEvent.*;
import static java.awt.event.MouseEvent.*;
import static java.awt.event.MouseEvent.MOUSE_DRAGGED;
import static nu.mine.mosher.zoom.swinglayer.example.Swings.pt2d;
import static nu.mine.mosher.zoom.swinglayer2.PlaqueUi.ggg;

public class PanUi extends LayerUI<JPanel> {
    private double panX = 0.0D;
    private double panY = 0.0D;
    private Point2D.Double pDragPivot;

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        ((JLayer<?>) c).setLayerEventMask(MOUSE_EVENT_MASK | MOUSE_MOTION_EVENT_MASK);
    }

    @Override
    public void uninstallUI(JComponent c) {
        ((JLayer<?>) c).setLayerEventMask(0);
        super.uninstallUI(c);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        System.out.println("PanUi.paint 1");
        System.out.println("Graphics: "+ggg(g));
        if (g instanceof Graphics2D g2) {
            g2.translate(this.panX, this.panY);
        }
        super.paint(g, c);
        System.out.println("PanUi.paint 2");
    }

    @Override
    protected void processMouseEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        mouse(e, l);
    }

    @Override
    protected void processMouseMotionEvent(MouseEvent e, JLayer<? extends JPanel> l) {
        mouse(e, l);
    }

    private void mouse(MouseEvent e, JLayer<? extends JPanel> l) {
        if (e.getID() == MOUSE_PRESSED) {
            val p = pt2d(e.getPoint());
            this.pDragPivot = p;
            l.repaint();
            e.consume();
        } else if (e.getID() == MOUSE_RELEASED) {
            if (Objects.nonNull(this.pDragPivot)) {
                this.pDragPivot = null;
                e.consume();
            }
        } else if (e.getID() == MOUSE_DRAGGED) {
            System.out.println("-----------------\nDRAGGED\n-----------------");
            if (Objects.nonNull(this.pDragPivot)) {
                val p = pt2d(e.getPoint());
                this.panX += p.getX()-this.pDragPivot.getX();
                this.panY += p.getY()-this.pDragPivot.getY();
                System.out.printf("pan: %.1fx%.1f\n", this.panX, this.panY);
                this.pDragPivot = p;
                l.repaint();
                e.consume();
            }
        }
    }
}
