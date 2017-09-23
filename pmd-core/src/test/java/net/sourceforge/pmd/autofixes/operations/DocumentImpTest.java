/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofixes.operations;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.autofixes.Document;

/**
 * Tests for different type of creations of a document
 */
public final class DocumentImpTest {

    private Document document;

    @Test
    public void createEmptyDocumentShouldReturnEmptyString() {
        final String expectedString = "";
        document = new DocumentImp();

        Assert.assertEquals(expectedString, document.getAsString());
    }

    @Test
    public void createEmptyDocumentShouldReturnZeroLength() {
        document = new DocumentImp();

        Assert.assertEquals(0, document.getLength());
    }

    @Test
    public void createDocumentWithStringHelloShouldGetHelloAsString() {
        final String expectedString = "hello";
        document = new DocumentImp(expectedString);

        Assert.assertEquals(expectedString, document.getAsString());
    }

    @Test
    public void createDocumentWithStringHelloShouldHaveLengthEqualToHelloLength() {
        final String expectedString = "hello";
        document = new DocumentImp(expectedString);

        Assert.assertEquals(expectedString.length(), document.getLength());
    }
}
