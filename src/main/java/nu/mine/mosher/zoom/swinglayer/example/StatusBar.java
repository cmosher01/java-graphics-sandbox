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

import nu.mine.mosher.zoom.swinglayer.Solarized;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Objects;

public final class StatusBar extends JPanel {
    private static final String DEFAULT_TEXT = " ";
    private static final Font FONT = new Font("Courier New", Font.PLAIN, 14);

    private final JLabel label;

    public StatusBar() {
        super(new BorderLayout());
        setBorder(new EmptyBorder(4,14,5,14));
        setBackground(Solarized.BASE_2);

        this.label = new JLabel(DEFAULT_TEXT);
        this.label.setBackground(this.getBackground());
        this.label.setFont(FONT);
        add(this.label, BorderLayout.LINE_START);
    }

    public void setText(final String s) {
        final String t;
        if (Objects.isNull(s) || s.isBlank()) {
            t = DEFAULT_TEXT;
        } else {
            t = s.strip();
        }
        this.label.setText(t);
    }
}
