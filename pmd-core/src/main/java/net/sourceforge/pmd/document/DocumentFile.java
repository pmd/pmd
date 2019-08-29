/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import static java.util.Objects.requireNonNull;
import static net.sourceforge.pmd.document.TextRegion.newRegionByLine;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import net.sourceforge.pmd.document.TextRegion.RegionByLine;
import net.sourceforge.pmd.document.TextRegion.RegionByOffset;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;


import org.apache.commons.io.IOUtils;

/**
 * Implementation that handles a Document as a file in the filesystem and receives operations in a sorted manner
 * (i.e. the regions are sorted). This improves the efficiency of reading the file by only scanning it once while
 * operations are applied, until an instance of this document is closed.
 */
public class DocumentFile implements Document, Closeable {

    private final ReplaceFunction out;
    /** The positioner has the original source file. */
    private final SourceCodePositioner positioner;
    private final SortedMap<Integer, Integer> accumulatedOffsets = new TreeMap<>();


    public DocumentFile(final File file, final Charset charset) throws IOException {
        byte[] bytes = Files.readAllBytes(requireNonNull(file).toPath());
        String text = new String(bytes, requireNonNull(charset));
        positioner = new SourceCodePositioner(text);
        out = ReplaceFunction.bufferedFile(text, file.toPath(), charset);
    }

    public DocumentFile(final String source, final ReplaceFunction writer) {
        this.out = writer;
        positioner = new SourceCodePositioner(source);
    }

    @Override
    public void insert(int beginLine, int beginColumn, final String textToInsert) {
        insert(positioner.offsetFromLineColumn(beginLine, beginColumn), textToInsert);
    }

    @Override
    public void insert(int offset, String textToInsert) {
        replace(createByOffset(offset, 0), textToInsert);
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

        RegionByOffset realPos = shift == 0 ? origCoords
                                            : createByOffset(origCoords.getOffset() + shift, origCoords.getLength());

        accumulatedOffsets.compute(origCoords.getOffset(), (k, v) -> {
            int s = v == null ? lenDiff : v + lenDiff;
            return s == 0 ? null : s; // delete mapping if shift is 0
        });

        return realPos;
    }

    @Override
    public RegionByLine mapToLine(RegionByOffset offset) {
        int bline = positioner.lineNumberFromOffset(offset.getOffset());
        int bcol = positioner.columnFromOffset(bline, offset.getOffset());
        int eline = positioner.lineNumberFromOffset(offset.getOffsetAfterEnding());
        int ecol = positioner.columnFromOffset(eline, offset.getOffsetAfterEnding());

        return newRegionByLine(bline, bcol, eline, ecol);
    }

    @Override
    public RegionByOffset mapToOffset(final RegionByLine regionByLine) {

        int offset = positioner.offsetFromLineColumn(regionByLine.getBeginLine(), regionByLine.getBeginColumn());
        int len = positioner.offsetFromLineColumn(regionByLine.getEndLine(), regionByLine.getEndColumn())
            - offset;


        return createByOffset(offset, len);
    }

    private RegionByOffset createByOffset(int offset, int len) {

        if (offset < 0) {
            throw new IndexOutOfBoundsException(
                "Region (" + offset + ",+" + len + ") is not in range of this document");
        }

        return TextRegion.newRegionByOffset(offset, len);
    }

    @Override
    public void close() throws IOException {
        out.commit();
    }

}
