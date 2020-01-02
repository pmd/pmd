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

import net.sourceforge.pmd.util.document.io.TextFileBehavior;


class TextEditorImpl implements TextEditor {

    private final TextDocumentImpl document;

    private final IoBuffer out;

    private boolean open = true;

    private SortedMap<Integer, Integer> accumulatedOffsets = new TreeMap<>();
    private List<TextRegion> affectedRegions = new ArrayList<>();


    TextEditorImpl(final TextDocumentImpl document, final TextFileBehavior backend) throws IOException {
        if (backend.isReadOnly()) {
            throw new UnsupportedOperationException(backend + " is readonly");
        }

        this.out = new IoBuffer(document.getText(), document.getCurStamp(), backend);
        this.document = document;
    }

    private void ensureOpen() {
        if (!open) {
            throw new IllegalStateException("Closed handler");
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (this) {
            ensureOpen();
            open = false;

            out.close(document);
        }
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

            for (TextRegion reg : affectedRegions) {
                if (reg.overlaps(region)
                    || region.isEmpty() && reg.contains(region.getStartOffset())) {
                    throw new OverlappingOperationsException(reg, region);
                }
            }

            affectedRegions.add(region);

            TextRegion realPos = shiftOffset(region, textToReplace.length() - region.getLength());

            out.replace(realPos, textToReplace);
        }
    }


    private TextRegion shiftOffset(TextRegion origCoords, int lenDiff) {
        // these data structures are not the most adapted (a binary tree would be)


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
