/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import java.util.Objects;

import net.sourceforge.pmd.autofixes.Document;

/**
 * Representation of an insert operation of a document in a given region.
 */
public class InsertTextOperation extends TextOperation {

    private final String textToInsert;

    /**
     * Creates a new insert operation.
     * @param offset the offset from which the operation will start
     * @param textToInsert the text to insert from the given offset
     */
    public InsertTextOperation(final int offset, final String textToInsert) {
        super(offset, 0);

        Objects.requireNonNull(textToInsert, "textToInsert must not be null");
        this.textToInsert = textToInsert;
    }

    @Override
    int applyTextOperationToDocument(final Document document) {
        document.insert(getRegion().getOffset(), textToInsert);

        return textToInsert.length();
    }
}
