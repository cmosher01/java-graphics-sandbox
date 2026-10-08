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

import lombok.val;
import nu.mine.mosher.collection.TreeNode;
import nu.mine.mosher.gedcom.*;
import nu.mine.mosher.gedcom.exception.InvalidLevel;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class IoGedcom {
    public void read(final Path path) throws IOException, InvalidLevel {
        val tree = Gedcom.readFile(new BufferedInputStream(Files.newInputStream(path)));
        new GedcomConcatenator(tree).concatenate();

        val mapIdToIndi = new HashMap<String, Indi>();

        val indis = buildIndis(tree, mapIdToIndi);
        val famis = buildFamis(tree, Collections.unmodifiableMap(mapIdToIndi));
    }

    private static ArrayList<Indi> buildIndis(final GedcomTree tree, final Map<String, Indi> mapIdToIndi) {
        val indis = new ArrayList<Indi>();
        tree.getRoot().forEach(nodeIndi -> {
            if (nodeIndi.getObject().getTag().equals(GedcomTag.INDI)) {
                val indi = buildIndi(nodeIndi);
                mapIdToIndi.put(indi.getId(), indi);
                indis.add(indi);
            }
        });
        return indis;
    }

    private static ArrayList<Fami> buildFamis(final GedcomTree tree, final Map<String, Indi> mapIdToIndi) {
        val famis = new ArrayList<Fami>();
        tree.getRoot().forEach(nodeFami -> {
            if (nodeFami.getObject().getTag().equals(GedcomTag.FAM)) {
                val fami = buildFami(nodeFami, mapIdToIndi);
                famis.add(fami);
            }
        });
        return famis;
    }



    private static Indi buildIndi(final TreeNode<GedcomLine> node) {
        val xy = getValue(node, "_XY");

        // these get displayed on the plaque
        val name = getValue(node, "NAME");
        val lifespan = GedcomDateUtil.getLifespan(getEventDate(node, "BIRT"), getEventDate(node, "DEAT"));
        val place = getPlace(node);

        // birthdate and sex are only used as hints by the automatic layout algorithms
        val sex = toSex(getValue(node, "SEX"));
        val birth = GedcomDateUtil.calcBirthForSort(getEventDate(node, "BIRT"));

        return new Indi(new IndividualId(node), xy, name, lifespan, place, sex, birth);
    }

    private static Fami buildFami(final TreeNode<GedcomLine> nodeFami, final Map<String, Indi> mapIdToIndi) {
        val fami = new Fami();
        for (val c : nodeFami) {
            val child = c.getObject();
            val oindi = Optional.ofNullable(mapIdToIndi.get(child.getPointer()));
            if (oindi.isPresent()) {
                val indi = oindi.get();
                switch (child.getTag()) {
                    case HUSB, WIFE -> fami.addSpouse(indi);
                    case CHIL -> fami.addChild(indi);
                }
            }
        }
        return fami;
    }





    private static String getPlace(final TreeNode<GedcomLine> nodeIndi) {
        val birthplace = getBirthPlace(nodeIndi).strip();
        if (!birthplace.isBlank()) {
            return birthplace;
        }
        return getAnyPlace(nodeIndi);
    }

    private static String getBirthPlace(final TreeNode<GedcomLine> node) {
        for (val c : node) {
            if (c.getObject().getTagString().equals("BIRT")) {
                return getValue(c, "PLAC");
            }
        }
        return "";
    }

    private static String getAnyPlace(final TreeNode<GedcomLine> node) {
        for (val c : node) {
            val p = getValue(c, "PLAC");
            if (!p.isBlank()) {
                return p;
            }
        }
        return "";
    }

    private static String getEventDate(final TreeNode<GedcomLine> node, final String tag) {
        for (val c : node) {
            if (c.getObject().getTagString().equals(tag)) {
                return getValue(c, "DATE");
            }
        }
        return "";
    }

    private static String getValue(final TreeNode<GedcomLine> node, final String tag) {
        for (val c : node) {
            if (c.getObject().getTagString().equals(tag)) {
                val p = c.getObject().getValue().strip();
                if (!p.isBlank()) {
                    return p;
                }
            }
        }
        return "";
    }





    private static int toSex(final String sex) {
        if (!sex.isEmpty()) {
            val c = sex.toUpperCase().charAt(0);
            if (c == 'M') {
                return 1;
            }
            if (c == 'F') {
                return 2;
            }
        }
        return 0;
    }
}
