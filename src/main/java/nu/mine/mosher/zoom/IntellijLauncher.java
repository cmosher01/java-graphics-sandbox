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

package nu.mine.mosher.zoom;

import nu.mine.mosher.zoom.javafx.GraphicsScalingApp;
import nu.mine.mosher.zoom.math.GenericZoomTestJavaFxApp;
import nu.mine.mosher.zoom.panningtiles.PanningTilesExample;

// there some strange IntelliJ behavior where JavaFX programs need
// to have a separate "main" class, or something like that:
public class IntellijLauncher {
    public static void main(final String... args) {
        GenericZoomTestJavaFxApp.main(args);
//        GraphicsScalingApp.main(args);
//        PanningTilesExample.main(args);
    }
}
