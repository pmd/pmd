/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.sourceforge.pmd.autofixes.Document;

/**
 * Tests for replace operations over a document
 */
public final class ReplaceTextOperationTest {

    @Test
    public void replaceTextAtBeginningOfDocumentShouldSucceed() {
        final String initialString = "public void main() {}";
        Document document = new DocumentImp(initialString);

        final String stringToBeReplaced = "void";
        final String replacementString = "int";
        TextOperation replaceTextOperation = new ReplaceTextOperation(7,
                stringToBeReplaced.length(), replacementString);

        replaceTextOperation.applyTextOperationTreeToDocument(document);
        final String expectedString = "public int main() {}";
        assertEquals(expectedString, document.getAsString());
    }
}
