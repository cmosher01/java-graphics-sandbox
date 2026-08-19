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
/*
https://web.eecs.utk.edu/~bvanderz/teaching/guiSp11/notes/transforms/transform.html

https://web.archive.org/web/20070126154004/http://www.javalobby.org/java/forums/t19387.html

 */


/*
Java2D: Have Fun With Affine Transform
At 1:47 AM on Jul 5, 2005, R.J. Lorimer wrote:

One of the most common (and most helpful) forms of visual feedback for a user is some sort of diagram, picture, or graph. As they say, a picture speaks a thousand words, and when writing effective and successful software, this statement could not be any truer.

If you have a program where you want the user to be able to drag an image around that may exceed the boundaries of the window, zoom in to certain aspects of the image, zoom out to get a more global view, and rotate the image on its head, then the java.awt.geom.AffineTransform class is for you. The AffineTransform class is an implementation of the concepts of... yep, you guessed it, affine transformations. Affine transformations are a concept based in Euclidean algebra, and help us define (through the use of matrices) ways to modify coordinate spaces so that 'parallelism' and 'perpendicularity' are preserved. To see a detailed mathematical description - please go here . Affine transformations can help provide translations (shifting on the x and y axes), rotations, scaling (zooming), and shearing (another, more mutative form of scaling).

While understanding the mathematics can make you more effective at using affine transformations - the 80/20 rule allows you to get pretty far without having to know much - thankfully the class is designed to encapsulate most of that knowledge already. Long story short, you can give an affine transform object some various parameters for coordinate space modifications, and it can then tell the graphics system the real location for any given 'un-transformed' point in the coordinate space.

Once we have created an AffineTransform object, telling it to adjust our pixels is really quite easy:

AffineTransform transformer = new AffineTransform();
transformer.translate(5, 5); // translate 5+ on x, and 5+ on y.

Given this simple example, if I were to ask the transformer to transform the location [10, 17] for me, I would be given in return [15, 23]. Scaling works in a similar fashion:

AffineTransform transformer = new AffineTransform();
transformer.scale(2, 2); // scale by 2x on x and y axes.

For the same values ([10, 17]) I would be given [20, 34] if I asked this transformer.

Thankfully, you can simply bypass all of this 'asking the transformer' jive, and just tell the graphics system to use it for you:

public void paint(Graphics g) {
	AffineTransform transformer = new AffineTransform();
	transformer.translate(5,5);
	transformer.scale(2,2);
	Graphics2D g2d = (Graphics2D)g;
	g2d.setTransform(transformer);
	// draw to g2d.
}

Now anything drawn to the g2d graphics object will be translated by 5 in the X and Y axes, and will be scaled by 2x on both axes as well.

Here is a complete working example that allows you to drag your mouse around to move the drawn picture left, right, up and down, and allows you to use your mouse wheel (hope you have one!) to zoom in and out. The transformation values are captured and stored, and then each subsequent change by the user simply increments/modifies the already existing values.

package org.javalobby.tnt.java2d;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
import javax.swing.JFrame;

public class AffineTransformTest {

	private static TransformingCanvas canvas;

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		canvas = new TransformingCanvas();
		TranslateHandler translater = new TranslateHandler();
		canvas.addMouseListener(translater);
		canvas.addMouseMotionListener(translater);
		canvas.addMouseWheelListener(new ScaleHandler());
		frame.setLayout(new BorderLayout());
		frame.getContentPane().add(canvas, BorderLayout.CENTER);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}

	private static class TransformingCanvas extends JComponent {
		private double translateX;
		private double translateY;
		private double scale;

		TransformingCanvas() {
			translateX = 0;
			translateY = 0;
			scale = 1;
			setOpaque(true);
			setDoubleBuffered(true);
		}

		@Override public void paint(Graphics g) {

			AffineTransform tx = new AffineTransform();
			tx.translate(translateX, translateY);
			tx.scale(scale, scale);
			Graphics2D ourGraphics = (Graphics2D) g;
			ourGraphics.setColor(Color.WHITE);
			ourGraphics.fillRect(0, 0, getWidth(), getHeight());
			ourGraphics.setTransform(tx);
			ourGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			ourGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			ourGraphics.setColor(Color.BLACK);
			ourGraphics.drawRect(50, 50, 50, 50);
			ourGraphics.fillOval(100, 100, 100, 100);
			ourGraphics.drawString("Test Affine Transform", 50, 30);
			// super.paint(g);
		}
	}

	private static class TranslateHandler implements MouseListener,
			MouseMotionListener {
		private int lastOffsetX;
		private int lastOffsetY;

		public void mousePressed(MouseEvent e) {
			// capture starting point
			lastOffsetX = e.getX();
			lastOffsetY = e.getY();
		}

		public void mouseDragged(MouseEvent e) {

			// new x and y are defined by current mouse location subtracted
			// by previously processed mouse location
			int newX = e.getX() - lastOffsetX;
			int newY = e.getY() - lastOffsetY;

			// increment last offset to last processed by drag event.
			lastOffsetX += newX;
			lastOffsetY += newY;

			// update the canvas locations
			canvas.translateX += newX;
			canvas.translateY += newY;

			// schedule a repaint.
			canvas.repaint();
		}

		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mouseMoved(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}

	private static class ScaleHandler implements MouseWheelListener {
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {

				// make it a reasonable amount of zoom
				// .1 gives a nice slow transition
				canvas.scale += (.1 * e.getWheelRotation());
				// don't cross negative threshold.
				// also, setting scale to 0 has bad effects
				canvas.scale = Math.max(0.00001, canvas.scale);
				canvas.repaint();
			}
		}
	}

}

Until next time,

R.J. Lorimer
Contributing Editor - rj -at- javalobby.org
Author              - http://www.coffee-bytes.com
Software Consultant - http://www.crosslogic.com
10 replies so far ( [Reply to this Thread] Post your own)
1 . At 2:29 AM on Jul 5, 2005, sebastien petrucci wrote:
      	Click to reply to this thread 	Reply
    Re: Java2D: Have Fun With Affine Transform
    Here is great tutorial about Java2D tranform with examples : http://www.glyphic.com/transform/applet/1intro.html

    Sebastien.
2 . At 8:34 AM on Jul 5, 2005, Jamie Lawrence wrote:
      	Click to reply to this thread 	Reply
    Re: Java2D: Have Fun With Affine Transform
    Yeah, I had my own AffineTransform epiphany a few months ago! In my case, I needed to flip an image: http://jamie.ideasasylum.com/2005/03/matrix-multiplication.php
3 . At 2:05 PM on Jul 5, 2005, Romain Guy DeveloperZone Top 100 wrote:
      	Click to reply to this thread 	Reply
    Re: Java2D: Have Fun With Affine Transform
    A similar but shorter way to flip a picture is to do this:

    AffineTransform reflectTransform = AffineTransform.getScaleInstance(1.0, -1.0);
    g.drawImage(image, reflectTransform, null);
    Romain Guy
    Romain Guy's Java Weblog, #ProgX, Jext
4 . At 4:27 PM on Jul 5, 2005, Andy DePue wrote:
      	Click to reply to this thread 	Reply
    Re: Java2D: Have Fun With Affine Transform
    Slightly off topic, but does anyone know of a Swing control for viewing an image that allows zooming, panning, etc (open source)? Some quick google searches haven't turned up anything useful, and although AffineTransform is easy enough, I'd still rather not work through all the details of using AffineTransform with images in a scrollable pane, etc, if it can be avoided.
5 . At 5:06 AM on Jul 6, 2005, Jamie Lawrence wrote:
      	Click to reply to this thread 	Reply
    Re: Java2D: Have Fun With Affine Transform
    Duh! And there was me thinking that there was a point to learning matrix multiplication. Still, at least I got to exercise the abandoned maths muscle.
6 . At 5:13 AM on Jul 6, 2005, Romain Guy DeveloperZone Top 100 wrote:
      	Click to reply to this thread 	Reply
    Re: Java2D: Have Fun With Affine Transform
    Argh math... hate'em but need'em badly for graphics programming. Prf, what an imperfect world /o\
    Romain Guy
    Romain Guy's Java Weblog, #ProgX, Jext
7 . At 4:40 PM on Jul 7, 2005, Andrew McVeigh DeveloperZone Top 100 wrote:
      	Click to reply to this thread 	Reply
    Re: Java2D: Have Fun With Affine Transform
    > Slightly off topic, but does anyone know of a Swing
    > control for viewing an image that allows zooming,
    > panning, etc (open source)? Some quick google
    > searches haven't turned up anything useful, and
    > although AffineTransform is easy enough, I'd still
    > rather not work through all the details of using
    > AffineTransform with images in a scrollable pane,
    > etc, if it can be avoided.


    It's perhaps not exactly what you want, but I've had very good results with Piccolo (and Jazz, it's older cousin):

    http://www.cs.umd.edu/hcil/piccolo/

    It's basically a zooming and transformable display list, with support for images etc. Very impressive. I don't think it takes much work for it to be packaged in a swing widget. I've done it before in Jazz.

    All open source too :-)

    Andrew
8 . At 11:07 AM on Aug 15, 2005, Matthias Orgler wrote:
      	Click to reply to this thread 	Reply
    How to transform mouse coordinates?
    The AffineTransform is a beatiful way to e.g. apply zoom functionality transparently to an application. But what about the inverse way?

    All my classes can use the Graphics object without even knowing about an AffineTransform - they just use an "abstracted" coordinate system. This is very nice. But when my classes receive MouseEvents, the coordinates in the MouseEvent are NOT transformed back! This breaks the nice SoC in which my classes don't need to know about any transforms :o(. Is there a way to apply the inverse transform to mouse coordinates WITHOUT my classes having to do it themselves (e.g. transparently)?

    Thanks in advance!

    Matthias
9 . At 6:32 AM on Mar 25, 2006, Laurent Merckx wrote:
      	Click to reply to this thread 	Reply
    Re: How to transform mouse coordinates?
    It is even worst when using mouseentered and mouseexited events. Methods are called when the mouse isn't in your components !

    It is a big problem for me and I don't find any nice workaround. If someone has a solution, it would be very useful ?!

    Thanks in advance.
10 . At 3:51 PM on Aug 6, 2006, ramya wrote:
      	Click to reply to this thread 	Reply
    Re: Java2D: Have Fun With Affine Transform
    hi,

    I am building a text editor(styled) using JTextPane. I have lines of text that need alignment(characters should be lined in a column). I could achieve this with monospaced fonts. Are there ways to do the same with proportional fonts? I am not familar with Affine Transforms. Can transforms help?

    Thanks for your time,
    ramya
 */



public class Zoom {
    public static void main(String[] args) {
        try {
            SwingUtilities.invokeAndWait(PanAndZoom::new);
        } catch (final Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
