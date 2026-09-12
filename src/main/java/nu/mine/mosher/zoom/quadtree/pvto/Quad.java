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

package nu.mine.mosher.zoom.quadtree.pvto;

import java.util.*;

class Quad<T> {
    private final QuadTree<T> quadTree;
    Quad<T> parent;
    private Quad<T> UL;
    private Quad<T> UR;
    private Quad<T> LL;
    private Quad<T> LR;
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    List<CoordHolder<T>> items = new LinkedList<>();



    public Quad(QuadTree<T> quadTree, Quad<T> parent, double x1, double y1, double x2, double y2) {
        this.quadTree = quadTree;
        this.parent = parent;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }



    public void findAll(double X1, double Y1, double X2, double Y2, List<CoordHolder<T>> ret) {
        if (UL == null) {
            for (final var h : items) {
                if ((X1 <= h.x && h.x <= X2) && (Y1 <= h.y && h.y <= Y2)) {
                    ret.add(h);
                }
            }
        } else {
            if (overlap(UL, X1, Y1, X2, Y2)) UL.findAll(X1, Y1, X2, Y2, ret);
            if (overlap(UR, X1, Y1, X2, Y2)) UR.findAll(X1, Y1, X2, Y2, ret);
            if (overlap(LL, X1, Y1, X2, Y2)) LL.findAll(X1, Y1, X2, Y2, ret);
            if (overlap(LR, X1, Y1, X2, Y2)) LR.findAll(X1, Y1, X2, Y2, ret);
        }
    }

    private static <T> boolean overlap(Quad<T> q, double X1, double Y1, double X2, double Y2) {
        return (X1 <= q.x2) && (Y1 <= q.y2) && (q.x1 <= X2) && (q.y1 <= Y2);
    }

    public CoordHolder<T> place(double x, double y, T o) {
        return place(new CoordHolder<T>(this.quadTree, x, y, o, null), 0);
    }

    public CoordHolder<T> place(CoordHolder<T> h, final int n) {
        final double x = h.x;
        final double y = h.y;
        if (x < x1 || y < y1 || x2 < x || y2 < y) {
            if (x1 == x2) {
                x1 = Math.min(x1, x);
                y1 = Math.min(y1, y);
                double add = Math.max(Math.max(x2, x) - x1, Math.max(y2, y) - y1);
                x2 = x1 + add;
                y2 = y1 + add;
            } else {
                if (parent == null) {
                    initParent(x, y);
                }
                return parent.place(h, n + 1);
            }
        }
        if (quadTree.getMaxLeafSize() <= items.size()) {
            expand(n + 1);
        }
        if (UL != null) {
            h = place_(h, this, n + 1);
        } else {
            h.quad = this;
            items.add(h);
        }
        return h;
    }

    private CoordHolder<T> place_(CoordHolder<T> h, Quad<T> quad, int n) {
        while (quad.UL != null) {
            if (h.x <= (quad.x2 + quad.x1) / 2) {
                quad = (h.y <= (quad.y2 + quad.y1) / 2 ? quad.UL : quad.LL);
            } else {
                quad = (h.y <= (quad.y2 + quad.y1) / 2 ? quad.UR : quad.LR);
            }
        }
        return quad.place(h, n + 1);
    }

    private void expand(int n) {
        if (LL == null) {
            initQuad();
        }
        for (CoordHolder<T> c : items) {
            place_(c, this, n + 1);
        }
        items = Collections.emptyList();
    }

    private void initQuad() {
        UL = new Quad<T>(this.quadTree, this, x1, y1, (x2 + x1) / 2.0, (y2 + y1) / 2.0);
        UR = new Quad<T>(this.quadTree, this, (x2 + x1) / 2.0, y1, x2, (y2 + y1) / 2.0);
        LL = new Quad<T>(this.quadTree, this, x1, (y2 + y1) / 2.0, (x2 + x1) / 2.0, y2);
        LR = new Quad<T>(this.quadTree, this, (x2 + x1) / 2.0, (y2 + y1) / 2.0, x2, y2);
    }

    private void initParent(double x, double y) {
        int quadInd = 0;
        double X1 = x1;
        double Y1 = y1;
        double X2 = x2 + (x2 - x1);
        double Y2 = y2 + (y2 - y1);
        if (x < X1) {
            quadInd++;
            X1 -= (x2 - x1);
            X2 -= (x2 - x1);
        }
        if (y < Y1) {
            quadInd += 2;
            Y1 -= (y2 - y1);
            Y2 -= (y2 - y1);
        }
        parent = new Quad<T>(this.quadTree, null, X1, Y1, X2, Y2);
        parent.initQuad();
        switch (quadInd) {
            case 0:
                parent.UL = this;
                break;
            case 1:
                parent.UR = this;
                break;
            case 2:
                parent.LL = this;
                break;
            case 3:
                parent.LR = this;
                break;
        }
        quadTree.setNewRoot(parent);
    }

    public void replace(CoordHolder<T> item, int n) {
        if (item.x < x1 || item.y < y1 || x2 < item.x || y2 < item.y) {
            items.remove(item);
            if (parent == null) {
                initParent(item.x, item.y);
            }
            parent.place(item, n + 1);
        }
    }

//    private void printChar(PrintStream out, char c, int n) {
//        for (int i = 0; i < n; i++) out.print(c);
//    }
//
//    private void print(PrintStream out, int indent) {
//        printChar(out, '.', indent - 1);
//        out.print(' ');
//        out.print('(');
//        out.print(x1);
//        out.print(',');
//        out.print(y1);
//        out.print(" - ");
//        out.print(x2);
//        out.print(',');
//        out.print(y2);
//        out.println(')');
//
//        for (CoordHolder<T> h : this.items) {
//            printChar(out, ' ', indent + 2);
//            out.print(h.x);
//            out.print(',');
//            out.print(h.y);
//            out.print(": ");
//            out.println(h.o.toString().replaceAll("\r?\n.*", ""));
//        }
//        if (UL != null) {
//            printChar(out, ' ', indent);
//            out.println('[');
//            UL.print(out, indent + 2);
//            UR.print(out, indent + 2);
//            LL.print(out, indent + 2);
//            LR.print(out, indent + 2);
//            printChar(out, ' ', indent);
//            out.println(']');
//        }
//    }

}
