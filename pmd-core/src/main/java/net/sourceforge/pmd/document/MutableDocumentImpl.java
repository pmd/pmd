/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;


class MutableDocumentImpl extends DocumentImpl implements MutableDocument {

    private ReplaceHandler out;
    private SortedMap<Integer, Integer> accumulatedOffsets = new TreeMap<>();


    MutableDocumentImpl(final CharSequence source, final ReplaceHandler writer) {
        super(source);
        this.out = writer;
    }

    @Override
    public void insert(int beginLine, int beginColumn, final String textToInsert) {
        insert(positioner.offsetFromLineColumn(beginLine, beginColumn), textToInsert);
    }

    @Override
    public void insert(int offset, String textToInsert) {
        replace(createRegion(offset, 0), textToInsert);
    }


    @Override
    public void delete(final TextRegion region) {
        replace(region, "");
    }

    @Override
    public void replace(final TextRegion region, final String textToReplace) {

        TextRegion realPos = shiftOffset(region, textToReplace.length() - region.getLength());

        out.replace(realPos, textToReplace);
    }

    private TextRegion shiftOffset(TextRegion origCoords, int lenDiff) {
        // instead of using a map, a balanced binary tree would be more efficient
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
                                 : new TextRegionImpl(origCoords.getStartOffset() + shift, origCoords.getLength());

        accumulatedOffsets.compute(origCoords.getStartOffset(), (k, v) -> {
            int s = v == null ? lenDiff : v + lenDiff;
            return s == 0 ? null : s; // delete mapping if shift is 0
        });

        return realPos;
    }


    @Override
    public CharSequence getUncommittedText() {
        return out.getCurrentText(this);
    }

    @Override
    public void close() throws IOException {
        out = out.commit();
        positioner = new SourceCodePositioner(out.getCurrentText(this));
        accumulatedOffsets = new TreeMap<>();
    }

}
