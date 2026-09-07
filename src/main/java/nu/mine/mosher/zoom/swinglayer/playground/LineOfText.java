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

package nu.mine.mosher.zoom.swinglayer.playground;

import lombok.val;
import nu.mine.mosher.zoom.swinglayer.example.Solarized;

import java.awt.*;
import java.awt.font.*;
import java.awt.geom.*;

public class LineOfText {
    private static Font FONT = new Font("Courier New", Font.PLAIN, 8);
    private final String s;
    private final Rectangle2D.Double bounds;
    private final Point2D at;

    public LineOfText(final String s, final Point2D at) {
        this.s = s;
        this.at = at;
        this.bounds = bounds(s, at);
    }

    private static Rectangle2D.Double bounds(final String s, final Point2D at) {
        val ctx = new FontRenderContext(new AffineTransform(), true, true);
        val layout = new TextLayout(s, FONT, ctx);
        val r = layout.getPixelBounds(ctx, (float)at.getX(), (float)at.getY());
        return new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public void paint(final Graphics2D g) {
        g.setColor(Solarized.VIOLET);
        g.setFont(FONT);
        g.drawString(s, (float)at.getX(), (float)at.getY());
    }

    public Rectangle2D.Double bounds() {
        return (Rectangle2D.Double)this.bounds.getBounds2D();
    }
}
