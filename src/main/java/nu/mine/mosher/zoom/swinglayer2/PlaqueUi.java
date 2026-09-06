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

import javax.swing.*;
import javax.swing.plaf.LayerUI;
import java.awt.*;

public class PlaqueUi extends LayerUI<JComponent> {
    @Override
    public void paint(Graphics g, JComponent c) {
        System.out.println("PlaqueUi.paint 1");
        System.out.println("Graphics: "+ggg(g));
        super.paint(g, c);
        if (g instanceof Graphics2D g2) {
            g2.setColor(new Color(0, 255, 0));
            g2.fillRect(200, 200, 200, 100);
        }
        System.out.println("PlaqueUi.paint 2");
    }

    public static String ggg(Object o) {
        return o.getClass().getName() + "@" + Integer.toHexString(o.hashCode());
    }
}
