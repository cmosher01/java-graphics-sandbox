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


import static nu.mine.mosher.zoom.swinglayer.example.DocumentController.*;

@SuppressWarnings("ClassCanBeRecord")
public class KillController {
    private final QuitController controllerQuit;
    private final DocumentController controllerDocument;

    public KillController(final DocumentController controllerDocument, final QuitController controllerQuit) {
        this.controllerQuit = controllerQuit;
        this.controllerDocument = controllerDocument;
        Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
    }

    private void onShutdown() {
        if (!this.controllerQuit.approved()) {
            this.controllerDocument.close(UNATTENDED);
        }
    }
}
