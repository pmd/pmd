/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.Comparator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A contiguous range of text in a {@link TextDocument}. See {@link TextDocument#createRegion(int, int)}
 * for a description of valid regions in a document.
 *
 * <p>Line and column information may be added when the {@link TextDocument} is known.
 * See {@link TextDocument#addLineInfo(TextRegion)}.
 *
 * <p>Regions are not bound to a specific document, keeping a reference
 * to them does not prevent the document from being garbage-collected.
 */
public interface TextRegion extends Comparable<TextRegion> {

    /** Compares the start offset, then the length of a region. */
    Comparator<TextRegion> COMPARATOR = Comparator.comparingInt(TextRegion::getStartOffset)
                                                  .thenComparingInt(TextRegion::getLength);


    /** 0-based, inclusive index. */
    int getStartOffset();


    /** 0-based, exclusive index. */
    int getEndOffset();


    /**
     * Returns the length of the region in characters. All characters
     * have length 1, including {@code '\t'}. The sequence {@code "\r\n"}
     * has length 2.
     */
    int getLength();


    /**
     * Returns true if the region contains no characters. In that case
     * it can be viewed as a caret position, eg used for text insertion.
     */
    default boolean isEmpty() {
        return getLength() == 0;
    }


    /**
     * Returns true if this region overlaps with the other region by at
     * least one character. This is a symmetric, reflexive relation.
     *
     * @param other Other region
     */
    default boolean overlaps(TextRegion other) {
        TextRegion intersection = this.intersect(other);
        return intersection != null && !intersection.isEmpty();
    }


    /**
     * Computes the intersection of this region with the other. It may
     * have length zero. Returns null if the two regions are completely
     * disjoint. For all regions {@code R}, {@code S}:
     *
     * <pre>
     *  R intersect R == R
     *  R intersect S == S intersect R
     * </pre>
     *
     * @param other Other region
     *
     * @return The intersection, if it exists
     */
    @Nullable
    default TextRegion intersect(TextRegion other) {
        int start = Math.max(this.getStartOffset(), other.getStartOffset());
        int end = Math.min(this.getEndOffset(), other.getEndOffset());

        return start <= end ? TextRegionImpl.fromBothOffsets(start, end)
                            : null;

    }


    /**
     * Ordering on text regions is defined by the {@link #COMPARATOR}.
     */
    @Override
    default int compareTo(@NonNull TextRegion o) {
        return COMPARATOR.compare(this, o);
    }


    /**
     * Adds line information to a text region.
     *
     * <p>Lines and columns in PMD are 1-based.
     */
    interface RegionWithLines extends TextRegion {


        /** 1-based, inclusive index. */
        int getBeginLine();


        /** 1-based, inclusive index. */
        int getEndLine();


        /** 1-based, inclusive index. */
        int getBeginColumn();


        /** 1-based, <b>exclusive</b> index. */
        int getEndColumn();
    }

}
