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

import java.awt.geom.*;

public class InteractiveRectModel {
    public static final double ITEM_WIDTH = 60.0D;
    public static final double ITEM_HEIGHT = 30.0D;

    private final double w;
    private final double h;
    private final String tag;

    private final Grid grid;
    private final DocumentModel modelDocument;

    private double xOrg;
    private double yOrg;
    private double xRaw;
    private double yRaw;
    private double xCur;
    private double yCur;

    private boolean selected;



    public InteractiveRectModel(final double x, final double y, final String tag, final DocumentModel modelDocument, final Grid grid) {
        this.w = ITEM_WIDTH;
        this.h = ITEM_HEIGHT;
        this.tag = tag;
        this.grid = grid;
        this.modelDocument = modelDocument;

        this.xCur = x;
        this.yCur = y;
        anchor();
        flip();
    }

    public String tag() {
        return this.tag;
    }

    public boolean selected() {
        return this.selected;
    }

    public void select(final boolean selected) {
        this.selected = selected;
    }

    public void move(final Point2D.Double d) {
        this.xRaw += d.getX();
        this.yRaw += d.getY();
        this.xCur = this.grid.snap(this.xRaw);
        this.yCur = this.grid.snap(this.yRaw);
        setDocumentModificationState();
    }

    private void setDocumentModificationState() {
        if (isModified()) {
            this.modelDocument.setModified();
        } else {
            this.modelDocument.refreshModified();
        }
    }

    public boolean isModified() {
        return this.xCur != this.xOrg || this.yCur != this.yOrg;
    }

    public void anchor() {
        this.xRaw = this.xCur;
        this.yRaw = this.yCur;
    }

    public void flip() {
        this.xOrg = this.xCur;
        this.yOrg = this.yCur;
    }

    public Rectangle2D.Double rect() {
        return new Rectangle2D.Double(this.xCur, this.yCur, this.w, this.h);
    }
}
