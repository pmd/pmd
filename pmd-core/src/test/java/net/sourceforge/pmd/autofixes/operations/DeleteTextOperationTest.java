/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.autofixes.Document;

/**
 * Tests for delete operations over a document
 */
public final class DeleteTextOperationTest {

    private TextOperation textOperation;
    private Document document;

    @Test
    public void deleteTextOfLengthZeroShouldNotChangeDocument() {
        final String expectedString = "public static void main() {}";
        document = new DocumentImp(expectedString);
        textOperation = new DeleteTextOperation(0, 0);

        textOperation.applyTextOperationTreeToDocument(document);

        Assert.assertEquals(expectedString, document.getAsString());
    }

    @Test
    public void deleteTextOfDocumentLengthShouldLeaveDocumentEmpty() {
        final String initialString = "public static void main() {}";
        final String expectedStringAfterOperation = "";
        document = new DocumentImp(initialString);
        textOperation = new DeleteTextOperation(0, document.getLength());

        textOperation.applyTextOperationTreeToDocument(document);

        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }

    @Test
    public void deletePartialTextOfDocumentShouldPreserveRestOfDocument() {
        final String initialString = "public static void main() {}";
        final String textToDelete = "public ";
        final String expectedStringAfterOperation = "static void main() {}";
        document = new DocumentImp(initialString);
        textOperation = new DeleteTextOperation(0, textToDelete.length());

        textOperation.applyTextOperationTreeToDocument(document);

        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }
}
