/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.autofixes.Document;

/**
 * Tests for different operations over a document
 */
public class MixedTextOperationTest {

    private TextOperations textOperations;
    private Document document;

    @Test
    public void sequentialSiblingDeleteTextOfDocumentShouldSucceed() {
        final String initialString = "public static void main() {}";
        final String text1ToDelete = "public ";
        final String text2ToDelete = "static ";
        final String expectedStringAfterOperation = "void main() {}";

        document = new DocumentImp(initialString);

        textOperations = new TextOperations(document);
        textOperations.addTextOperation(new DeleteTextOperation(0, text1ToDelete.length()));
        textOperations.addTextOperation(new DeleteTextOperation(7, text2ToDelete.length()));

        textOperations.applyToDocument();

        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }

    @Test
    public void twoSideSiblingDeleteTextOfDocumentShouldSucceed() {
        final String initialString = "public static void main() {}";
        final String text1ToDelete = "public ";
        final String text2ToDelete = " {}";
        final String expectedStringAfterOperation = "static void main()";

        document = new DocumentImp(initialString);
        textOperations = new TextOperations(document);
        textOperations.addTextOperation(new DeleteTextOperation(0, text1ToDelete.length()));
        textOperations.addTextOperation(new DeleteTextOperation(25, text2ToDelete.length()));

        textOperations.applyToDocument();

        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }

    @Test
    public void deleteReplaceAndInsertTextInAnyOrderShouldSucceed() {
        final String initialString = "public static int main(String[] args, int dummy) {}";
        document = new DocumentImp(initialString);

        textOperations = new TextOperations(document);
        final String textToDelete = ", int dummy";
        textOperations.addTextOperation(new DeleteTextOperation(36, textToDelete.length()));
        final String textToBeReplaced = "int";
        final String replacementText = "void";
        textOperations.addTextOperation(new ReplaceTextOperation(14, textToBeReplaced.length(), replacementText));
        final String textToInsert = "final ";
        textOperations.addTextOperation(new InsertTextOperation(23, textToInsert));

        textOperations.applyToDocument();
        final String expectedStringAfterOperation = "public static void main(final String[] args) {}";
        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }
}
