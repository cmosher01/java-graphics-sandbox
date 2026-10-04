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

// From: https://docs.oracle.com/javase/tutorial/uiswing/examples/painting/
// with my corrections, additions, and refactorings (Chris Mosher)

package nu.mine.mosher.zoom.swinglayer.example;

import lombok.*;

import java.util.List;

import static java.lang.Boolean.*;

public class FamilyTreeXyEditor {
    private static final String TITLE = "Family Tree XY Editor";

    @SneakyThrows
    public static void main(final String... args) {
        preSwingSetup();
        val application = new FamilyTreeXyEditorApplication();
        application.run(List.of(args));
    }

    private static void preSwingSetup() {
        System.setProperty("apple.awt.application.name", TITLE);
        System.setProperty("sun.awt.noerasebackground", TRUE.toString());
        System.setProperty("swing.boldMetal", FALSE.toString());
        System.setProperty("sun.java2d.opengl", TRUE.toString());
        System.setProperty("apple.laf.useScreenMenuBar", TRUE.toString());
        System.setProperty("com.apple.macos.useScreenMenuBar", TRUE.toString());
    }
}
