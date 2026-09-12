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

import lombok.val;

import java.nio.file.Path;
import java.util.prefs.Preferences;

public class UserPrefs {
    public static Preferences prefs() {
        return Preferences.userNodeForPackage(UserPrefs.class);
    }

    public static Path dir() {
        val cur = prefs().get("dir", "").strip();
        val sys = System.getProperty("user.home", "").strip();

        final String def;
        if (!cur.isBlank()) {
            def = cur;
        } else if (!sys.isBlank()) {
            def = sys;
        } else {
            def = "./";
        }

        return Path.of(prefs().get("dir", def).strip());
    }

    public static void dir(final Path dir) {
        prefs().put("dir", dir.toString().strip());
    }
}
