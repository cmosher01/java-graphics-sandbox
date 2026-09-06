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

import java.awt.*;

public class RedSquare {
    private int xPos = 50;
    private int yPos = 50;
    private int width = 20;
    private int height = 20;

    public void setX(int xPos) {
        this.xPos = xPos - height / 2;
    }

    public int getX() {
        return xPos;
    }

    public void setY(int yPos) {
        this.yPos = yPos - width / 2;
    }

    public int getY() {
        return yPos;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void paintSquare(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(xPos, yPos, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(xPos, yPos, width, height);
    }
}
