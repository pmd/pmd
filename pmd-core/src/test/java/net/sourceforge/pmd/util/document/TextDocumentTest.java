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
    public void testRegionAtEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);

        TextRegion region = TextRegion.fromOffsetLength(0, "bonjour\n".length());
        assertEquals("bonjour\n", doc.sliceText(region).toString());
        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1, withLines.getBeginColumn());
        assertEquals(1 + "bonjour\n".length(), withLines.getEndColumn());
        assertEquals("bonjour\n".length(), withLines.getEndColumn() - withLines.getBeginColumn());
    }

    @Test
    public void testEmptyRegionAtEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);
        //                                                             ^ The caret position right after the \n
        //                                                               We consider it's part of the next line

        TextRegion region = TextRegion.fromOffsetLength("bonjour\n".length(), 0);
        assertEquals("", doc.sliceText(region).toString());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(2, withLines.getBeginLine());
        assertEquals(2, withLines.getEndLine());
        assertEquals(1, withLines.getBeginColumn());
        assertEquals(1, withLines.getEndColumn());
    }

    @Test
    public void testRegionForEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);
        //                                                           [ [ The region containing the \n
        //                                                               We consider it ends on the same line, not the next one


        TextRegion region = TextRegion.fromOffsetLength("bonjour".length(), 1);
        assertEquals("\n", doc.sliceText(region).toString());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1 + "bonjour".length(), withLines.getBeginColumn());
        assertEquals(1 + "bonjour\n".length(), withLines.getEndColumn());
    }

    @Test
    public void testRegionAtEndOfFile() {
        TextDocument doc = TextDocument.readOnlyString("flemme", dummyVersion);

        TextRegion region = TextRegion.fromOffsetLength(0, doc.getLength());
        assertEquals(doc.getText(), doc.sliceText(region));

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getBeginLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1, withLines.getBeginColumn());
        assertEquals(1 + doc.getLength(), withLines.getEndColumn());
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
