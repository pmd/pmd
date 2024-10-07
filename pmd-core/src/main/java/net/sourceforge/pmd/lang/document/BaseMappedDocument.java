/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.io.IOException;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Base class for documents that apply a transform to their output offsets.
 * This includes translated documents, and slices (subdocument views).
 */
abstract class BaseMappedDocument implements TextDocument {

    protected final TextDocument base;

    BaseMappedDocument(TextDocument base) {
        this.base = base;
    }

    @Override
    public long getCheckSum() {
        return base.getCheckSum();
    }

    @Override
    public FileId getFileId() {
        return base.getFileId();
    }

    @Override
    public Chars sliceOriginalText(TextRegion region) {
        return base.sliceOriginalText(inputRegion(region));
    }

    @Override
    public FileLocation toLocation(TextRegion region) {
        return base.toLocation(inputRegion(region));
    }

    @Override
    public TextRegion createLineRange(int startLineInclusive, int endLineInclusive) {
        // see the doc, lines do not need to be translated
        return base.createLineRange(startLineInclusive, endLineInclusive);
    }

    @Override
    public TextPos2d lineColumnAtOffset(int offset, boolean inclusive) {
        return base.lineColumnAtOffset(inputOffset(offset, inclusive));
    }

    @Override
    public int offsetAtLineColumn(TextPos2d position) {
        throw new UnsupportedOperationException();
    }

    /**
     * Translate a region given in the coordinate system of this
     * document, to the coordinate system of the base document.
     * This works as if creating a new region with both start and end
     * offsets translated through {@link #inputOffset(int, boolean)}. The
     * returned region may have a different length.
     *
     * @param outputRegion Output region
     *
     * @return Input region
     */
    protected @NonNull TextRegion inputRegion(TextRegion outputRegion) {
        if (outputRegion.isEmpty()) {
            return TextRegion.caretAt(inputOffset(outputRegion.getStartOffset(), true));
        }
        return TextRegion.fromBothOffsets(inputOffset(outputRegion.getStartOffset(), true),
                                          inputOffset(outputRegion.getEndOffset(), false));
    }

    /**
     * Returns the input offset for the given output offset. This maps
     * back an offset in the coordinate system of this document, to the
     * coordinate system of the base document. This includes the
     * length of any unicode escapes.
     *
     * <pre>
     * input:      "a\u00a0b"   (original document)
     * translated: "a b"        (this document)
     *
     * translateOffset(0) = 0
     * translateOffset(1) = 1
     * translateOffset(2) = 7 // includes the length of the escape
     * </pre>
     *
     * @param outOffset Output offset
     * @param inclusive Whether the offset is to be interpreted as the index of a character (true),
     *                  or the position after a character (false)
     *
     * @return Input offset
     */
    protected final int inputOffset(int outOffset, boolean inclusive) {
        if (outOffset < 0 || outOffset > getLength()) {
            throw new IndexOutOfBoundsException();
        }
        return localOffsetTransform(outOffset, inclusive);
    }

    /**
     * Output offset to input offset.
     */
    protected abstract int localOffsetTransform(int outOffset, boolean inclusive);


    @Override
    public void close() throws IOException {
        base.close();
    }
}
