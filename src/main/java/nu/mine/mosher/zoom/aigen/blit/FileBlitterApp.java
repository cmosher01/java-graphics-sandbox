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

/* Example of creating bitmaps and blitting them. Gemini AI generated, with my prompting. */

package nu.mine.mosher.zoom.aigen.blit;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileBlitterApp extends JFrame {
    // Helper class to bundle the bitmap frame with its spatial center coordinates
    private static class PositionedBitmap {
        final BufferedImage bitmap;
        final int centerX;
        final int centerY;

        public PositionedBitmap(BufferedImage bitmap, int centerX, int centerY) {
            this.bitmap = bitmap;
            this.centerX = centerX;
            this.centerY = centerY;
        }
    }

    public FileBlitterApp(String filePath) {
        setTitle("Centered Coordinate File Text Blitter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        List<PositionedBitmap> positionedBitmaps = createBitmapsFromFile(filePath);

        add(new BlitPanel(positionedBitmaps));
        setLocationRelativeTo(null);
    }

    private List<PositionedBitmap> createBitmapsFromFile(String filePath) {
        List<PositionedBitmap> positionedBitmaps = new ArrayList<>();
        Font font = new Font("Arial", Font.PLAIN, 20);

        BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D tempG = tempImg.createGraphics();
        tempG.setFont(font);
        FontMetrics fm = tempG.getFontMetrics();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(" +", 3);
                if (parts.length < 3) {
                    System.err.println("Skipping malformed line: " + line);
                    continue;
                }

                try {
                    int centerX = (int)Math.round(Math.rint(Double.parseDouble(parts[0].trim())));
                    int centerY = (int)Math.round(Math.rint(Double.parseDouble(parts[1].trim())));
                    String text = parts[2].trim();

                    if (text.isEmpty()) text = " ";

                    int width = fm.stringWidth(text);
                    int height = fm.getHeight();

                    BufferedImage lineBitmap = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = lineBitmap.createGraphics();

                    g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2d.setFont(font);
                    g2d.setColor(Color.BLACK);

                    g2d.drawString(text, 0, fm.getAscent());
                    g2d.dispose();

                    positionedBitmaps.add(new PositionedBitmap(lineBitmap, centerX, centerY));

                } catch (NumberFormatException nfe) {
                    System.err.println("Failed to parse coordinates on line: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } finally {
            tempG.dispose();
        }
        return positionedBitmaps;
    }

    // Custom Panel responsible for blitting text bitmaps centered on target coordinates
    private static class BlitPanel extends JPanel {
        private final List<PositionedBitmap> positionedBitmaps;

        public BlitPanel(List<PositionedBitmap> positionedBitmaps) {
            this.positionedBitmaps = positionedBitmaps;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            // Optional: Enable bilinear rendering hints for cleaner pixel interpolation if panels scale
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            for (PositionedBitmap pb : positionedBitmaps) {
                // Calculate top-left corner by offsetting half the dimensions from the center point
                int topLeftX = pb.centerX - (pb.bitmap.getWidth() / 2);
                int topLeftY = pb.centerY - (pb.bitmap.getHeight() / 2);

                // Blit the image using the newly calculated boundary origins
                g2d.drawImage(pb.bitmap, topLeftX, topLeftY, null);
            }
        }
    }

    public static void main(String[] args) {
        String sampleFilePath = "Eaton.xy";

        SwingUtilities.invokeLater(() -> {
            new FileBlitterApp(sampleFilePath).setVisible(true);
        });
    }
}
