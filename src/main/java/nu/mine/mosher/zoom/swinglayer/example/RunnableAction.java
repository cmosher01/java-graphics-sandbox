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

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RunnableAction extends AbstractAction {
    private final Runnable lambda;

    public static RunnableAction create(final String name, final Runnable lambda) {
         final var o = new RunnableAction(lambda);
         o.putValue(NAME, name);
         return o;
    }

    private RunnableAction(final Runnable lambda) {
        this.lambda = lambda;
    }



    @Override
    public void actionPerformed(final ActionEvent e) {
        this.lambda.run();
    }
}
