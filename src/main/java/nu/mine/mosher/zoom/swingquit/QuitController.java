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

package nu.mine.mosher.zoom.swingquit;

import lombok.*;

import java.awt.desktop.QuitResponse;
import java.util.Objects;

import static javax.swing.JOptionPane.*;

@RequiredArgsConstructor
public class QuitController {
    private final LocalQuitResponse LOCAL_QUIT = new LocalQuitResponse();

    private final CommandController command;
    private final DialogViews dlg;

    @Setter
    private Disposable window;

    private boolean approved;



    public void quit() {
        quit(LOCAL_QUIT);
    }

    public void quit(final QuitResponse r) {
        if (this.approved) {
            this.approved = false;
            if (Objects.nonNull(this.window)) {
                this.window.dispose();
            } else {
                System.exit(0);
            }
        } else {
            // TODO check model: if dirty then pressed = askOk() else pressed = NO_OPTION
            // TODO timeout for askOk dialog
            val pressed = this.dlg.askOk();
            if (pressed == CANCEL_OPTION || pressed == CLOSED_OPTION){
                r.cancelQuit();
            } else {
                if (pressed == YES_OPTION) {
                    this.command.save();
                }
                this.approved = true;
                r.performQuit();
            }
        }
    }



    private class LocalQuitResponse implements QuitResponse {
        @Override public void performQuit() { quit(); }
        @Override public void  cancelQuit() { }
    }



//    private static String resp(int r) {
//        return "    response: "+r+" => "+switch (r) {
//            case YES_OPTION -> "YES";
//            case NO_OPTION -> "NO";
//            case CANCEL_OPTION -> "CANCEL";
//            case CLOSED_OPTION -> "CLOSED";
//            default -> "UNKNOWN";
//        };
//    }
}
