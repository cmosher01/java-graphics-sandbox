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
import java.awt.event.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuitController {
    private final LocalQuitResponse LOCAL_QUIT = new LocalQuitResponse();

    private final CommandController command;
    private final SwingQuitView view;
    private final FakeModel model;

    private final AtomicBoolean approved = new AtomicBoolean();

    public QuitController(CommandController command, SwingQuitView view, FakeModel model) {
        this.command = command;
        this.view = view;
        this.model = model;

        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                quit();
            }
        });
    }


    public void quit() {
        quit(LOCAL_QUIT);
    }

    public void quit(final QuitResponse r) {
        log("QuitController.quit");
        if (approved()) {
            log("    (was already approved)");
            // TERMINATE APP:
            if (Objects.nonNull(this.view)) {
                log("    will dispose window");
                this.view.dispose();
            } else {
                System.exit(0);
            }
        } else {
            log("    (not yet approved)");
            final DialogViews.QuitOptions answer;
            if (this.model.isDirty()) {
                log("    model is dirty");
                answer = this.view.dialogs().askSaveDiscardCancel();
            } else {
                log("    model is not dirty");
                answer = DialogViews.QuitOptions.DISCARD;
            }
            if (answer == DialogViews.QuitOptions.CANCEL) {
                log("    user canceled shutdown");
                r.cancelQuit();
            } else {
                if (answer == DialogViews.QuitOptions.SAVE) {
                    log("    user chose to Save changes");
                    this.command.save();
                } else if (answer == DialogViews.QuitOptions.TIMED_OUT) {
                    log("    user did not respond; will auto-save changes");
                    this.command.save(false);
                } else {
                    log("    user chose to Discard changes");
                }
                this.approved.set(true);
                r.performQuit();
            }
        }
    }

    public boolean approved() {
        return this.approved.get();
    }

    private static void log(final String s) {
        System.out.println(s);
        System.out.flush();
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
