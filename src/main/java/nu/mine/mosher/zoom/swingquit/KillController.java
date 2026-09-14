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

public class KillController {
    private final QuitController quit;
    private final CommandController command;

    public KillController(final CommandController command, final QuitController quit) {
        this.quit = quit;
        this.command = command;
        Runtime.getRuntime().addShutdownHook(new Thread(this::shuttingDown));
    }

    private void shuttingDown() {
        System.out.println("SHUTDOWN HOOK:");
        System.out.println("    quitting already approved?: "+this.quit.approved());
        System.out.flush();
        // TODO can we just check if the model exists and is dirty, instead of "approved"
        // this means the model would be removed by the quit controller
        if (!this.quit.approved()) {
            System.out.println("    will auto-save changes");
            this.command.save(false);
        } else {
            System.out.println("    [nothing to clean up]");
        }
    }
}
