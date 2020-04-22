/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.Comparator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A contiguous range of text in a {@link TextDocument}. Empty regions may
 * be thought of like caret positions in an IDE. An empty region at offset
 * {@code n} does not contain the character at offset {@code n} in the
 * document, but if it were a caret, typing a character {@code c} would
 * make {@code c} the character at offset {@code n} in the document.
 *
 * <p>Line and column information may be added by {@link TextDocument#toLocation(TextRegion)}.
 *
 * <p>Regions are not bound to a specific document, keeping a reference
 * to them does not prevent the document from being garbage-collected.
 *
 * <p>Regions are represented as a simple offset+length tuple. Valid
 * start offsets range from 0 to {@link #getLength()} (inclusive).
 * The sum {@code startOffset + length} must range from {@code startOffset}
 * to {@link #getLength()} (inclusive).
 *
 * <p>Those rules make the region starting at {@link #getLength()}
 * with length 0 a valid region (the caret position at the end of the document).
 *
 * <p>For example, for a document of length 1 ({@code "c"}), there
 * are only three valid regions:
 * <pre>{@code
 * [[c     : caret position at offset 0 (empty region)
 *  [c[    : range containing the character
 *   c[[   : caret position at offset 1 (empty region)
 * }</pre>
 */
public final class TextRegion implements Comparable<TextRegion> {

    private static final Comparator<TextRegion> COMPARATOR =
        Comparator.comparingInt(TextRegion::getStartOffset)
                  .thenComparingInt(TextRegion::getLength);

    private final int startOffset;
    private final int length;

    private TextRegion(int startOffset, int length) {
        this.startOffset = startOffset;
        this.length = length;

        assert startOffset >= 0 && length >= 0 : "Invalid region " + this;
    }

    /** 0-based, inclusive index. */
    public int getStartOffset() {
        return startOffset;
    }

    /** 0-based, exclusive index. */
    public int getEndOffset() {
        return startOffset + length;
    }

    /**
     * Returns the length of the region in characters. This is the difference
     * between start offset and end offset. All characters have length 1,
     * including {@code '\t'}. The sequence {@code "\r\n"} has length 2 and
     * not 1.
     */
    public int getLength() {
        return length;
    }

    /**
     * Returns true if the region contains no characters. In that case
     * it can be viewed as a caret position, and e.g. used for text insertion.
     */
    public boolean isEmpty() {
        return length == 0;
    }

    /**
     * Returns true if this region contains the character at the given
     * offset. Note that a region with length zero does not even contain
     * the character at its start offset.
     *
     * @param offset Offset of a character
     */
    public boolean containsOffset(int offset) {
        return getStartOffset() <= offset && offset < getEndOffset();
    }

    /**
     * Returns true if this region contains the entirety of the other
     * region. Any region contains itself.
     *
     * @param other Other region
     */
    public boolean contains(TextRegion other) {
        return this.getStartOffset() <= other.getStartOffset()
            && other.getEndOffset() <= this.getEndOffset();
    }

    /**
     * Returns true if this region overlaps the other region by at
     * least one character. This is a symmetric, reflexive relation.
     *
     * @param other Other region
     */
    public boolean overlaps(TextRegion other) {
        TextRegion intersection = TextRegion.intersect(this, other);
        return intersection != null && intersection.getLength() != 0;
    }

    /**
     * Returns a region that ends at the same point, but starts 'delta'
     * characters before this region. If the delta is negative, then this
     * shifts the start of the region to the right (but the end stays fixed).
     */
    public TextRegion growLeft(int delta) {
        return new TextRegion(startOffset - delta, delta + length);
    }

    /**
     * Returns a region that starts at the same point, but ends 'delta'
     * characters after this region. If the delta is negative, then this
     * shifts the end of the region to the left (but the start stays fixed).
     */
    public TextRegion growRight(int delta) {
        return new TextRegion(startOffset, delta + length);
    }

    /**
     * Computes the intersection of this region with the other. This is the
     * largest region that this region and the parameter both contain.
     * It may have length zero, or not exist (if the regions are completely
     * disjoint).
     *
     * @return The intersection, if it exists
     */
    @Nullable
    public static TextRegion intersect(TextRegion r1, TextRegion r2) {
        int start = Math.max(r1.getStartOffset(), r2.getStartOffset());
        int end = Math.min(r1.getEndOffset(), r2.getEndOffset());

        return start <= end ? fromBothOffsets(start, end)
                            : null;
    }

    /**
     * Computes the union of this region with the other. This is the
     * smallest region that contains both this region and the parameter.
     *
     * @return The union of both regions
     */
    public static TextRegion union(TextRegion r1, TextRegion r2) {
        if (r1.equals(r2)) {
            return r1;
        }

        int start = Math.min(r1.getStartOffset(), r2.getStartOffset());
        int end = Math.max(r1.getEndOffset(), r2.getEndOffset());

        return fromBothOffsets(start, end);
    }

    /**
     * Builds a new region from offset and length.
     *
     * @throws AssertionError If either parameter is negative
     */
    public static TextRegion fromOffsetLength(int startOffset, int length) {
        return new TextRegion(startOffset, length);
    }

    /**
     * Builds a new region from start and end offset.
     *
     * @param startOffset Start offset
     * @param endOffset   End offset
     *
     * @throws AssertionError If either offset is negative, or the two
     *                        offsets are not ordered
     */
    public static TextRegion fromBothOffsets(int startOffset, int endOffset) {
        return new TextRegion(startOffset, endOffset - startOffset);
    }

    /**
     * Checks that the parameters are a valid region, this is provided
     * to debug, will be a noop unless assertions are enabled.
     */
    public static boolean isValidRegion(int startOffset, int endOffset, TextDocument doc) {
        assert startOffset >= 0 : "Negative start offset: " + startOffset;
        assert endOffset >= 0 : "Negative end offset: " + endOffset;
        assert startOffset <= endOffset : "Start and end offset are not ordered: " + startOffset + " > " + endOffset;
        assert endOffset <= doc.getLength() : "End offset " + endOffset + " out of range for doc of length " + doc.getLength();
        return true;
    }

    /** Compares the start offset, then the length of a region. */
    @Override
    public int compareTo(@NonNull TextRegion o) {
        return COMPARATOR.compare(this, o);
    }

    @Override
    public String toString() {
        return "Region(start=" + startOffset + ", len=" + length + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TextRegion)) {
            return false;
        }
        TextRegion that = (TextRegion) o;
        return startOffset == that.getStartOffset()
            && length == that.getLength();
    }

    @Override
    public int hashCode() {
        return startOffset * 31 + length;
    }
}
