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

import net.imglib2.cache.img.*;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.view.Views;
import net.imglib2.RandomAccess;

import java.nio.file.Path;

public class UltraLargeCanvas {
    public static void main(String[] args) {
        // 1. Define dimensions (100 Million x 100 Million)
        long[] dimensions = new long[] { 100_000_000L, 100_000_000L };

        // 2. Configure tile (cell) sizes (e.g., 512x512 pixels)
        int[] cellDimensions = new int[] { 512, 512 };

        // 3. Define a loader that populates empty/sparse cells instantly with a default value
        CellLoader<UnsignedByteType> loader = cell -> {
            for (UnsignedByteType pixel : cell) {
                pixel.setZero(); // Empty canvas default state
            }
        };

        // 4. Create the disk-backed virtual image
        DiskCachedCellImgOptions options = DiskCachedCellImgOptions.options()
                .cacheDirectory(Path.of("./canvas_cache")) // Stores edited tiles on disk
                .maxCacheSize(1000); // Max number of tiles kept in RAM simultaneously

        // 4.1. Instantiate factory passing the matching Type object to the constructor
        DiskCachedCellImgFactory<UnsignedByteType> factory = new DiskCachedCellImgFactory<>(new UnsignedByteType(), options);

        // 4.2 Create the disk-cached sparse image matrix
        DiskCachedCellImg<UnsignedByteType, ?> baseImage = factory.create(
                dimensions,
                new UnsignedByteType(),
                loader
        );

        // 5. Real-Time Editing: Grab an active pointer to write data
        RandomAccess<UnsignedByteType> writePointer = baseImage.randomAccess();
        writePointer.setPosition(new long[]{ 55_451_200L, 89_120_350L });
        writePointer.get().set(255); // Change pixel color. Tile is dynamically cached to disk!

        // 6. Real-Time Zooming/Viewing: Create a 50% virtual zoom view on the fly
        // Every pixel read here looks at every 2nd pixel of the base image automatically
        var zoomLevel1 = Views.subsample(baseImage, 2);
    }
}
