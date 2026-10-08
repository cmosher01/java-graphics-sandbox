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

import lombok.*;

import java.util.ArrayList;

public class Indi {
    private final IndividualId id;

//    private final MovablePoint xy;

    private final int sex;
    private final long birth;

    private final ArrayList<Fami> rFamiChildTo = new ArrayList<>(1);
    private final ArrayList<Fami> rFamiSpouseTo = new ArrayList<>(1);

    private final Plaque plaque;



    public Indi(final @NonNull IndividualId id, final @NonNull String xy, @NonNull String name, @NonNull String lifespan, @NonNull String place, final int sex, final long birth) {
        this.id = id;
//        this.xy = new XyValue(xy).get();
        this.sex = sex;
        this.birth = birth;
        this.plaque = Plaque.create(name, lifespan, place);
    }

    public void addAsChildTo(final Fami fami) {
        this.rFamiChildTo.add(fami);
    }

    public void addAsSpouseTo(final Fami fami) {
        this.rFamiSpouseTo.add(fami);
    }



    public String getId() {
        if (this.id.isFtm()) {
            return this.id.pkid();
        }
        return this.id.node().getObject().getID();
    }
}
