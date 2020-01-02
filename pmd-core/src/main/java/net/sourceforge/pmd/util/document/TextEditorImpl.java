/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.pmd.util.document.io.ReadOnlyFileException;
import net.sourceforge.pmd.util.document.io.TextFileBehavior;
import net.sourceforge.pmd.internal.util.BaseCloseable;


class TextEditorImpl extends BaseCloseable implements TextEditor {

    private final TextDocumentImpl document;

    private final IoBuffer out;

    private SortedMap<Integer, Integer> accumulatedOffsets = new TreeMap<>();
    private List<TextRegion> affectedRegions = new ArrayList<>();


    /** @throws ReadOnlyFileException If the backend is read-only */
    TextEditorImpl(final TextDocumentImpl document, final TextFileBehavior backend) throws IOException {
        this.out = new IoBuffer(document.getText(), document.getCurStamp(), backend);
        this.document = document;
    }

    @Override
    protected void ensureOpen() {
        try {
            super.ensureOpen();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    protected void doClose() throws IOException {
        if (!affectedRegions.isEmpty()) {
            out.close(document);
        }
    }

    void sever() {
        open = false; // doClose will never be called
    }

    @Override
    public void drop() {
        ensureOpen();
        out.reset();
        accumulatedOffsets.clear();
        affectedRegions.clear();
    }

    @Override
    public void insert(int offset, String textToInsert) {
        replace(document.createRegion(offset, 0), textToInsert);
    }

    @Override
    public void delete(TextRegion region) {
        replace(region, "");
    }

    @Override
    public void replace(final TextRegion region, final String textToReplace) {
        synchronized (this) {
            ensureOpen();

            for (TextRegion changedRegion : affectedRegions) {
                if (changedRegion.overlaps(region)
                    || region.isEmpty() && changedRegion.containsChar(region.getStartOffset())) {
                    throw new OverlappingOperationsException(changedRegion, region);
                }
            }

            affectedRegions.add(region);

            TextRegion realPos = shiftOffset(region, textToReplace.length() - region.getLength());

            out.replace(realPos, textToReplace);
        }
    }


    private TextRegion shiftOffset(TextRegion origCoords, int lenDiff) {
        // these data structures are not the most adapted, we'll see if
        // that poses a performance problem

        ArrayList<Integer> keys = new ArrayList<>(accumulatedOffsets.keySet());
        int idx = Collections.binarySearch(keys, origCoords.getStartOffset());

        if (idx < 0) {
            // there is no entry exactly for this offset, so that binarySearch
            // returns the correct insertion index (but inverted)
            idx = -(idx + 1);
        } else {
            // there is an exact entry
            // since the loop below stops at idx, increment it to take that last entry into account
            idx++;
        }

        // compute the shift accumulated by the mutations that have occurred
        // left of the start index
        int shift = 0;
        for (int i = 0; i < idx; i++) {
            shift += accumulatedOffsets.get(keys.get(i));
        }

        TextRegion realPos = shift == 0
                             ? origCoords
                             // don't check the bounds
                             : TextRegionImpl.fromOffsetLength(
                                 origCoords.getStartOffset() + shift, origCoords.getLength());

        accumulatedOffsets.compute(origCoords.getStartOffset(), (k, v) -> {
            int s = v == null ? lenDiff : v + lenDiff;
            return s == 0 ? null : s; // delete mapping if shift is 0
        });

        return realPos;
    }


}
