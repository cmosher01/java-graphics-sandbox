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

package dev.cmosher01.genealogy.xy;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public abstract class Canvas extends JPanel {
    private static final LayoutManager NO_LAYOUT_MANAGER = null;
    private static final boolean DOUBLE_BUFFERED = true;

    public Canvas(final Color bg) {
        super(NO_LAYOUT_MANAGER, DOUBLE_BUFFERED);
        setOpaque(true);
        setBackground(bg);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (!(g instanceof Graphics2D g2)) {
            throw new IllegalStateException("Graphics2D context is required.");
        }

        super.paintComponent(g2);

        g2.addRenderingHints(Swings.GLOBAL_RENDERING_HINTS);
        g2.setStroke(Swings.simpleStroke());
        g2.setClip(viewportClip());

        paint(g2);
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



    protected abstract void paint(final Graphics2D g);
}
