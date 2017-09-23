/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import net.sourceforge.pmd.autofixes.Document;

/**
 * This text operation type is intended to be the root of every text operation applied to a document, marking the total
 * region of text operations and will apply all the text operations of its children over the document, but this instance
 * will not apply any operation over the document.
 */
public class RootTextOperation extends TextOperation {

    /**
     * Creates a new PlaceHolderTextOperation
     * @param length the length of the document
     */
    public RootTextOperation(final int length) {
        super(0, length);
    }

    @Override
    int applyTextOperationToDocument(final Document document) {
        return 0;
    }
}
