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

package nu.mine.mosher.zoom.swinglayer.example;

import java.nio.file.Path;
import java.util.Objects;

public class DatabaseModel {
    private Path file;

    public void open(final Path file) {
        if (isOpen()) {
            close();
        }
        this.file = file;
        System.out.println("Opened: "+this.file);
    }

    public boolean isOpen() {
        return Objects.nonNull(this.file);
    }

    public void close() {
        System.out.println("Closing: "+this.file);
        this.file = null;
    }
}
