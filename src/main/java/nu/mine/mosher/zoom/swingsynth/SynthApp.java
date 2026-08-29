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

package nu.mine.mosher.zoom.swingsynth;

import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.*;

public class SynthApp {
    @SneakyThrows
    public static void main(String[] args) {
        SwingUtilities.invokeAndWait(SynthApp::init);
    }

    @SneakyThrows
    private static void init() {
        JFrame.setDefaultLookAndFeelDecorated(true);
        UIManager.setLookAndFeel(MetalLookAndFeel.class.getName());

        JFrame frame = new JFrame("Cross Platform Example");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        JPanel testPanel = new JPanel(new BorderLayout());
        testPanel.setBackground(Color.decode("#252526"));
        testPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        JLabel statusLabel = new JLabel("Mouse is OUTSIDE the panel", SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        statusLabel.setForeground(Color.WHITE);
        testPanel.add(statusLabel, BorderLayout.CENTER);
        frame.getContentPane().add(testPanel, BorderLayout.CENTER);
        frame.setVisible(true);

        JComponent titlePane = null;
        for (Component comp : frame.getRootPane().getLayeredPane().getComponents()) {
            if (comp.getClass().getName().contains("TitlePane")) {
                titlePane = (JComponent) comp;
                titlePane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                break;
            }
        }

        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event instanceof MouseEvent && testPanel.isShowing()) {
                    PointerInfo pointerInfo = MouseInfo.getPointerInfo();
                    if (pointerInfo == null) return;
                    Point mousePoint = pointerInfo.getLocation();

                    Point panelPos = testPanel.getLocationOnScreen();
                    Rectangle panelBounds = new Rectangle(panelPos.x, panelPos.y, testPanel.getWidth(), testPanel.getHeight());

                    if (panelBounds.contains(mousePoint)) {
                        statusLabel.setText("Mouse is INSIDE the panel");
                        testPanel.setBackground(Color.decode("#3E3E42"));
                    } else {
                        statusLabel.setText("Mouse is OUTSIDE the panel");
                        testPanel.setBackground(Color.decode("#252526"));
                    }
                }
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }
}
