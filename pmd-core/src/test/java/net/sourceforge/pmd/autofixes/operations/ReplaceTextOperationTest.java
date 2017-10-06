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

    private TextOperations textOperations;

    @Test
    public void replaceTextAtBeginningOfDocumentShouldSucceed() {
        final String initialString = "public void main() {}";
        Document document = new DocumentImp(initialString);

        textOperations = new TextOperations(document);
        final String stringToBeReplaced = "void";
        final String replacementString = "int";
        textOperations.addTextOperation(new ReplaceTextOperation(7,
                stringToBeReplaced.length(), replacementString));

        textOperations.applyToDocument();
        final String expectedString = "public int main() {}";
        assertEquals(expectedString, document.getAsString());
    }
}
