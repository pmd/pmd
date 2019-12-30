/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;

import net.sourceforge.pmd.util.document.TextRegion.RegionWithLines;
import net.sourceforge.pmd.util.document.TextRegionImpl.WithLineInfo;
import net.sourceforge.pmd.util.document.io.PhysicalTextSource;


class TextDocumentImpl implements TextDocument {

    private static final String OUT_OF_BOUNDS_WITH_LINES =
        "Region [bpos=(%d, %d), epos = (%d, %d)] is not in range of this document";

    private static final String OUT_OF_BOUNDS_WITH_OFFSET =
        "Region [%d, +%d] is not in range of this document";

    private final PhysicalTextSource backend;

    private long curStamp;

    /** The positioner has the original source file. */
    private SourceCodePositioner positioner;

    TextDocumentImpl(PhysicalTextSource backend) throws IOException {
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
        int bline = positioner.lineNumberFromOffset(region.getStartOffset());
        int bcol = positioner.columnFromOffset(bline, region.getStartOffset());
        int eline = positioner.lineNumberFromOffset(region.getEndOffset());
        int ecol = positioner.columnFromOffset(eline, region.getEndOffset());

        return createRegion(bline, bcol, eline, ecol);
    }

    @Override
    public RegionWithLines createRegion(int beginLine, int beginColumn, int endLine, int endColumn) {
        // TODO checks, positioner should return -1 if not found
        int startOffset = positioner.offsetFromLineColumn(beginLine, beginColumn);
        int endOffset = positioner.offsetFromLineColumn(endLine, endColumn);

        if (startOffset < 0 || endOffset < 0) {
            throw new IndexOutOfBoundsException(
                String.format(OUT_OF_BOUNDS_WITH_LINES,
                              beginLine, beginColumn,
                              endLine, endColumn)
            );
        }

        return new WithLineInfo(startOffset, endOffset - startOffset,
                                beginLine, beginColumn, endLine, endColumn);
    }

    @Override
    public TextRegion createRegion(int offset, int length) {
        if (offset < 0 || offset + length > positioner.getSourceCode().length()) {
            throw new IndexOutOfBoundsException(String.format(OUT_OF_BOUNDS_WITH_OFFSET, offset, length));
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

    void setText(CharSequence text) {
        positioner = new SourceCodePositioner(text);
    }

    @Override
    public CharSequence subSequence(TextRegion region) {
        return getText().subSequence(region.getStartOffset(), region.getEndOffset());
    }

}
