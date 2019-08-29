/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.sourceforge.pmd.document.TextRegion.RegionByOffset;

public class DocumentOperationsApplierForNonOverlappingRegions {

    private final MutableDocument document;
    private final List<DocumentOperation> operations;

    private boolean applied;

    public DocumentOperationsApplierForNonOverlappingRegions(final MutableDocument document) {
        this.document = Objects.requireNonNull(document);
        operations = new ArrayList<>();
        applied = false;
    }

    public void addDocumentOperation(DocumentOperation documentOperation) {
        assertOperationsHaveNotBeenApplied();

        final int index = getIndexForDocumentOperation(Objects.requireNonNull(documentOperation));
        operations.add(index, documentOperation);
    }

    private void assertOperationsHaveNotBeenApplied() {
        if (applied) {
            throw new IllegalStateException("Document operations have already been applied to the document");
        }
    }

    private int getIndexForDocumentOperation(final DocumentOperation documentOperation) {
        int potentialIndex = Collections.binarySearch(operations, documentOperation, this::compareOps);

        if (potentialIndex < 0) {
            return ~potentialIndex;
        }

        final int lastIndex = operations.size() - 1;
        while (potentialIndex < lastIndex && areSiblingsEqual(potentialIndex)) {
            potentialIndex++;
        }
        return potentialIndex + 1;
    }

    private boolean areSiblingsEqual(final int index) {
        return compareOps(operations.get(index), operations.get(index + 1)) == 0;
    }

    public void apply() {
        assertOperationsHaveNotBeenApplied();
        applied = true;

        for (final DocumentOperation operation : operations) {
            operation.apply(document);
        }
    }

    private int compareOps(final DocumentOperation o1, final DocumentOperation o2) {
        final RegionByOffset r1 = Objects.requireNonNull(o1).getRegion().toOffset(document);
        final RegionByOffset r2 = Objects.requireNonNull(o2).getRegion().toOffset(document);

        final int comparison;
        if (r1.getOffset() == r2.getOffset() && r2.getLength() == r1.getLength() && r1.getLength() == 0) {
            comparison = 0;
        } else if (r1.getOffsetAfterEnding() <= r2.getOffset()) {
            comparison = -1;
        } else if (r2.getOffsetAfterEnding() <= r1.getOffset()) {
            comparison = 1;
        } else {
            throw new IllegalArgumentException(
                "Regions between document operations overlap, " + r1.toString() + "\n" + r2.toString());
        }
        return comparison;
    }
}
