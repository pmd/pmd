/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.pmd.document.TextRegion.RegionByLine;
import net.sourceforge.pmd.document.TextRegion.RegionByOffset;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;


class DocumentImpl implements MutableDocument {

    private ReplaceFunction out;
    /** The positioner has the original source file. */
    private SourceCodePositioner positioner;
    private SortedMap<Integer, Integer> accumulatedOffsets = new TreeMap<>();


    public DocumentImpl(final String source, final ReplaceFunction writer) {
        this.out = writer;
        positioner = new SourceCodePositioner(source);
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
        RegionByOffset off = region.toOffset(this);

        RegionByOffset realPos = shiftOffset(off, textToReplace.length() - off.getLength());

        out.replace(realPos, textToReplace);
    }

    private RegionByOffset shiftOffset(RegionByOffset origCoords, int lenDiff) {
        ArrayList<Integer> keys = new ArrayList<>(accumulatedOffsets.keySet());
        int idx = Collections.binarySearch(keys, origCoords.getOffset());

        if (idx < 0) {
            idx = -(idx + 1);
        } else {
            idx++;
        }

        int shift = 0;
        for (int i = 0; i < idx; i++) {
            shift += accumulatedOffsets.get(keys.get(i));
        }

        RegionByOffset realPos = shift == 0
                                 ? origCoords
                                 // don't check it
                                 : new RegionByOffsetImp(origCoords.getOffset() + shift, origCoords.getLength());

        accumulatedOffsets.compute(origCoords.getOffset(), (k, v) -> {
            int s = v == null ? lenDiff : v + lenDiff;
            return s == 0 ? null : s; // delete mapping if shift is 0
        });

        return realPos;
    }

    @Override
    public RegionByLine mapToLine(RegionByOffset region) {
        int bline = positioner.lineNumberFromOffset(region.getOffset());
        int bcol = positioner.columnFromOffset(bline, region.getOffset());
        int eline = positioner.lineNumberFromOffset(region.getOffsetAfterEnding());
        int ecol = positioner.columnFromOffset(eline, region.getOffsetAfterEnding());

        return createRegion(bline, bcol, eline, ecol);
    }

    @Override
    public RegionByOffset mapToOffset(RegionByLine region) {
        int offset = positioner.offsetFromLineColumn(region.getBeginLine(), region.getBeginColumn());
        int len = positioner.offsetFromLineColumn(region.getEndLine(), region.getEndColumn())
            - offset;

        return createRegion(offset, len);
    }

    @Override
    public RegionByLine createRegion(int beginLine, int beginColumn, int endLine, int endColumn) {
        // TODO checks, positioner should return -1
        return TextRegion.newRegionByLine(beginLine, beginColumn, endLine, endColumn);
    }

    @Override
    public RegionByOffset createRegion(int offset, int length) {
        if (offset < 0 || offset + length > positioner.getSourceCode().length()) {
            throw new IndexOutOfBoundsException(
                "Region (" + offset + ",+" + length + ") is not in range of this document");
        }


        return new RegionByOffsetImp(offset, length);
    }

    @Override
    public CharSequence getText() {
        return positioner.getSourceCode();
    }

    @Override
    public CharSequence subSequence(TextRegion region) {
        RegionByOffset byOffset = region.toOffset(this);
        return getText().subSequence(byOffset.getOffset(), byOffset.getOffsetAfterEnding());
    }

    @Override
    public CharSequence getUncommittedText() {
        return out.getCurrentText(this);
    }

    @Override
    public void close() throws IOException {
        out = out.commit();
        positioner = new SourceCodePositioner(out.getCurrentText(this).toString());
        accumulatedOffsets = new TreeMap<>();
    }

}
