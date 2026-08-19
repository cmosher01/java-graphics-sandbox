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

package nu.mine.mosher.zoom.imglib2;

import net.imglib2.RandomAccess;
import net.imglib2.cache.CacheLoader;
import net.imglib2.cache.img.*;
import net.imglib2.cache.img.optional.CacheOptions;
import net.imglib2.img.Img;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.cell.*;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.view.Views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class MassiveCanvasApp {

    public static void main(String[] args) {
        long[] totalDimensions = new long[]{100000000L, 100000000L};
        int[] cellSizes = new int[]{512, 512};

        List<Rectangle2D.Double> shapes = DatasetGenerator.generateRectangles(totalDimensions[0]);
        CellGrid cellGrid = new CellGrid(totalDimensions, cellSizes);
        ProceduralRectLoader loader = new ProceduralRectLoader(cellGrid, shapes);

        DiskCachedCellImgOptions options = DiskCachedCellImgOptions.options().cellDimensions(cellSizes).cacheType(CacheOptions.CacheType.SOFTREF).maxCacheSize(2048);

        DiskCachedCellImgFactory<DoubleType> factory = new DiskCachedCellImgFactory<>(new DoubleType(), options);
        Img<DoubleType> cachedImg = factory.createWithCacheLoader(totalDimensions, loader);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("ImgLib2 100M x 100M Cached Procedural Canvas");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            MassiveImgLib2Canvas canvas = new MassiveImgLib2Canvas(cachedImg);
            canvas.setPreferredSize(new Dimension(1024, 768));

            frame.add(canvas);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    public static class DatasetGenerator {
        public static List<Rectangle2D.Double> generateRectangles(long maxBound) {
            List<Rectangle2D.Double> rects = new ArrayList<>();
            Random rand = new Random(42);

            int minW = 100, maxW = 300;
            int minH = 30, maxH = 60;

            for (int i = 0; i < 100000; i++) {
                double x = rand.nextDouble() * (maxBound - maxW);
                double y = rand.nextDouble() * (maxBound - maxH);
                double w = minW + rand.nextInt(maxW - minW + 1);
                double h = minH + rand.nextInt(maxH - minH + 1);
                rects.add(new Rectangle2D.Double(x, y, w, h));
            }
            return rects;
        }
    }

    public static class SpatialGridIndex {
        private static final long BUCKET_SIZE = 1000000L;
        private final Map<Long, List<Rectangle2D.Double>> grid = new HashMap<>();

        public SpatialGridIndex(List<Rectangle2D.Double> rectangles) {
            for (Rectangle2D.Double r : rectangles) {
                long minBucketX = (long) (r.x / BUCKET_SIZE);
                long maxBucketX = (long) ((r.x + r.width) / BUCKET_SIZE);
                long minBucketY = (long) (r.y / BUCKET_SIZE);
                long maxBucketY = (long) ((r.y + r.height) / BUCKET_SIZE);

                for (long bx = minBucketX; bx <= maxBucketX; bx++) {
                    for (long by = minBucketY; by <= maxBucketY; by++) {
                        long key = (bx << 32) | (by & 0xFFFFFFFFL);
                        grid.computeIfAbsent(key, k -> new ArrayList<>()).add(r);
                    }
                }
            }
        }

        public List<Rectangle2D.Double> getIntersectingRects(long cMinX, long cMinY, long cMaxX, long cMaxY) {
            long bx1 = cMinX / BUCKET_SIZE;
            long bx2 = cMaxX / BUCKET_SIZE;
            long by1 = cMinY / BUCKET_SIZE;
            long by2 = cMaxY / BUCKET_SIZE;

            Set<Rectangle2D.Double> uniqueCandidates = new HashSet<>();
            for (long bx = bx1; bx <= bx2; bx++) {
                for (long by = by1; by <= by2; by++) {
                    long key = (bx << 32) | (by & 0xFFFFFFFFL);
                    List<Rectangle2D.Double> list = grid.get(key);
                    if (list != null) uniqueCandidates.addAll(list);
                }
            }

            List<Rectangle2D.Double> matches = new ArrayList<>();
            Rectangle2D.Double cellBounds = new Rectangle2D.Double(cMinX, cMinY, cMaxX - cMinX + 1, cMaxY - cMinY + 1);
            for (Rectangle2D.Double r : uniqueCandidates) {
                if (r.intersects(cellBounds)) {
                    matches.add(r);
                }
            }
            return matches;
        }
    }

    public static class ProceduralRectLoader implements CacheLoader<Long, Cell<DoubleArray>> {
        private final CellGrid grid;
        private final SpatialGridIndex spatialIndex;

        public ProceduralRectLoader(CellGrid grid, List<Rectangle2D.Double> rectangles) {
            this.grid = grid;
            this.spatialIndex = new SpatialGridIndex(rectangles);
        }

        @Override
        public Cell<DoubleArray> get(Long key) throws Exception {
            int[] cellDims = new int[2];
            long[] cellMin = new long[2];
            grid.getCellDimensions(key, cellMin, cellDims);

            long cellMaxX = cellMin[0] + cellDims[0] - 1;
            long cellMaxY = cellMin[1] + cellDims[1] - 1;

            List<Rectangle2D.Double> localRects = spatialIndex.getIntersectingRects(cellMin[0], cellMin[1], cellMaxX, cellMaxY);
            double[] data = new double[cellDims[0] * cellDims[1]];

            if (!localRects.isEmpty()) {
                int index = 0;
                for (int y = 0; y < cellDims[1]; y++) {
                    long worldY = cellMin[1] + y;
                    for (int x = 0; x < cellDims[0]; x++) {
                        long worldX = cellMin[0] + x;
                        for (Rectangle2D.Double r : localRects) {
                            if (worldX >= r.x && worldX <= (r.x + r.width) && worldY >= r.y && worldY <= (r.y + r.height)) {
                                data[index] = 1.0;
                                break;
                            }
                        }
                        index++;
                    }
                }
            }
            return new Cell<>(cellDims, cellMin, new DoubleArray(data));
        }
    }

    public static class MassiveImgLib2Canvas extends JPanel {
        private final Img<DoubleType> img;
        private final AffineTransform transform = new AffineTransform();
        private Point lastDragPoint;

        public MassiveImgLib2Canvas(Img<DoubleType> img) {
            this.img = img;
            this.setBackground(Color.DARK_GRAY);

            //  STARTUP CAMERA POSITION ADJUSTMENT
            // Center the display viewport near a known generated rectangle coordinate location
            transform.translate(-50000000.0, -50000000.0); // Center the massive canvas
            transform.scale(0.05, 0.05); // Zoom out significantly so shapes are visible

            MouseAdapter mouseAdapter = new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    lastDragPoint = e.getPoint();
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    if (lastDragPoint != null) {
                        Point currentPoint = e.getPoint();
                        double dx = currentPoint.x - lastDragPoint.x;
                        double dy = currentPoint.y - lastDragPoint.y;

                        transform.preConcatenate(AffineTransform.getTranslateInstance(dx, dy));
                        lastDragPoint = currentPoint;
                        repaint();
                    }
                }

                @Override
                public void mouseWheelMoved(MouseWheelEvent e) {
                    double zoomFactor = (e.getWheelRotation() < 0) ? 1.15 : 1.0 / 1.15;
                    Point mousePt = e.getPoint();

                    AffineTransform at = new AffineTransform();
                    at.translate(mousePt.x, mousePt.y);
                    at.scale(zoomFactor, zoomFactor);
                    at.translate(-mousePt.x, -mousePt.y);

                    transform.preConcatenate(at);
                    repaint();
                }
            };

            addMouseListener(mouseAdapter);
            addMouseMotionListener(mouseAdapter);
            addMouseWheelListener(mouseAdapter);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            int width = getWidth();
            int height = getHeight();
            if (width <= 0 || height <= 0) return;

            BufferedImage screenBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            int[] screenPixels = ((DataBufferInt) screenBuffer.getRaster().getDataBuffer()).getData();

            try {
                AffineTransform inverse = transform.createInverse();

                ThreadLocal<RandomAccess<DoubleType>> threadLocalRA = ThreadLocal.withInitial(() -> Views.extendZero(img).randomAccess());

                IntStream.range(0, height).parallel().forEach(y -> {
                    RandomAccess<DoubleType> ra = threadLocalRA.get();
                    Point2D.Double screenPt = new Point2D.Double();
                    Point2D.Double worldPt = new Point2D.Double();
                    screenPt.y = y;

                    int pixelIndex = y * width;
                    for (int x = 0; x < width; x++) {
                        screenPt.x = x;
                        inverse.transform(screenPt, worldPt);

                        long worldX = (long) Math.floor(worldPt.x);


                        long worldY = (long) Math.floor(worldPt.y);
                        ra.setPosition(worldX, 0);
                        ra.setPosition(worldY, 1);
                        double value = ra.get().getRealDouble();
                        screenPixels[pixelIndex++] = (value > 0.5) ? 0xFF33B5E5 : 0xFF111111;
                    }
                });
                g2.drawImage(screenBuffer, 0, 0, null);
            } catch (NoninvertibleTransformException e) {
                g2.setColor(Color.RED);
                g2.drawString("Transformation Error", 20, 20);
            }
            g2.setColor(Color.WHITE);
            g2.drawString("Use Mouse Wheel to Zoom, Drag to Pan across 100M x 100M grid", 15, 25);
        }
    }
}









