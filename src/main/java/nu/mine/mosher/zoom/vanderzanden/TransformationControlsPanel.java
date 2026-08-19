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

package nu.mine.mosher.zoom.vanderzanden;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Formatter;

class TransformationControlsPanel extends JPanel {
    private final Transform transform;
    // variables for formatting a mouse point
    StringBuilder formatString = new StringBuilder();
    Formatter ptFormatter = new Formatter(formatString);
    JLabel mouseX = new JLabel("X: 0");
    JLabel mouseY = new JLabel("Y: 0");
    JPanel mousePanel = new JPanel();

    public TransformationControlsPanel(Transform transform) {
        this.transform = transform;
        JSlider rotateSlider = new JSlider(JSlider.HORIZONTAL, 0, 360, 0);
        JSlider xTranslateSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 0);
        JSlider yTranslateSlider = new JSlider(JSlider.HORIZONTAL, 0, 300, 0);
        JSlider xScaleSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 100);
        JSlider yScaleSlider = new JSlider(JSlider.HORIZONTAL, 0, 200, 100);
        JLabel rotateLabel = new JLabel("rotation angle: ");
        JLabel xTranslateLabel = new JLabel("x translation: ");
        JLabel yTranslateLabel = new JLabel("y translation: ");
        JLabel xScaleLabel = new JLabel("x zoom percentage: ");
        JLabel yScaleLabel = new JLabel("y zoom percentage: ");

        // add action listeners
        rotateSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider) ce.getSource();
                transform.angle = Math.toRadians(slider.getValue());
                transform.mainCanvas.repaint();
            }
        });
        xTranslateSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider) ce.getSource();
                transform.translateX = slider.getValue();
                transform.mainCanvas.repaint();
            }
        });

        yTranslateSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider) ce.getSource();
                transform.translateY = slider.getValue();
                transform.mainCanvas.repaint();
            }
        });

        xScaleSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider) ce.getSource();
                transform.scaleX = Math.max(0.00001, slider.getValue() / 100.0);
                transform.mainCanvas.repaint();
            }
        });

        yScaleSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ce) {
                JSlider slider = (JSlider) ce.getSource();
                transform.scaleY = Math.max(0.00001, slider.getValue() / 100.0);
                transform.mainCanvas.repaint();
            }
        });

        rotateSlider.setMinorTickSpacing(9);
        rotateSlider.setMajorTickSpacing(45);
        xTranslateSlider.setMinorTickSpacing(10);
        xTranslateSlider.setMajorTickSpacing(50);
        yTranslateSlider.setMinorTickSpacing(10);
        yTranslateSlider.setMajorTickSpacing(50);
        xScaleSlider.setMinorTickSpacing(5);
        xScaleSlider.setMajorTickSpacing(25);
        yScaleSlider.setMinorTickSpacing(5);
        yScaleSlider.setMajorTickSpacing(25);

        // enable painting of tick marks in addSliderRows
        GridBagLayout layoutMgr = new GridBagLayout();
        setLayout(new BorderLayout());
        JPanel controlsPanel = new JPanel(layoutMgr);
        JLabel[] labels = {rotateLabel, xTranslateLabel, yTranslateLabel,
                xScaleLabel, yScaleLabel};
        JSlider[] sliders = {rotateSlider, xTranslateSlider,
                yTranslateSlider,
                xScaleSlider, yScaleSlider};
        addSliderRows(labels, sliders, layoutMgr, controlsPanel);
        mousePanel.add(mouseX);
        mousePanel.add(mouseY);
        this.add(controlsPanel, BorderLayout.CENTER);
        this.add(mousePanel, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEtchedBorder());
    }

    /* lay out the labels and sliders so that the labels are
       right aligned with one another and so that the sliders
       get all the additional space allocated to them */
    void addSliderRows(JLabel[] labels,
                       JSlider[] sliders,
                       GridBagLayout gridbag,
                       Container container) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;
        int numLabels = labels.length;

        for (int i = 0; i < numLabels; i++) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;                       //reset to default
            container.add(labels[i], c);

            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            container.add(sliders[i], c);
            sliders[i].setPaintTicks(true);
            sliders[i].setPaintLabels(true);
        }
    }

    public void setMousePoint(Point2D pt) {
        formatString.setLength(0); // clear the buffer
        ptFormatter.format("%6.0f", pt.getX());
        mouseX.setText("X: " + formatString.toString());
        formatString.setLength(0); // clear the buffer
        ptFormatter.format("%6.0f", pt.getY());
        mouseY.setText("Y: " + formatString.toString());
        mousePanel.revalidate();
        repaint();
    }


    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
}
