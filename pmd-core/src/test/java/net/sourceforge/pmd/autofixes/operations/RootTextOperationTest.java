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
public class RootTextOperationTest {

    private TextOperation textOperation;
    private Document document;

    @Test
    public void sequentialSiblingDeleteTextOfDocumentShouldSucceed() {
        final String initialString = "public static void main() {}";
        final String text1ToDelete = "public ";
        final String text2ToDelete = "static ";
        final String expectedStringAfterOperation = "void main() {}";

        document = new DocumentImp(initialString);
        textOperation = new RootTextOperation(initialString.length());
        textOperation.addChild(new DeleteTextOperation(0, text1ToDelete.length()));
        textOperation.addChild(new DeleteTextOperation(7, text2ToDelete.length()));

        textOperation.applyTextOperationTreeToDocument(document);

        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }

    @Test
    public void twoSideSiblingDeleteTextOfDocumentShouldSucceed() {
        final String initialString = "public static void main() {}";
        final String text1ToDelete = "public ";
        final String text2ToDelete = " {}";
        final String expectedStringAfterOperation = "static void main()";

        document = new DocumentImp(initialString);
        textOperation = new RootTextOperation(initialString.length());
        textOperation.addChild(new DeleteTextOperation(0, text1ToDelete.length()));
        textOperation.addChild(new DeleteTextOperation(25, text2ToDelete.length()));

        textOperation.applyTextOperationTreeToDocument(document);

        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }

    @Test
    public void deleteReplaceAndInsertTextInAnyOrderShouldSucceed() {
        final String initialString = "public static int main(String[] args, int dummy) {}";
        document = new DocumentImp(initialString);

        textOperation = new RootTextOperation(initialString.length());
        final String textToDelete = ", int dummy";
        textOperation.addChild(new DeleteTextOperation(36, textToDelete.length()));
        final String textToBeReplaced = "int";
        final String replacementText = "void";
        textOperation
                .addChild(new ReplaceTextOperation(14, textToBeReplaced.length(), replacementText));
        final String textToInsert = "final ";
        textOperation.addChild(new InsertTextOperation(23, textToInsert));

        textOperation.applyTextOperationTreeToDocument(document);
        final String expectedStringAfterOperation = "public static void main(final String[] args) {}";
        Assert.assertEquals(expectedStringAfterOperation, document.getAsString());
    }
}
