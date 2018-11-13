/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DocumentOperationsApplierForNonOverlappingRegions {

    private static final Comparator<DocumentOperation> COMPARATOR = new DocumentOperationNonOverlappingRegionsComparator();

    private final Document document;
    private final List<DocumentOperation> operations;

    private boolean applied;

    public DocumentOperationsApplierForNonOverlappingRegions(final Document document) {
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
        int potentialIndex = Collections.binarySearch(operations, documentOperation, COMPARATOR);

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
        return COMPARATOR.compare(operations.get(index), operations.get(index + 1)) == 0;
    }

    public void apply() {
        assertOperationsHaveNotBeenApplied();
        applied = true;

        for (final DocumentOperation operation : operations) {
            operation.apply(document);
        }
    }

    private static class DocumentOperationNonOverlappingRegionsComparator implements Comparator<DocumentOperation> {

        @Override
        public int compare(final DocumentOperation o1, final DocumentOperation o2) {
            final RegionByLine r1 = Objects.requireNonNull(o1).getRegionByLine();
            final RegionByLine r2 = Objects.requireNonNull(o2).getRegionByLine();

            final int comparison;
            if (operationsStartAtTheSameOffsetAndHaveZeroLength(r1, r2)) {
                comparison = 0;
            } else if (doesFirstRegionEndBeforeSecondRegionBegins(r1, r2)) {
                comparison = -1;
            } else if (doesFirstRegionEndBeforeSecondRegionBegins(r2, r1)) {
                comparison = 1;
            } else {
                throw new IllegalArgumentException("Regions between document operations overlap, " + r1.toString() + "\n" + r2.toString());
            }
            return comparison;
        }

        private boolean operationsStartAtTheSameOffsetAndHaveZeroLength(final RegionByLine r1, final RegionByLine r2) {
            return r1.getBeginLine() == r2.getBeginLine() && r1.getBeginColumn() == r2.getBeginColumn()
                    && r1.getBeginLine() == r1.getEndLine() && r1.getBeginColumn() == r1.getEndColumn();
        }

        private boolean doesFirstRegionEndBeforeSecondRegionBegins(final RegionByLine r1, final RegionByLine r2) {
            if (r1.getEndLine() < r2.getBeginLine()) {
                return true;
            } else if (r1.getEndLine() == r2.getBeginLine()) {
                return r1.getEndColumn() <= r2.getBeginColumn();
            }
            return false;
        }
    }
}
