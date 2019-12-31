/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.util.document.TextRegion.RegionWithLines;
import net.sourceforge.pmd.util.document.TextRegionImpl.WithLineInfo;
import net.sourceforge.pmd.util.document.io.TextFile;


final class TextDocumentImpl implements TextDocument {

    private static final String OUT_OF_BOUNDS_WITH_LINES =
        "Region [bpos=(%d, %d), epos = (%d, %d)] is not in range of this document (length %d)";

    private static final String OUT_OF_BOUNDS_WITH_OFFSET =
        "Region [%d, +%d] is not in range of this document (length %d)";

    private final TextFile backend;

    private long curStamp;

    /** The positioner has the original source file. */
    private SourceCodePositioner positioner;

    TextDocumentImpl(TextFile backend) throws IOException {
        this.backend = backend;
        this.curStamp = backend.fetchStamp();
        this.positioner = new SourceCodePositioner(backend.readContents());
    }

    @Override
    public boolean isReadOnly() {
        return backend.isReadOnly();
    }

    @Override
    public TextEditor newEditor() throws IOException {
        return new TextEditorImpl(this, backend);
    }

    @Override
    public RegionWithLines addLineInfo(TextRegion region) {
        if (region.getEndOffset() > getText().length()) {
            throw new IndexOutOfBoundsException(
                String.format(
                    OUT_OF_BOUNDS_WITH_OFFSET,
                    region.getStartOffset(),
                    region.getLength(),
                    getText().length()
                )
            );
        }

        int bline = positioner.lineNumberFromOffset(region.getStartOffset());
        int bcol = positioner.columnFromOffset(bline, region.getStartOffset());
        int eline = positioner.lineNumberFromOffset(region.getEndOffset());
        int ecol = positioner.columnFromOffset(eline, region.getEndOffset());

        return new WithLineInfo(
            region.getStartOffset(),
            region.getLength(),
            bline, bcol,
            eline, ecol
        );
    }

    @Override
    public TextRegion createRegion(int offset, int length) {
        if (offset < 0 || offset + length > getText().length()) {
            throw new IndexOutOfBoundsException(
                String.format(
                    OUT_OF_BOUNDS_WITH_OFFSET,
                    offset,
                    length,
                    getText().length()
                )
            );
        }

        return new TextRegionImpl(offset, length);
    }

    @Override
    public CharSequence getText() {
        return positioner.getSourceCode();
    }

    long getCurStamp() {
        return curStamp;
    }

    void setText(CharSequence text, long stamp) {
        this.positioner = new SourceCodePositioner(text.toString());
        this.curStamp = stamp;
    }

    @Override
    public CharSequence subSequence(TextRegion region) {
        return getText().subSequence(region.getStartOffset(), region.getEndOffset());
    }

}
