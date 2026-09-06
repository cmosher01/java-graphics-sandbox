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

package nu.mine.mosher.zoom.swinglayer.playground;

public class DropLineChart /*implements Graphic*/ {
//    private static final boolean BOUNDS_FILL = false;
//    private static final Color COLOR_CANVAS_BG = BASE_3;
//
//    private static final boolean BOUNDS_DRAW = true;
//    private static final Stroke BOUNDS_STROKE = new BasicStroke(10.0F, CAP_BUTT, JOIN_BEVEL);
//    private static final double BOUNDS_OUTSET = 1000.0D;
//
//
//    private final ArrayList<InteractiveRect> sqs = new ArrayList<>();
//    private final LineOfText tx = new LineOfText("This is my custom Panel!", new Point2D.Double(200D,200D));
//
//    private InteractiveRect selection;
//
//
//
//    private Rectangle2D.Double bounds;
//    private Rectangle2D.Double boundsOutset;



//    public DropLineChart() {
//        generateRandomObjects(1_000_000);
//    }

//    private void generateRandomObjects(int n) {
//        final double maxCoord = 1.0e8D;
//        val rand = new Random();
//        for (int i = 0; i < n; i++) {
//            final double x = rand.nextDouble(-maxCoord, maxCoord);
//            final double y = rand.nextDouble(-maxCoord, maxCoord);
//            this.sqs.add(new InteractiveRect(x, y,));
//        }
//    }


//    @Override
//    public void updateBounds() {
//        var b = this.tx.bounds();
//        for (val sq : this.sqs) {
//            Rectangle2D.union(sq.bounds(), b, b);
//        }
//        this.bounds = b;
//        b = Swings.outset(b, BOUNDS_OUTSET);
//        this.boundsOutset = b;
//    }
//
//    @Override
//    public Rectangle2D.Double bounds() {
//        return (Rectangle2D.Double)this.bounds.getBounds2D();
//    }
//
//    public Rectangle2D.Double boundsOutset() {
//        return (Rectangle2D.Double)this.boundsOutset.getBounds2D();
//    }

//    @Override
//    public void paint(final Graphics2D g, final Rectangle2D clip) {
//        if (this.tx.bounds().intersects(clip)) {
//            this.tx.paint(g);
//        }
//
//        // TODO potential optimization in searching for clip region
//        this.sqs.forEach(sq ->{
//            if (sq.bounds().intersects(clip)) {
//                sq.paint(g);
//            }
//        });
//    }

//    @Override
//    public void paintBackground(final Graphics2D g, final Rectangle2D clip) {
//        if (BOUNDS_FILL) {
//            g.setColor(COLOR_CANVAS_BG);
//            // some glitching at zoom in greater than ~30:
//            g.fill(bounds());
//        }
//
//        if (BOUNDS_DRAW) {
//            g.setStroke(BOUNDS_STROKE);
//            g.setColor(BASE_03);
//            g.draw(boundsOutset());
//        }
//    }

//    @Override
//    public boolean press(final Point2D at, final MouseEvent e) {
//        hitOne(at);
//        return Objects.nonNull(this.selection);
//    }
//
//    @Override
//    public void drag(final Point2D delta, final MouseEvent e) {
//        if (Objects.nonNull(this.selection)) {
//            this.selection.setRect(
//                this.selection.x + delta.getX(),
//                this.selection.y + delta.getY(),
//                this.selection.width,
//                this.selection.height);
//            updateBounds();
//        }
//    }
//
//    @Override
//    public void release(final MouseEvent e) {
//        this.selection = null;
//    }
//
//
//
//    private void hitOne(final Point2D at) {
//        this.selection = null;
//        int i = 0;
//        // TODO potential optimization in searching for hits
//        while (i < this.sqs.size() && Objects.isNull(this.selection)) {
//            val sq = this.sqs.get(i);
//            if (sq.contains(at)) {
//                this.selection = sq;
//            }
//            ++i;
//        }
//    }
}
