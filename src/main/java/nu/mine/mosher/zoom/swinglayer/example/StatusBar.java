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

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class StatusBar extends JPanel {
    private static final Font FONT = new Font("Courier New", Font.PLAIN, 14);

    private final Status status;
    private final JLabel label;

    public StatusBar(final Status status) {
        super(new BorderLayout());
        this.status = status;

        setBorder(new EmptyBorder(4,14,5,14));
        setBackground(Solarized.BASE_2);

        this.label = new JLabel();
        this.label.setBackground(this.getBackground());
        this.label.setFont(FONT);
        add(this.label, BorderLayout.LINE_START);
    }

    public void refresh() {
        this.label.setText(this.status.get());
    }
}
