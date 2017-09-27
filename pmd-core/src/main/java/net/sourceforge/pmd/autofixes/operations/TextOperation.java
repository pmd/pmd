/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.sourceforge.pmd.autofixes.Document;
import net.sourceforge.pmd.autofixes.Region;
import net.sourceforge.pmd.autofixes.RegionImp;


/**
 * A Text Operation is a representation of a generic manipulation of a text. <p>The representation
 * is implemented in a tree manner, in which any textOperation instance may or may not have any Text
 * Operation child</p>
 */
public abstract class TextOperation implements Comparable<TextOperation> {

    private final Region region;
    private final List<TextOperation> children;

    /**
     * Default constructor for a text operation.
     * @param offset the offset from which the operation will start
     * @param length the length of the region
     */
    public TextOperation(final int offset, final int length) {
        region = new RegionImp(offset, length);
        children = new ArrayList<>();
    }

    /**
     * Note: this class has a natural ordering that is inconsistent with equals.*
     * Two text Operations are comparable iff they do not overlap.
     *
     * @return -1 if this textOperation ends before the other textOperation; 1 if this textOperation
     *      starts after the other textOperation ends; 0 if both textOperations start at the same offset
     *      and have length equal to zero (represented as an insert operation)
     * @throws IllegalArgumentException if both textOperations overlap
     */
    @Override
    public final int compareTo(final TextOperation other) {
        Objects.requireNonNull(other, "otherTextOperation must not be null");

        final int comparison;

        // If both operations are an insert operation
        if (getOffset() == other.getOffset() && getLength() == 0 && other.getLength() == 0) {
            comparison = 0;
        } else if (getOffsetAfterEnding() <= other.getOffset()) {
            comparison = -1;
        } else if (other.getOffsetAfterEnding() <= getOffset()) {
            comparison = 1;
        } else {
            throw new IllegalArgumentException("region overlaps with one of its potential siblings");
        }

        return comparison;
    }

    /**
     * Adds a Text Operation whose parent is going to be this instance. The child may be added iff it
     * satisfies the following requirements:
     * <ul>The child to be added must be contained within the region of its potential parent.</ul>
     * <ul>The child to be added must not enter in conflict with their potential siblings. This case
     * may happen if their regions overlap. For more information refer to {@link
     * #compareTo(Object)}</ul>
     *
     * @param childToAdd the child to be added
     * @throws RuntimeException if the child does not satisfy at least one of the requirements
     */
    public final void addChild(final TextOperation childToAdd) {
        Objects.requireNonNull(childToAdd, "ChildToAdd must not be null");

        if (!containsChildRegion(childToAdd)) {
            throw new IllegalArgumentException("ChildToAdd's region must be contained inside its parent");
        }
        final int indexToAddChild = getIndexToAddChild(childToAdd);

        children.add(indexToAddChild, childToAdd);
    }

    private boolean containsChildRegion(final TextOperation childToAdd) {
        return getRegion().contains(childToAdd.getRegion());
    }

    /* package-private */ Region getRegion() {
        return new RegionImp(region);
    }

    private int getIndexToAddChild(final TextOperation childToAdd) {
        int potentialIndex = Collections.binarySearch(children, childToAdd);

        if (potentialIndex < 0) { // It is not an insert operation
            return ~potentialIndex;
        }

        final int childrenLastIndex = children.size() - 1;

        // Of all the insert operations at this offset this one will be to the rightmost
        while (potentialIndex < childrenLastIndex && compareAdjacentChildren(potentialIndex) == 0) {
            potentialIndex++;
        }

        return potentialIndex + 1;
    }

    private int compareAdjacentChildren(final int index) {
        return children.get(index).compareTo(children.get(index + 1));
    }

    private int getOffsetAfterEnding() {
        return region.getOffsetAfterEnding();
    }

    /**
     * Get the offset of the text operation.
     * @return the offset
     */
    protected int getOffset() {
        return region.getOffset();
    }

    /**
     * Get the length of the text operation.
     * @return the length
     */
    protected int getLength() {
        return region.getLength();
    }

    /**
     * Get the number of children this instance has.
     *
     * @return a non-negative value that determines the number of children
     */
    public final int getChildrenSize() {
        return children.size();
    }

    /**
     * Applies the text Operations to a document in an inverse post-order manner (from right to left).
     *
     * @param document the document to apply the text Operations
     */
    public final void applyTextOperationTreeToDocument(final Document document) {
        applyTextOperationTreeToDocumentAndGetDelta(document);
    }

    private int applyTextOperationTreeToDocumentAndGetDelta(final Document document) {
        final int childrenDelta = applyChildTextOperationsToDocument(document);
        getRegion().sumLength(childrenDelta);

        final int ownDelta = applyTextOperationToDocument(document);
        getRegion().sumLength(ownDelta);

        return childrenDelta + ownDelta;
    }

    private int applyChildTextOperationsToDocument(final Document document) {
        int accumulatedDelta = 0;

        /*
        Note: the children are iterated in an inverse manner; if not, after every iteration we should
        update the delta of its siblings to the right;
         */
        for (int indexChild = getChildrenSize() - 1; indexChild >= 0; indexChild--) {
            final TextOperation child = children.get(indexChild);
            accumulatedDelta += child.applyTextOperationTreeToDocumentAndGetDelta(document);
        }

        return accumulatedDelta;
    }

    /* package-private */
    abstract int applyTextOperationToDocument(Document document);
}
