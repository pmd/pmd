/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import net.sourceforge.pmd.autofixes.Document;

/**
 * This text operation type is intended to mark the total region of text operations and will apply
 * all the text operations of its children over the document, but this instance will not apply any
 * operation over the document.
 */
public class PlaceholderTextOperation extends TextOperation {

    /**
     * Creates a new PlaceHolderTextOperation
     * @param offset
     * @param length
     */
    public PlaceholderTextOperation(final int offset, final int length) {
        super(offset, length);
    }

    @Override
    int applyTextOperationToDocument(final Document document) {
        return 0;
    }
}
