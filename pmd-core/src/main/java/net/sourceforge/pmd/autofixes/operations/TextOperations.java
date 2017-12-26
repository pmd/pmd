/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import net.sourceforge.pmd.autofixes.Document;
import net.sourceforge.pmd.autofixes.TextOperationNoOverlappingRegionsComparator;


/**
 * TextOperations is a placeholder for all the text operations that are to be applied to a document. It may only be
 * used once and only on one specified document.
 */
public class TextOperations {

    private final Document document;
    private final List<TextOperation> textOperations;
    private final Comparator<TextOperation> textOperationComparator;
    private boolean alreadyApplied;

    /**
     * Creates a new TextOperations instance to manipulate a document
     * @param documentToOperateOn the document to execute the operations on
     */
    public TextOperations(final Document documentToOperateOn) {
        document = Objects.requireNonNull(documentToOperateOn, "documentToOperateOn must not be null");
        textOperations = new ArrayList<>();
        textOperationComparator = new TextOperationNoOverlappingRegionsComparator();
        alreadyApplied = false;
    }

    /**
     * Adds a Text Operation whose parent is going to be this instance. The operation may be added iff it
     * satisfies the following requirement:
     * <ul>The textOperation to be added must not have its region overlapped with its siblings
     *
     * @param textOperationToAdd the operation to be added
     * @throws RuntimeException if the operation does not satisfy at least one of the requirements
     */
    public final void addTextOperation(final TextOperation textOperationToAdd) {
        checkThatOperationsHaveNotBeenApplied();
        Objects.requireNonNull(textOperationToAdd, "TextOperationToAdd must not be null");
        final int indexToAddTextOperation = getIndexToAddTextOperation(textOperationToAdd);
        textOperations.add(indexToAddTextOperation, textOperationToAdd);
    }

    private void checkThatOperationsHaveNotBeenApplied() {
        if (alreadyApplied) {
            throw new IllegalStateException("Text Operations have already been applied to Document");
        }
    }

    private int getIndexToAddTextOperation(final TextOperation textOperationToAdd) {
        int potentialIndex = Collections.binarySearch(textOperations, textOperationToAdd, textOperationComparator);

        if (potentialIndex < 0) {   // It is not an insert operation
            return ~potentialIndex;
        }

        final int lastIndex = textOperations.size() - 1;
        // Of all the insert operations at this offset this one will be to the rightmost
        while (potentialIndex < lastIndex && compareDirectSiblings(potentialIndex) == 0) {
            potentialIndex++;
        }

        return potentialIndex + 1;
    }

    private int compareDirectSiblings(final int index) {
        return textOperationComparator.compare(textOperations.get(index), (textOperations.get(index + 1)));
    }

    /**
     * Apply all the previously inserted text operations to the document
     */
    public void applyToDocument() {

        checkThatOperationsHaveNotBeenApplied();
        alreadyApplied = true;

        /*
        Note: the operations are iterated in an inverse manner; if not, after every iteration we should
        update the delta of its siblings to the right;
         */
        for (int i = textOperations.size() - 1; i >= 0; i--) {
            final TextOperation textOperationToApply = textOperations.get(i);
            textOperationToApply.applyTextOperationToDocument(document);
        }
    }
}
