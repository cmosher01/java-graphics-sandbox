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

package dev.cmosher01.genealogy.xy.genealogy;

import lombok.NonNull;

public class Plaque {
    private final @NonNull String name;
    private final @NonNull String lifespan;
    private final @NonNull String place;

    public static Plaque create(final @NonNull String name, final @NonNull String lifespan, final @NonNull String place) {
        return new Plaque(name, lifespan, place);
    }

    private Plaque(String name, String lifespan, String place) {
        this.name = name;
        this.lifespan = lifespan;
        this.place = place;
    }
}
