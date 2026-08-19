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

package nu.mine.mosher.zoom.swingevents;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SwingEventBubbleDemo {
    private static final Font FONT = new Font("Courier New", Font.PLAIN, 10);
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Swing Event Bubbling Simulator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 450);
            frame.setLocationRelativeTo(null);

            // 1. Text Area to display the log output
            JTextArea logArea = new JTextArea(8, 40);
            logArea.setFont(FONT);
            logArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(logArea);

            // Helper lambda to append text to log
            java.util.function.Consumer<String> log = (message) -> {
                logArea.append(message + "\n");
                logArea.setCaretPosition(logArea.getDocument().getLength());
            };

            // 2. Create Parent Panel
            JPanel parentPanel = new JPanel();
            parentPanel.setBackground(new Color(173, 216, 230)); // Light Blue
            parentPanel.setLayout(new GridBagLayout()); // Center the child
            parentPanel.setPreferredSize(new Dimension(400, 200));

            // Add standard listener to parent panel
            parentPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    log.accept(" -> [PARENT] Caught the click event at coordinates: (" + e.getX() + ", " + e.getY() + ")");
                }
            });

            // 3. Create Child Panel
            JPanel childPanel = new JPanel() {
                // Draw a clear target area in the middle of the child panel
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.DARK_GRAY);
                    g.drawRect(40, 25, 120, 50);
                    g.drawString("CLICK INSIDE ME", 50, 45);
                    g.drawString("(Child Handles It)", 50, 65);
                }
            };
            childPanel.setBackground(new Color(255, 182, 193)); // Light Pink
            childPanel.setPreferredSize(new Dimension(200, 100));

            // 4. Implement Child-First Fallback Logic
            childPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    log.accept("[CHILD] Click detected inside child boundaries.");

                    // Define the target zone rectangle (X: 40, Y: 25, Width: 120, Height: 50)
                    Rectangle targetZone = new Rectangle(40, 25, 120, 50);

                    // Check if the user clicked inside the child's active zone
                    if (targetZone.contains(e.getPoint())) {
                        log.accept(" -> [CHILD] Click fell inside target zone! Handled by child. Event consumed.");
                        e.consume(); // Prevents underlying OS/peer defaults, but does not move event anywhere
                    } else {
                        log.accept(" -> [CHILD] Missed target zone! Passing event up to parent...");

                        // Translate child coordinates to parent component coordinates
                        MouseEvent parentEvent = SwingUtilities.convertMouseEvent(childPanel, e, parentPanel);

                        // Manually fire the converted event on the parent container
                        parentPanel.dispatchEvent(parentEvent);
                    }
                }
            });

            // Assemble Layout
            parentPanel.add(childPanel);

            frame.setLayout(new BorderLayout());
            frame.add(parentPanel, BorderLayout.SOUTH);
            frame.add(scrollPane, BorderLayout.CENTER);

            // Instructions text at the top
            JLabel instructions = new JLabel(
                    "<html><center><br><b>Instructions:</b> Click the <b>Pink</b> area outside the box to bubble up.<br>" +
                            "Click <i>inside</i> the <b>Dark Gray Box</b> to let the child consume it.</center><br></html>",
                    SwingConstants.CENTER
            );
            frame.add(instructions, BorderLayout.NORTH);

            frame.setVisible(true);
        });
    }
}
