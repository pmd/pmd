/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

public class TextDocumentTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();
    private final LanguageVersion dummyVersion = LanguageRegistry.getDefaultLanguage().getDefaultVersion();

    @Test
    public void testSingleLineRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);

        TextRegion region = TextRegion.fromOffsetLength(0, "bonjour".length());

        assertEquals(0, region.getStartOffset());
        assertEquals("bonjour".length(), region.getLength());
        assertEquals("bonjour".length(), region.getEndOffset());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1, withLines.getBeginColumn());
        assertEquals(1 + "bonjour".length(), withLines.getEndColumn());
        assertEquals("bonjour".length(), withLines.getEndColumn() - withLines.getBeginColumn());
    }

    @Test
    public void testMultiLineRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion);

        TextRegion region = TextRegion.fromOffsetLength("bonjou".length(), "r\noha\ntri".length());

        assertEquals("bonjou".length(), region.getStartOffset());
        assertEquals("r\noha\ntri".length(), region.getLength());
        assertEquals("bonjour\noha\ntri".length(), region.getEndOffset());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(3, withLines.getEndLine());
        assertEquals(1 + "bonjou".length(), withLines.getBeginColumn());
        assertEquals(1 + "tri".length(), withLines.getEndColumn());
    }

    @Test
    public void testEmptyRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion);

        TextRegion region = TextRegion.fromOffsetLength("bonjour".length(), 0);

        assertEquals("bonjour".length(), region.getStartOffset());
        assertEquals(0, region.getLength());
        assertEquals(region.getStartOffset(), region.getEndOffset());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1 + "bonjour".length(), withLines.getBeginColumn());
        assertEquals(1 + "bonjour".length(), withLines.getEndColumn());
    }

    @Test
    public void testRegionOutOfBounds() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion);

        expect.expect(AssertionError.class);

        TextRegion.isValidRegion(0, 40, doc);
    }

}
