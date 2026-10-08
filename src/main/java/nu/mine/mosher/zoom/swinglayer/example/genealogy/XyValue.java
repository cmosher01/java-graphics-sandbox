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

package nu.mine.mosher.zoom.swinglayer.example.genealogy;

import lombok.*;

import java.awt.geom.Point2D;
import java.io.StringReader;
import java.util.*;

@RequiredArgsConstructor
public class XyValue {
    private final boolean exists;
    private final double x;
    private final double y;
    private final String error;

    public static XyValue create(final @NonNull String s) {
        double x;
        double y;
        boolean exists;
        String error;
        try (val in = new Scanner(new StringReader(s.strip()))) {
            if (in.hasNextDouble()) {
                x = in.nextDouble();
                if (in.hasNextDouble()) {
                    y = in.nextDouble();
                    exists = true;
                    error = "";
                } else {
                    x = 0;
                    y = 0;
                    exists = false;
                    error = "Invalid value for y";
                }
            } else {
                x = 0;
                y = 0;
                exists = false;
                error = "Invalid value for x";
            }
        } catch (final Exception e) {
            x = 0;
            y = 0;
            exists = false;
            error = e.toString();
        }
        return new XyValue(x, y, exists, error);
    }

    private XyValue(double x, double y, boolean exists, String error) {
        this.x = x;
        this.y = y;
        this.exists = exists;
        this.error = error;
    }

    @Override
    public @NonNull String toString() {
        final String ret;
        if (this.exists) {
            ret = this.x+" "+this.y;
        } else {
            ret = "";
        }
        return ret;
    }

    public @NonNull String error() {
        return this.error;
    }

    public boolean isPresent() {
        return this.exists;
    }

    public @NonNull Optional<Point2D.Double> get() {
        final Optional<Point2D.Double> ret;
        if (this.exists) {
            ret = Optional.of(new Point2D.Double(this.x, this.y));
        } else {
            ret = Optional.empty();
        }
        return ret;
    }
}
