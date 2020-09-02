/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

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
    public long getChecksum() {
        return base.getChecksum();
    }

    @Override
    public String getPathId() {
        return base.getPathId();
    }

    @Override
    public String getDisplayName() {
        return base.getDisplayName();
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
    public @NonNull TextRegion inputRegion(TextRegion outputRegion) {
        // note that inputOffset already recurses up to the original document,
        // so that we don't have to call base.inputRegion on the produced region
        return TextRegion.fromBothOffsets(inputOffset(outputRegion.getStartOffset(), true),
                                          inputOffset(outputRegion.getEndOffset(), false));
    }

    @Override
    public TextRegion createLineRange(int startLineInclusive, int endLineInclusive) {
        // see the doc, lines do not need to be translated
        return base.createLineRange(startLineInclusive, endLineInclusive);
    }

    @Override
    public int inputOffset(int outOffset, boolean inclusive) {
        if (outOffset < 0 || outOffset > getLength()) {
            throw new IndexOutOfBoundsException();
        }
        return base.inputOffset(localOffsetTransform(outOffset, inclusive), inclusive);
    }

    protected abstract int localOffsetTransform(int outOffset, boolean inclusive);


    @Override
    public void close() throws IOException {
        base.close();
    }
}
