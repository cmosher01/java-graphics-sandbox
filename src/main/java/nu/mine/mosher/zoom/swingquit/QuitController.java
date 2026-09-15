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

import java.awt.desktop.QuitResponse;
import java.awt.event.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class QuitController {
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
        quit(new QuitResponse() {
            @Override public void performQuit() { quit(); }
            @Override public void  cancelQuit() { }
        });
    }


    /*
        All types of quit requests should perform these steps:

        1. close/clean up everything in the app that doesn't need human interaction (i.e., all but unsaved changes)

        2. "Are you sure?" dialog w/ SAVE, DISCARD, CANCEL options (and 15 sec. timeout to auto SAVE)

        3. if CANCEL:
               cancel shutdown process and continue running app as-is
               in OS shutdown case: signal OS to abort
           else:
               if SAVE:
                   save all unsaved changes
                       on timeout of dialog:
                           untitled items can get auto timestamp-UUID name
                           others can get auto save w/backup of on-disk version
                           continue OS shutdown
                       on user asked to save untitled item, for each item:
                           on SAVE ("APPROVE_OPTION"): save and continue
                           on CANCEL (anything but APPROVE_OPTION):
                               cancel shutdown process and continue running app as-is
                               in OS shutdown case: signal OS to abort
               exit app (and continue OS shutdown in that case)
     */
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
                log("    model is not dirty (OK to discard)");
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
                    log("    any changes will be discarded");
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
}
