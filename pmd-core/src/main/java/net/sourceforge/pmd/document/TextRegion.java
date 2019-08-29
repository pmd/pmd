/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import java.util.Comparator;

import org.checkerframework.checker.nullness.qual.NonNull;

/** A generic range of text in a document. */
public interface TextRegion {

    // TODO should we have a single interface, and bind regions to their document?

    /**
     * Returns a view of this region as an (offset,length) 2-tuple.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in the document
     */
    RegionByOffset toOffset(Document document);


    /**
     * Returns a view of this region as a (begin,end)x(line,column) 4-tuple.
     *
     * @throws IndexOutOfBoundsException If the argument does not identify a valid region in the document
     */
    RegionByLine toLineColumn(Document document);


    /**
     * Represents a region in a {@link Document} with the tuple (beginLine, endLine, beginColumn, endColumn).
     *
     * <p>Lines and columns in PMD are 1-based.
     */
    interface RegionByLine extends TextRegion, Comparable<RegionByLine> {

        Comparator<RegionByLine> COMPARATOR = Comparator.comparingInt(RegionByLine::getBeginLine)
                                                        .thenComparingInt(RegionByLine::getBeginColumn)
                                                        .thenComparingInt(RegionByLine::getEndLine)
                                                        .thenComparingInt(RegionByLine::getEndColumn);


        /** 1-based, inclusive index. */
        int getBeginLine();


        /** 1-based, inclusive index. */
        int getEndLine();


        /** 1-based, inclusive index. */
        int getBeginColumn();


        /** 1-based, inclusive index. */
        int getEndColumn();


        @Override
        default int compareTo(@NonNull RegionByLine o) {
            return COMPARATOR.compare(this, o);
        }


        @Override
        default RegionByLine toLineColumn(Document document) {
            return this;
        }


        @Override
        default RegionByOffset toOffset(Document document) {
            return document.mapToOffset(this);
        }
    }

    /**
     * Represents a region in a {@link Document} with the tuple (offset, length).
     */
    interface RegionByOffset extends TextRegion, Comparable<RegionByOffset> {

        Comparator<RegionByOffset> COMPARATOR = Comparator.comparingInt(RegionByOffset::getStartOffset)
                                                          .thenComparingInt(RegionByOffset::getLength);


        /** 0-based, inclusive index. */
        int getStartOffset();


        /** Length of the region. */
        int getLength();


        /** 0-based, exclusive index. */
        default int getOffsetAfterEnding() {
            return getStartOffset() + getLength();
        }


        @Override
        default RegionByLine toLineColumn(Document document) {
            return document.mapToLine(this);
        }


        @Override
        default RegionByOffset toOffset(Document document) {
            return this;
        }


        @Override
        default int compareTo(@NonNull RegionByOffset o) {
            return COMPARATOR.compare(this, o);
        }
    }
}
