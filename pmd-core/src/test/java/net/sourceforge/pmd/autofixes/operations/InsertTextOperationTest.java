/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.autofixes.Document;


/**
 * Tests for insert operations over a document
 */
public final class InsertTextOperationTest {

    private TextOperation insertTextOperation;
    private Document document;

    @Test
    public void addTextAtBeginningOfDocumentShouldSucceed() {
        final String initialString = "static void main() {}";
        document = new DocumentImp(initialString);

        final String stringToInsert = "public ";
        insertTextOperation = new InsertTextOperation(0, stringToInsert);

        insertTextOperation.applyTextOperationTreeToDocument(document);
        final String expectedString = "public static void main() {}";
        assertEquals(expectedString, document.getAsString());
    }

    @Test
    public void addTextAtInRangePositionOfDocumentShouldSucceed() {
        final String initialString = "public static void main(String[] args) {}";
        document = new DocumentImp(initialString);

        final String stringToInsert = "final ";
        insertTextOperation = new InsertTextOperation(24, stringToInsert);

        insertTextOperation.applyTextOperationTreeToDocument(document);
        final String expectedString = "public static void main(final String[] args) {}";
        assertEquals(expectedString, document.getAsString());
    }

    @Test
    public void addTextAtEndOfDocumentShouldSucceed() {
        final String initialString = "public static void main()";
        document = new DocumentImp(initialString);

        final String stringToInsert = " {}";
        insertTextOperation = new InsertTextOperation(document.getLength(),
                stringToInsert);

        insertTextOperation.applyTextOperationTreeToDocument(document);
        final String expectedString = "public static void main() {}";
        assertEquals(expectedString, document.getAsString());
    }
}
