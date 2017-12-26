/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes;

import java.util.Comparator;
import java.util.Objects;

import net.sourceforge.pmd.autofixes.operations.TextOperation;

/**
 *  Establish a non-total order relationship in which two text operations may be ordered iff their regions do not
 *  overlap.
 *
 * @return -1 if this textOperation ends before the other textOperation; 1 if this textOperation
 *      starts after the other textOperation ends; 0 if both textOperations start at the same offset
 *      and have length equal to zero (represented as an insert operation)
 * @throws IllegalArgumentException if both textOperations overlap
 */
public class TextOperationNoOverlappingRegionsComparator implements Comparator<TextOperation> {

    @Override
    public final int compare(final TextOperation textOperation1, final TextOperation textOperation2) {
        Objects.requireNonNull(textOperation1, "textOperation1 must not be null");
        Objects.requireNonNull(textOperation2, "textOperation2 must not be null");

        final int comparison;

        // If both operations are an insert operation
        if (textOperation1.getOffset() == textOperation2.getOffset()
                && textOperation1.getLength() == 0 && textOperation2.getLength() == 0) {
            comparison = 0;
        } else if (textOperation1.getOffsetAfterEnding() <= textOperation2.getOffset()) {
            comparison = -1;
        } else if (textOperation2.getOffsetAfterEnding() <= textOperation1.getOffset()) {
            comparison = 1;
        } else {
            throw new IllegalArgumentException("There exists a region overlap between these text operations");
        }

        return comparison;
    }
}
