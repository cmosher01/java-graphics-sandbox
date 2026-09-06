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

// From: https://docs.oracle.com/javase/tutorial/uiswing/examples/painting/
// with my corrections, additions, and refactorings (Chris Mosher)

package nu.mine.mosher.zoom.swinglayer.example;

import lombok.*;
import nu.mine.mosher.zoom.swinglayer.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class JavaTutorialSwingPaintDemo4 {
    @SneakyThrows
    public static void main(final String... args) {
        System.setProperty("sun.awt.noerasebackground", "true");
        System.setProperty("swing.boldMetal", "false");
        System.setProperty("sun.java2d.opengl", "true");
        SwingUtilities.invokeAndWait(JavaTutorialSwingPaintDemo4::createAndShowGUI);
    }

    @SneakyThrows
    private static void createAndShowGUI() {
//        for (val ffn : GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()) {
//            System.out.println(ffn);
//        }

//        JFrame.setDefaultLookAndFeelDecorated(true);
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

        val f = new JFrame("Swing Paint Demo");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);



        f.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));



        val zp = new ZoomPan();
        val zpm = new ZoomPanMouse(zp);
        val model = new RedSquaresModel();
        zp.setZoomOutMinFromBounds(model.bounds());
        val modelMouse = new RedSquaresMouse(model, zp);
        val modelDragSelection = new DragSelectionModel();
        val mouseCtlrDragSelection = new DragSelectionMouse(modelDragSelection, model, zp);
        val panel = new MyPanel(model, zp, modelDragSelection);
        val status = new Status(zp, panel);
        val sb = new StatusBar(status);



//        val uiZoomPan = new ZoomPanUi(zp);
//        val panelZoomPan = new JLayer<>(panel, uiZoomPan);
//        f.getContentPane().add(panelZoomPan, BorderLayout.CENTER);

        f.getContentPane().add(panel, BorderLayout.CENTER);
        f.getContentPane().add(sb, BorderLayout.PAGE_END);

        f.setGlassPane(new MyGlassPane(panel, sb, zp, zpm, status, modelMouse, mouseCtlrDragSelection));
        f.getGlassPane().setVisible(true);
        f.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(final WindowEvent e) {
                f.getGlassPane().dispatchEvent(new MouseEvent(f, MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(),
                    0, 0, 0, 1, false, MouseEvent.BUTTON1));
            }
        });



        final var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final int w = (int)Math.round(Math.rint(0.80D * screenSize.getWidth ()));
        final int h = (int)Math.round(Math.rint(0.80D * screenSize.getHeight()));
        f.setSize(w, h);
        f.setLocationRelativeTo(null);



        // for testing: this should never show up in the GUI
//        f.setBackground(Solarized.GREEN);



        f.setVisible(true);
    }
}
