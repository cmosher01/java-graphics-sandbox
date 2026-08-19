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

package nu.mine.mosher.zoom.aigen;

import java.awt.event.*;
import java.util.*;

public class PanZoom extends MouseAdapter {
    private static final double MIN_ZOOM = 0.0005;
    private static final double MAX_ZOOM = 5.0;
    private static final double ZOOM_SPEED = 0.05;

    private double panX;
    private double panY;
    private double zoomFactor = 1.0;

    private boolean isPanning;
    private int lastMouseX;
    private int lastMouseY;
//    private Set<SimpleRectangleWhy> draggedShapes = Collections.emptySet();

    private boolean cacheNeedsRebuild = true;
//
//    @Override
//    public void mousePressed(MouseEvent e) {
//        int mouseX = e.getX();
//        int mouseY = e.getY();
//        lastMouseX = mouseX;
//        lastMouseY = mouseY;
//        shapes = Collections.emptySet();
//        isPanning = false;
//
//        // Convert screen pixel space back into Infinite World Space
//        int worldMouseX = (int) ((mouseX - panX) / zoomFactor);
//        int worldMouseY = (int) ((mouseY - panY) / zoomFactor);
//
//        // Loop backward through the array to detect shape clicks
//        int size = shapes.size();
//        for (val r : shapes) {
//            if (worldMouseX >= r.x() && worldMouseX <= (r.x() + r.width()) &&
//                    worldMouseY >= r.y() && worldMouseY <= (r.y() + r.height())) {
//
//                draggedShape = r;
//                shapeOffsetX = worldMouseX - r.x();
//                shapeOffsetY = worldMouseY - r.y();
//                break;
//            }
//        }
//
//        if (draggedShape != null) {
//            cacheNeedsRebuild = true; // Exclude moving element from background cache
//        } else {
//            isPanning = true;
//        }
//        repaint();
//    }
//
//    @Override
//    public void mouseDragged(MouseEvent e) {
//        int mouseX = e.getX();
//        int mouseY = e.getY();
//
//        if (draggedShape != null) {
//            draggedShape.x = (int) ((mouseX - panX) / zoomFactor) - shapeOffsetX;
//            draggedShape.y = (int) ((mouseY - panY) / zoomFactor) - shapeOffsetY;
//            repaint();
//        }
//        else if (isPanning) {
//            panX += (mouseX - lastMouseX);
//            panY += (mouseY - lastMouseY);
//            lastMouseX = mouseX;
//            lastMouseY = mouseY;
//            cacheNeedsRebuild = true;
//            repaint();
//        }
//    }
//
//    @Override
//    public void mouseReleased(MouseEvent e) {
//        if (draggedShape != null) {
//            draggedShape = null;
//            cacheNeedsRebuild = true;
//        }
//        isPanning = false;
//        repaint();
//    }
//
//    @Override
//    public void mouseWheelMoved(MouseWheelEvent e) {
//        final double oldZoom = zoomFactor;
//        zoomFactor = Math.clamp(zoomFactor / Math.exp(e.getWheelRotation() * ZOOM_SPEED), MIN_ZOOM, MAX_ZOOM);
//        final double deltaZoom = zoomFactor/oldZoom;
//
//        final double mouseX = e.getX();
//        final double mouseY = e.getY();
//        panX = mouseX - (mouseX - panX) * deltaZoom;
//        panY = mouseY - (mouseY - panY) * deltaZoom;
//
//        cacheNeedsRebuild = true;
//        repaint();
//    }
}
