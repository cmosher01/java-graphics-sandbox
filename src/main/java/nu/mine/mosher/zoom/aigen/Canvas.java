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

import javax.swing.*;
import java.awt.*;

public abstract class Canvas extends JPanel {
    private static final boolean DOUBLE_BUFFERED = true;
    private static final LayoutManager NO_LAYOUT_MANAGER = null;

    public Canvas() {
        super(NO_LAYOUT_MANAGER, DOUBLE_BUFFERED);
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        if (!(g instanceof Graphics2D g2)) {
            throw new IllegalStateException("Graphics2D context is required.");
        }

        super.paintComponent(g2);
        drawOnto(g2);
    }

    protected abstract void drawOnto(final Graphics2D g);
}
