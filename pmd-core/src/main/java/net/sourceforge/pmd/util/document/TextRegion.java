/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.Comparator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a range of text in a {@link TextDocument} with the tuple (offset, length).
 *
 * <p>Line and column information may be added when the {@link TextDocument} is known.
 */
public interface TextRegion extends Comparable<TextRegion> {

    /** Compares the start offset, then the length of a region. */
    Comparator<TextRegion> COMPARATOR = Comparator.comparingInt(TextRegion::getStartOffset)
                                                  .thenComparingInt(TextRegion::getLength);


    /** 0-based, inclusive index. */
    int getStartOffset();


    /** 0-based, exclusive index. */
    int getEndOffset();


    /** Length of the region. */
    int getLength();


    /**
     * Returns true if this region overlaps with the other region by at
     * least one character. This is a symmetric relation.
     *
     * @param other Other region
     */
    default boolean overlaps(TextRegion other) {
        TextRegion intersection = this.intersect(other);
        return intersection != null && intersection.getLength() > 0;
    }


    /**
     * Compute the intersection of this region with the other. Returns
     * null if the two regions are disjoint.
     *
     * @param other Other region
     */
    @Nullable
    default TextRegion intersect(TextRegion other) {
        if (this.getStartOffset() < other.getStartOffset()) {
            int len = this.getEndOffset() - other.getStartOffset();
            return len < 0 ? null : new TextRegionImpl(other.getStartOffset(), len);
        } else if (other.getStartOffset() < this.getStartOffset()) {
            int len = other.getEndOffset() - this.getStartOffset();
            return len < 0 ? null : new TextRegionImpl(this.getStartOffset(), len);
        } else {
            return new TextRegionImpl(this.getStartOffset(), Math.min(this.getLength(), other.getLength()));
        }
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
