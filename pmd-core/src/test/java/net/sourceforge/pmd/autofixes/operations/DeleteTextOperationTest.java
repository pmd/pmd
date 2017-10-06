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

    private TextOperations textOperations;
    private Document document;

    @Test
    public void deleteTextOfLengthZeroShouldNotChangeDocument() {
        final String expectedString = "public static void main() {}";
        document = new DocumentImp(expectedString);

        textOperations = new TextOperations(document);
        textOperations.addTextOperation(new DeleteTextOperation(0, 0));

        textOperations.applyToDocument();
        Assert.assertEquals(expectedString, document.getAsString());
    }

    @Test
    public void deleteTextOfDocumentLengthShouldLeaveDocumentEmpty() {
        final String initialString = "public static void main() {}";
        final String expectedStringAfterOperation = "";
        document = new DocumentImp(initialString);

        textOperations = new TextOperations(document);
        textOperations.addTextOperation(new DeleteTextOperation(0, document.getLength()));

        textOperations.applyToDocument();
        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }

    @Test
    public void deletePartialTextOfDocumentShouldPreserveRestOfDocument() {
        final String initialString = "public static void main() {}";
        final String textToDelete = "public ";
        final String expectedStringAfterOperation = "static void main() {}";
        document = new DocumentImp(initialString);

        textOperations = new TextOperations(document);
        textOperations.addTextOperation(new DeleteTextOperation(0, textToDelete.length()));

        textOperations.applyToDocument();
        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }
}
