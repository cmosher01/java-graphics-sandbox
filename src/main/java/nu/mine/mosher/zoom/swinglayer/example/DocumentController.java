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

/*
DESIGN NOTES:

opt doc = empty

FOR GEDCOM:
Open
Close
Save As...
Export Skeletons (changed)
Export Skeletons (all)
Export PDF
Export SVG

canOpen: if doc is empty
canClose: if doc is present
canSaveAs: if doc is present
canExpChanges: if doc is dirty
canExpAll: if doc is present
canExpPdf: if doc is present
canExpSvg: if doc is present

void onOpen()
    assert doc is empty
    path = askOpenFile()
    if path is present
        doc = open file at path
    else if path is empty
        do nothing

void onClose()
    assert doc is present
    if doc is dirty
        a = askSaveDiscardCancel()
        if a = save
            path = askSaveAs(suggest timestamp path/file)
            if path is present
                ok = save doc to path
                if saved ok
                    doc = empty (close file)
                else error during save
                    do nothing
            else if path is empty
                do nothing
        else if a = discard
            doc = empty (close file)
        else if a = timeout
            do nothing
        else if a = cancel
            do nothing
    else
        doc = empty (close file)

void onSaveAs()
    assert doc is present
    path = askSaveAs(suggest timestamp path/file)
    if path is present
        ok = save doc to path
        if saved ok
            set doc's dirty = false
            set doc's file = path
        else if error while saving
            do nothing
    else if path is empty
        do nothing

void onExpChanges() export skeleton of changes
void onExpAll() export skeleton of all
void onExpPdf() export all to PDF
void onExpSvg() export all to SVG

all of these need to ask for a "export to" file
and write to that file (or do nothing on Cancel)





FOR FTM:
Open
Close
Save
Export PDF
Export SVG

canOpen: if doc is empty
canClose: if doc is present
canSave: if doc is dirty
canExpPdf: if doc is present
canExpSvg: if doc is present

void onOpen()
    assert doc is empty
    path = askOpenFile()
    if path is present
        doc = open database and read contents
    else if path is empty
        do nothing

void onClose()
    assert doc is present
    if doc is dirty
        a = askSaveDiscardCancel()
        if a = save
            ok = save doc changes to database
            if saved ok
                doc = empty (close database)
            else error during save
                do nothing
        else if a = discard
            doc = empty (close database)
        else if a = timeout
            do nothing
        else if a = cancel
            do nothing
    else
        doc = empty (close file)

void onSave()
    assert doc is present
    assert doc is dirty
    ok = save doc changes to database
    if saved ok
        set doc's dirty = false
    else if error while saving
        do nothing

void onExpPdf() export all to PDF
void onExpSvg() export all to SVG

both of these need to ask for a "export to" file
and write to that file (or do nothing on Cancel)

*/

import lombok.*;

import java.nio.file.Path;
import java.util.*;

@RequiredArgsConstructor
public class DocumentController {
    public static final boolean ATTENDED = true;
    public static final boolean UNATTENDED = false;

    private final DocumentModel document;
    private final ZoomPanModel zp;
    private final FrameView viewFrame;
    private final MenuView viewMenu;
    private final MainView viewMain;
    private final InteractiveRectsView viewInteractiveRects;



    // let user choose file to open
    public void open() {
        open(this.viewFrame.dialogs().chooseFilesToOpen());
    }

    // open the first path, ignore the rest
    public void open(final List<Path> paths) {
        if (!paths.isEmpty()) {
            open(paths.getFirst());
        }
    }

    public void open(final Path path) {
        try {
            this.document.open(path);
        } catch (final Exception e) {
            // TODO log and show error message
            System.out.println("WARNING file may not be fully loaded");
        }
        this.zp.setZoomOutMinFromBounds(this.document.bounds());

        this.viewInteractiveRects.refresh();
        this.viewMain.repaint();
        this.viewFrame.refresh();
        this.viewMenu.refresh();
    }



    public boolean close() {
        return close(ATTENDED);
    }

    public boolean close(boolean attended) {
        boolean ok;
        if (attended) {
            if (this.document.isModified()) {
                val a = this.viewFrame.dialogs().askSaveDiscardCancel();
                if (a == DialogViews.QuitOptions.SAVE || a == DialogViews.QuitOptions.TIMED_OUT) {
                    final Optional<Path> optPath;
                    if (this.document.isFileReadOnly()) {
                        optPath = this.viewFrame.dialogs().saveAsFileDialog(Optional.of(this.document.suggestSaveAsPath()));
                    } else {
                        optPath = Optional.of(this.document.suggestSaveAsPath());
                    }
                    ok = optPath.isPresent();
                    if (ok) {
                        this.document.setPath(optPath.get());
                        ok = this.document.save();
                        if (ok) {
                            this.document.setUnmodified();
                            this.document.close();
                            // ok = true;
                        } else {
                            // TODO log error message, and show it if attended
                            // ok = false;
                        }
                    }
                } else if (a == DialogViews.QuitOptions.DISCARD) {
                    this.document.close();
                    ok = true;
                } else { // if (a == DialogViews.QuitOptions.CANCEL)
                    ok = false;
                }
            } else {
                this.document.close();
                ok = true;
            }
        } else { // unattended
            ok = saveAs(UNATTENDED);
            if (ok) {
                this.document.close();
            }
        }

        this.viewInteractiveRects.refresh();
        this.viewMain.repaint();
        this.viewFrame.refresh();
        this.viewMenu.refresh();

        return ok;
    }



    public boolean save() {
        return save(ATTENDED);
    }

    /**
     * Implements "save (to same) file" functionality.
     * @param attended true if user is there, false if user is away
     * @return true if file was successfully saved,
     * false if an error occurred (maybe file saved, maybe not, maybe file is corrupt)
     */
    public boolean save(final boolean attended) {
        boolean ok;
        if (this.document.isFileReadOnly()) {
            ok = saveAs(attended);
        } else {
            // NOTE: this would need to be modified in apps that allow "untitled" new docs
            ok = this.document.save();
            if (ok) {
                this.document.onSaved();
                // ok = true;
            } else {
                // TODO log error message, and show it if attended
                // ok = false;
            }
        }
        this.viewFrame.refresh();
        this.viewMenu.refresh();
        return ok;
    }



    public boolean saveAs() {
        return saveAs(ATTENDED);
    }

    /**
     * Implements "save as new file" functionality.
     * @param attended true if user is (probably) there, false if user is (probably) away
     * @return true if file was successfully saved,
     * false if an error occurred (maybe file saved, maybe not, maybe file is corrupt)
     */
    public boolean saveAs(final boolean attended) {
        final Optional<Path> optPath;
        if (attended) {
            optPath = this.viewFrame.dialogs().saveAsFileDialog(Optional.of(this.document.suggestSaveAsPath()));
        } else {
            optPath = Optional.of(this.document.suggestSaveAsPath());
        }

        boolean ok = optPath.isPresent();
        if (ok) {
            this.document.setPath(optPath.get());
            ok = this.document.save();
            if (ok) {
                this.document.onSaved();
                // ok = true;
            } else {
                // TODO log error message, and show it if attended
                // ok = false;
            }
        }

        this.viewFrame.refresh();
        this.viewMenu.refresh();
        return ok;
    }
}
