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


import java.awt.desktop.QuitResponse;
import java.awt.event.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static nu.mine.mosher.zoom.swinglayer.example.CommandController.*;
import static nu.mine.mosher.zoom.swinglayer.example.DialogViews.QuitOptions.*;


public class QuitController {
    private final CommandController command;
    private final FrameView view;
    private final InteractiveRectsModel model;
    private final StatusBarController controllerStatusBar;

    private final AtomicBoolean approved = new AtomicBoolean();


    public QuitController(final CommandController command, final FrameView view, final InteractiveRectsModel model, StatusBarController controllerStatusBar) {
        this.command = command;
        this.view = view;
        this.model = model;
        this.controllerStatusBar = controllerStatusBar;

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

        2. "Are you sure?" dialog w/ SAVE, DISCARD, CANCEL options, and (15? second) TIMEOUT

        3. if CANCEL:
               cancel shutdown process and continue running app as-is
               in OS shutdown case: signal OS to abort
           else:
               if SAVE:
                   save all unsaved changes
                       on TIMEOUT of dialog:
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
        if (approved()) {
            this.controllerStatusBar.close();
            this.view.dispose(); // terminates EDT (and therefore the application)
        } else {
            final DialogViews.QuitOptions answer;
            if (this.model.isDirty()) {
                answer = this.view.dialogs().askSaveDiscardCancel();
            } else {
                answer = DISCARD;
            }

            if (answer == CANCEL) {
                r.cancelQuit();
            } else {
                if (answer == SAVE) {
                    this.command.save(ATTENDED);
                } else if (answer == TIMED_OUT) {
                    this.command.save(UNATTENDED);
                }

                this.approved.set(true);
                r.performQuit();
            }
        }
    }

    public boolean approved() {
        return this.approved.get();
    }
}
