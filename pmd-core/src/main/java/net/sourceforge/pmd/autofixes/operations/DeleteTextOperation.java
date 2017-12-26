/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import java.util.Objects;

import net.sourceforge.pmd.autofixes.Document;

/**
 * Representation of a delete operation of a document in a given region.
 */
public class DeleteTextOperation extends TextOperation {
    /**
     * Creates a new delete operation.
     * @param offset the offset from which the operation will start deleting
     * @param length the length of the desired deletion
     */
    public DeleteTextOperation(final int offset, final int length) {
        super(offset, length);
    }

    @Override
    int applyTextOperationToDocument(final Document document) {
        Objects.requireNonNull(document, "Document must not be null");

        document.delete(getRegion());

        return -(getRegion().getLength());
    }
}
