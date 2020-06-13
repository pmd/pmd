/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.io.IOException;
import java.io.Reader;

import net.sourceforge.pmd.internal.util.BaseCloseable;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.util.document.io.TextFile;
import net.sourceforge.pmd.util.document.io.TextFileContent;


final class TextDocumentImpl extends BaseCloseable implements TextDocument {

    private final TextFile backend;

    private SourceCodePositioner positioner;

    private TextFileContent content;

    private final LanguageVersion langVersion;

    private final String fileName;

    TextDocumentImpl(TextFile backend, LanguageVersion langVersion) throws IOException {
        this.backend = backend;
        this.content = backend.readContents();
        this.langVersion = langVersion;
        this.positioner = null;
        this.fileName = backend.getDisplayName();
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return langVersion;
    }

    @Override
    public String getDisplayName() {
        return fileName;
    }

    @Override
    protected void doClose() throws IOException {
        backend.close();
    }

    @Override
    public FileLocation toLocation(TextRegion region) {
        checkInRange(region);

        if (positioner == null) {
            // if nobody cares about lines, this is not computed
            positioner = new SourceCodePositioner(getText());
        }

        int bline = positioner.lineNumberFromOffset(region.getStartOffset());
        int bcol = positioner.columnFromOffset(bline, region.getStartOffset());
        int eline = positioner.lineNumberFromOffset(region.getEndOffset());
        int ecol = positioner.columnFromOffset(eline, region.getEndOffset());

        return new FileLocation(
            fileName,
            bline, bcol,
            eline, ecol
        );
    }

    @Override
    public TextRegion createLineRange(int startLineInclusive, int endLineInclusive) {
        if (!positioner.isValidLine(startLineInclusive)
            || !positioner.isValidLine(endLineInclusive)
            || startLineInclusive > endLineInclusive) {
            throw invalidLineRange(startLineInclusive, endLineInclusive, positioner.getLastLine());
        }

        int first = positioner.offsetFromLineColumn(startLineInclusive, 1);
        int last = positioner.offsetOfEndOfLine(endLineInclusive);
        return TextRegion.fromBothOffsets(first, last);
    }

    void checkInRange(TextRegion region) {
        if (region.getEndOffset() > getLength()) {
            throw regionOutOfBounds(region.getStartOffset(), region.getEndOffset(), getLength());
        }
    }

    @Override
    public Chars getText() {
        return content.getNormalizedText();
    }

    @Override
    public Reader newReader() {
        return getText().newReader();
    }

    @Override
    public int getLength() {
        return getText().length();
    }

    @Override
    public Chars slice(TextRegion region) {
        return getText().subSequence(region.getStartOffset(), region.getEndOffset());
    }

    private static final String NOT_IN_RANGE = "Region {start=%d, end=%d} is not in range of this document (length %d)";
    private static final String INVALID_LINE_RANGE = "Line range %d..%d is not in range of this document (%d lines) (line numbers are 1-based)";

    static IndexOutOfBoundsException invalidLineRange(int start, int end, int numLines) {
        return new IndexOutOfBoundsException(String.format(INVALID_LINE_RANGE, start, end, numLines));
    }

    static IndexOutOfBoundsException regionOutOfBounds(int start, int end, int maxLen) {
        return new IndexOutOfBoundsException(String.format(NOT_IN_RANGE, start, end, maxLen));
    }
}
