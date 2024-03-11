/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.IOException;
import java.util.Objects;

import net.sourceforge.pmd.internal.util.BaseCloseable;
import net.sourceforge.pmd.lang.LanguageVersion;


/**
 * A text document directly backed by a {@link TextFile}. In the future
 * some other implementations of the interface may be eg views on part
 * of another document.
 */
final class RootTextDocument extends BaseCloseable implements TextDocument {

    private final TextFile backend;


    // to support CPD with the same api, we could probably just store
    // a soft reference to the contents, and build the positioner eagerly.
    private final TextFileContent content;

    private final LanguageVersion langVersion;

    private final FileId fileId;

    RootTextDocument(TextFile backend) throws IOException {
        this.backend = backend;
        this.content = backend.readContents();
        this.langVersion = backend.getLanguageVersion();
        this.fileId = backend.getFileId();

        Objects.requireNonNull(langVersion, "Null language version for file " + backend);
        Objects.requireNonNull(fileId, "Null path id for file " + backend);
    }

    @Override
    public LanguageVersion getLanguageVersion() {
        return langVersion;
    }

    @Override
    public FileId getFileId() {
        return fileId;
    }

    @Override
    protected void doClose() throws IOException {
        backend.close();
    }

    @Override
    public Chars getText() {
        return content.getNormalizedText();
    }

    @Override
    public FileLocation toLocation(TextRegion region) {
        checkInRange(region, this.getLength());
        SourceCodePositioner positioner = content.getPositioner();

        // We use longs to return both numbers at the same time
        // This limits us to 2 billion lines or columns, which is FINE
        TextPos2d bpos = positioner.lineColFromOffset(region.getStartOffset(), true);
        TextPos2d epos = region.isEmpty() ? bpos
                                          : positioner.lineColFromOffset(region.getEndOffset(), false);

        return new FileLocation(
            fileId,
            bpos.getLine(),
            bpos.getColumn(),
            epos.getLine(),
            epos.getColumn(),
            region
        );
    }

    @Override
    public TextPos2d lineColumnAtOffset(int offset, boolean inclusive) {
        return content.getPositioner().lineColFromOffset(offset, inclusive);
    }

    @Override
    public int offsetAtLineColumn(TextPos2d position) {
        return content.getPositioner().offsetFromLineColumn(position.getLine(), position.getColumn());
    }

    @Override
    public TextRegion createLineRange(int startLineInclusive, int endLineInclusive) {
        SourceCodePositioner positioner = content.getPositioner();

        if (!positioner.isValidLine(startLineInclusive)
            || !positioner.isValidLine(endLineInclusive)
            || startLineInclusive > endLineInclusive) {
            throw invalidLineRange(startLineInclusive, endLineInclusive, positioner.getLastLine());
        }

        int first = positioner.offsetFromLineColumn(startLineInclusive, 1);
        int last = positioner.offsetOfEndOfLine(endLineInclusive);
        return TextRegion.fromBothOffsets(first, last);
    }

    static void checkInRange(TextRegion region, int length) {
        if (region.getEndOffset() > length) {
            throw regionOutOfBounds(region.getStartOffset(), region.getEndOffset(), length);
        }
    }

    @Override
    public long getCheckSum() {
        return content.getCheckSum();
    }

    @Override
    public Chars sliceOriginalText(TextRegion region) {
        return getText().subSequence(region.getStartOffset(), region.getEndOffset());
    }

    private static final String NOT_IN_RANGE = "Region [start=%d, end=%d[ is not in range of this document (length %d)";
    private static final String INVALID_LINE_RANGE = "Line range %d..%d is not in range of this document (%d lines) (line numbers are 1-based)";
    private static final String INVALID_OFFSET = "Offset %d is not in range of this document (length %d) (offsets are 0-based)";

    static IndexOutOfBoundsException invalidLineRange(int start, int end, int numLines) {
        return new IndexOutOfBoundsException(String.format(INVALID_LINE_RANGE, start, end, numLines));
    }

    static IndexOutOfBoundsException regionOutOfBounds(int start, int end, int maxLen) {
        return new IndexOutOfBoundsException(String.format(NOT_IN_RANGE, start, end, maxLen));
    }

    static IndexOutOfBoundsException invalidOffset(int offset, int maxLen) {
        return new IndexOutOfBoundsException(String.format(INVALID_OFFSET, offset, maxLen));
    }
}
