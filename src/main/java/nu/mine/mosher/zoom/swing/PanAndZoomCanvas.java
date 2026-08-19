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

package nu.mine.mosher.zoom.swing;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class PanAndZoomCanvas extends JComponent {
    private AffineTransform at;   // the current pan and zoom transform
    private Point2D translate = AwtUtil.zero();
    private double scaleDeltaFromPrev = 1.0D;
    private double scale = 1.0D;
    private Point2D pivotPrev = AwtUtil.zero();
    private Point2D pivot = AwtUtil.zero();
    private Point2D mouse = AwtUtil.zero();

    @Override
    public void paintComponent(final Graphics g) {
        final var gr = (Graphics2D)g;
        // save the original transform so that we can restore
        // it later
        final var saveTransform = gr.getTransform();

        // blank the screen. If we do not call super.paintComponent, then
        // we need to blank it ourselves
        gr.setColor(Color.WHITE);
        gr.fillRect(0, 0, getWidth(), getHeight());

        // We need to add new transforms to the existing
        // transform, rather than creating a new transform from scratch.
        // If we create a transform from scratch, we
        // will start from the upper left of a JFrame,
        // rather than from the upper left of our component
        this.at = new AffineTransform(saveTransform);



        dump();



        // The zooming transformation. Notice that it will be performed
        // after the panning transformation, zooming the panned scene,
        // rather than the original scene
//        this.at.translate(getWidth()/2, getHeight()/2);
        final var xlatex = this.pivot.getX();
        final var xlatey = this.pivot.getY();
        this.at.translate(xlatex, xlatey);
        System.out.printf("xlate s1 : (%6.1f,%6.1f)\n", xlatex, xlatey);

        this.at.scale(this.scale, this.scale);
        System.out.printf("scale current=%9.4f, last_delta=%9.4f\n", this.scale, this.scaleDeltaFromPrev);
//        this.at.translate(-getWidth()/2, -getHeight()/2);

        final var f = -this.scaleDeltaFromPrev;
        this.at.translate(xlatex*f, xlatey*f);
        System.out.printf("xlate s2 : (%6.1f,%6.1f)\n", xlatex*f, xlatey*f);



        // The panning transformation
        this.at.translate(this.translate.getX(), this.translate.getY());
        System.out.printf("xlate pan: (%6.1f,%6.1f)\n", this.translate.getX(), this.translate.getY());



        dump();



        System.out.println("----------------------------------");
        gr.setTransform(this.at);

        // draw the objects
        gr.setColor(Color.BLACK);
        gr.drawRect(50, 50, 50, 50);
        gr.fillOval(100, 100, 100, 100);
        gr.drawString("Test Affine Transform", 50, 30);

        // make sure you restore the original transform or else the drawing
        // of borders and other components might be messed up
        gr.setTransform(saveTransform);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }

    public AffineTransform at() {
        return this.at;
    }

    public double scale() {
        return this.scale;
    }

    public void scale(final double scaleNew) {
        final var scaleOld = this.scale;
        this.scaleDeltaFromPrev = scaleNew/scaleOld;
        this.scale = scaleNew;
    }

    public Point2D pivot() {
        return this.pivot;
    }

    public void pivot(final Point2D pivot) {
        this.pivotPrev = this.pivot;
        this.pivot = pivot;
    }

    public Point2D translate() {
        return this.translate;
    }

    public void translate(final Point2D delta) {
        this.translate = AwtUtil.add(this.translate, delta);
    }


    public void dump() {
        final var xlate = translate();
        final var scale = scale();
        final var xfrmdX = (this.mouse.getX()-xlate.getX())/scale;
        final var xfrmdY = (this.mouse.getY()-xlate.getY())/scale;
        System.out.printf("mouse: viewport=(%5.1f,%5.1f) xlate=(%5.1f,%5.1f) scale=%9.4f canvas=(%5.1f,%5.1f)\n",
                this.mouse.getX(), this.mouse.getY(), xlate.getX(), xlate.getY(), scale, xfrmdX, xfrmdY);
    }

    public void mouse(final Point2D mouse) {
        this.mouse = mouse;
        dump();
    }
}
