/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.Comparator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A contiguous range of text in a {@link TextDocument}. See {@link TextDocument#createRegion(int, int)}
 * for a description of valid regions in a document. Empty regions may
 * be thought of as caret positions in an IDE. An empty region at offset
 * {@code n} does not contain the character at offset {@code n} in the
 * document, but if it were a caret, typing a character {@code c} would
 * make {@code c} the character at offset {@code n} in the document.
 *
 * <p>Line and column information may be added by {@link TextDocument#toLocation(TextRegion)}.
 *
 * <p>Regions are not bound to a specific document, keeping a reference
 * to them does not prevent the document from being garbage-collected.
 */
public interface TextRegion extends Comparable<TextRegion> {

    TextRegion UNDEFINED = TextRegionImpl.fromOffsetLength(0, 0);

    /** Compares the start offset, then the length of a region. */
    Comparator<TextRegion> COMPARATOR = Comparator.comparingInt(TextRegion::getStartOffset)
                                                  .thenComparingInt(TextRegion::getLength);


    /** 0-based, inclusive index. */
    int getStartOffset();


    /** 0-based, exclusive index. */
    int getEndOffset();


    /**
     * Returns the length of the region in characters. This is the difference
     * between start offset and end offset. All characters have length 1,
     * including {@code '\t'}. The sequence {@code "\r\n"} has length 2 and
     * not 1.
     */
    int getLength();


    /**
     * Returns true if the region contains no characters. In that case
     * it can be viewed as a caret position, and e.g. used for text insertion.
     */
    boolean isEmpty();


    /**
     * Returns true if this region contains the character at the given
     * offset. Note that a region with length zero does not even contain
     * the character at its start offset.
     *
     * @param offset Offset of a character
     */
    default boolean containsOffset(int offset) {
        return getStartOffset() <= offset && offset < getEndOffset();
    }


    /**
     * Returns true if this region contains the entirety of the other
     * region. Any region contains itself.
     *
     * @param other Other region
     */
    default boolean contains(TextRegion other) {
        return this.getStartOffset() <= other.getStartOffset()
            && other.getEndOffset() <= this.getEndOffset();
    }


    /**
     * Returns true if this region overlaps the other region by at
     * least one character. This is a symmetric, reflexive relation.
     *
     * @param other Other region
     */
    default boolean overlaps(TextRegion other) {
        TextRegion intersection = TextRegion.intersect(this, other);
        return intersection != null && intersection.getLength() != 0;
    }


    /**
     * Computes the intersection of this region with the other. This is the
     * largest region that this region and the parameter both contain.
     * It may have length zero, or not exist (if the regions are completely
     * disjoint).
     *
     * @param r1 A region
     * @param r2 A region
     *
     * @return The intersection, if it exists
     */
    @Nullable
    static TextRegion intersect(TextRegion r1, TextRegion r2) {
        int start = Math.max(r1.getStartOffset(), r2.getStartOffset());
        int end = Math.min(r1.getEndOffset(), r2.getEndOffset());

        return start <= end ? fromBothOffsets(start, end)
                            : null;

    }


    /**
     * Computes the union of this region with the other. This is the
     * smallest region that contains both this region and the parameter.
     *
     * @param r1 A region
     * @param r2 A region
     *
     * @return The union of both regions
     */
    static TextRegion union(TextRegion r1, TextRegion r2) {
        if (r1 == r2) {
            return r1;
        }

        int start = Math.min(r1.getStartOffset(), r2.getStartOffset());
        int end = Math.max(r1.getEndOffset(), r2.getEndOffset());

        return fromBothOffsets(start, end);
    }


    /**
     * Builds a new region from offset and length.
     */
    static TextRegion fromOffsetLength(int startOffset, int length) {
        return TextRegionImpl.fromOffsetLength(startOffset, length);
    }


    /**
     * Builds a new region from start and end offset.
     */
    static TextRegion fromBothOffsets(int startOffset, int endOffset) {
        return TextRegionImpl.fromBothOffsets(startOffset, endOffset);
    }


    /**
     * Ordering on text regions is defined by the {@link #COMPARATOR}.
     */
    @Override
    default int compareTo(@NonNull TextRegion o) {
        return COMPARATOR.compare(this, o);
    }


    /**
     * Returns true if the other object is a text region with the same
     * start offset and end offset as this one.
     *
     * @param o {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Override
    boolean equals(Object o);


}
