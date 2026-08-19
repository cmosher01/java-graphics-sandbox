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
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

class TransPanel extends JPanel {
    private final Transform transform;
    /* The object for the two handles */
    Rectangle2D handle = new Rectangle2D.Double(0, 0, 20, 20);

    /* The rectangle that gets transformed */
    Rectangle2D target = new Rectangle2D.Double(0, 0, 200, 100);

    /* The position where the mouse was last pressed */
    int mouseX = 0;
    int mouseY = 0;

    AffineTransform at;  // the current affine transform

    TransPanel(Transform transform) {
        this.transform = transform;
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // record the mouse point
                mouseX = e.getX();
                mouseY = e.getY();

                // transform the mouse point to the rectangle's coordinate space
                // and save the transformed point. You can do a sanity
                // check on the transformed point's value by comparing it with
                // the upper left corner of the rectangle. Do the transformed
                // values appear to lie at the right point relative to the
                // upper left corner of the rectangle? They should.
                Point2D mousePoint = new Point2D.Double(mouseX, mouseY);
                try {
                    Point2D XFormedPoint = at.inverseTransform(mousePoint, null);
                    transform.controlsCanvas.setMousePoint(XFormedPoint);
                } catch (NoninvertibleTransformException te) {
                    System.out.println(te);
                }
            }
        });
        setBorder(BorderFactory.createEtchedBorder());
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // get a copy of the current transform so we can restore it later
        AffineTransform saveTransform = g2.getTransform();

        // this is the copy we will modify
        at = g2.getTransform();
        Color saveColor = g2.getColor();

        Dimension d = getSize();
        int width = d.width;
        int height = d.height;
        Line2D xAxis = new Line2D.Double(0, 0, width, 0);
        Line2D yAxis = new Line2D.Double(0, 0, 0, height);

        // java applies transforms in a LIFO order, so the first
        // transform you see here is actually the last one applied

        // adjust the rectangle's coordinate system by the current
        // transformation amounts
        at.translate(transform.translateX, transform.translateY);

        // adjust the rectangle's coordinate system so that it starts
        // inside the insets for this canvas and so that the upper
        // left handle is fully displayed
        Insets insets = getInsets();
        at.translate(insets.left + handle.getWidth(),
                insets.top + handle.getHeight());

        // notice that we rotate about the target's center, rather than
        // about the origin of the coordinate system
        at.rotate(transform.angle, target.getWidth() / 2, target.getHeight() / 2);

        // the first transformation we do is a scaling transformation.
        // we do the scaling transformation first so that parallel
        // lines and perpendicular lines are maintained. If we switch
        // the order of the rotate and scale, the rectangle will be
        // turned into a diamond
        at.scale(transform.scaleX, transform.scaleY);


        g2.setTransform(at);

        // draw the coordinate axes and the rectangle
        g2.setColor(Color.blue);
        g2.draw(xAxis);
        g2.draw(yAxis);
        g2.setColor(Color.black);
        g2.draw(target);

        // translate the first handle to the upper left corner and then
        // rotate it by 45 degrees
        g2.translate(-handle.getWidth() / 2,
                -handle.getHeight() / 2);
        g2.rotate(Math.PI / 4, handle.getWidth() / 2, handle.getHeight() / 2);
        g2.fill(handle);

        // get back the rectangle's coordinate system so that the second
        // handle can be positioned. We first move the handle so that
        // its upper left corner lies at the lower right corner of the
        // rectangle. Then we scale the handle to its correct size and
        // center it about the lower right corner of the rectangle. It
        // might seem that the two translations can be combined but that
        // is not possible. Suppose the two translations are combined.
        // The resulting translation must be:
        //  (target.wd - handle.wd / 2), (target.ht - handle.ht / 2)
        // This translation must either precede the scaling or succeed
        // it. Let's examine both cases:
        //
        // 1) The translation precedes the scaling: The translation
        //    set's the handles coordinate space to start at the same
        //    point where the handle's upper, left corner should
        //    ultimately reside. When the scaling is applied, it
        //    stretches the coordinate space starting at this upper,
        //    left corner. The result is that the upper, left corner
        //    of the handle is in the correct position but the lower,
        //    right corner gets stretched too far (1/4 of the handle lies
        //    to the left of the rectangle's lower right corner and 3/4
        //    of the handle lies to the right of the rectangle's lower
        //    right corner.
        //
        // 2) The translation succeeds the scaling: The scaling is now
        //    applied to the rectangle's entire coordinate space, which
        //    means that the translation will get elongated to twice
        //    the length you want it to. The handle will now show up
        //    well to the right of the lower, right corner of the
        //    rectangle. The problem is that the appropriate scaling gets
        //    applied to the handle.getWidth part of the translation but
        //    the wrong scaling gets applied to the target.getWidth part
        //    of the translation. By doing the translation for
        //    target.getWidth before the coordinate space gets elongated,
        //    the handle gets placed properly.
        g2.setTransform(at);
        g2.translate(target.getWidth(), target.getHeight());
        g2.scale(2.0, 1.0);
        g2.translate(-handle.getWidth() / 2, -handle.getHeight() / 2);
        g2.fill(handle);
        g2.setTransform(saveTransform);
        g2.setColor(saveColor);
    }

    public Dimension getPreferredSize() {
        return new Dimension(400, 400);
    }
}
