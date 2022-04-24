/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

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

        // todo rename to getStartLine
        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        // todo rename to getStartLine
        assertEquals(1, withLines.getStartColumn());
        assertEquals(1 + "bonjour".length(), withLines.getEndColumn());
        // todo rename to getStartLine
        assertEquals("bonjour".length(), withLines.getEndColumn() - withLines.getStartColumn());
    }

    @Test
    public void testRegionAtEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);

        TextRegion region = TextRegion.fromOffsetLength(0, "bonjour\n".length());
        assertEquals("bonjour\n", doc.sliceOriginalText(region).toString());
        FileLocation withLines = doc.toLocation(region);

        // todo rename to getStartLine
        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        // todo rename to getStartLine
        assertEquals(1, withLines.getStartColumn());
        assertEquals(1 + "bonjour\n".length(), withLines.getEndColumn());
        // todo rename to getStartLine
        assertEquals("bonjour\n".length(), withLines.getEndColumn() - withLines.getStartColumn());
    }

    @Test
    public void testEmptyRegionAtEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);
        //                                                             ^ The caret position right after the \n
        //                                                               We consider it's part of the next line

        TextRegion region = TextRegion.fromOffsetLength("bonjour\n".length(), 0);
        assertEquals("", doc.sliceOriginalText(region).toString());

        FileLocation withLines = doc.toLocation(region);

        // todo rename to getStartLine
        assertEquals(2, withLines.getStartLine());
        assertEquals(2, withLines.getEndLine());
        // todo rename to getStartLine
        assertEquals(1, withLines.getStartColumn());
        assertEquals(1, withLines.getEndColumn());
    }

    @Test
    public void testRegionForEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);
        //                                                           [ [ The region containing the \n
        //                                                               We consider it ends on the same line, not the next one


        TextRegion region = TextRegion.fromOffsetLength("bonjour".length(), 1);
        assertEquals("\n", doc.sliceOriginalText(region).toString());

        FileLocation withLines = doc.toLocation(region);

        // todo rename to getStartLine
        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        // todo rename to getStartLine
        assertEquals(1 + "bonjour".length(), withLines.getStartColumn());
        assertEquals(1 + "bonjour\n".length(), withLines.getEndColumn());
    }

    @Test
    public void testRegionAtEndOfFile() {
        TextDocument doc = TextDocument.readOnlyString("flemme", dummyVersion);

        TextRegion region = TextRegion.fromOffsetLength(0, doc.getLength());
        assertEquals(doc.getText(), doc.sliceOriginalText(region));

        FileLocation withLines = doc.toLocation(region);

        // todo rename to getStartLine
        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        // todo rename to getStartLine
        assertEquals(1, withLines.getStartColumn());
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

        // todo rename to getStartLine
        assertEquals(1, withLines.getStartLine());
        assertEquals(3, withLines.getEndLine());
        // todo rename to getStartLine
        assertEquals(1 + "bonjou".length(), withLines.getStartColumn());
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

        // todo rename to getStartLine
        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        // todo rename to getStartLine
        assertEquals(1 + "bonjour".length(), withLines.getStartColumn());
        assertEquals(1 + "bonjour".length(), withLines.getEndColumn());
    }

    @Test
    public void testOffsetFromLineColumn() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noa\n", dummyVersion);

        assertEquals(0, doc.offsetAtLineColumn(1, 1));
        assertEquals(1, doc.offsetAtLineColumn(1, 2));
        assertEquals(2, doc.offsetAtLineColumn(1, 3));
        assertEquals(3, doc.offsetAtLineColumn(1, 4));
        assertEquals(4, doc.offsetAtLineColumn(1, 5));
        assertEquals(5, doc.offsetAtLineColumn(1, 6));
        assertEquals(6, doc.offsetAtLineColumn(1, 7));
        assertEquals(7, doc.offsetAtLineColumn(1, 8));
        assertEquals(8, doc.offsetAtLineColumn(1, 9));
        assertEquals(8, doc.offsetAtLineColumn(2, 1));
        assertEquals(9, doc.offsetAtLineColumn(2, 2));
        assertEquals(10, doc.offsetAtLineColumn(2, 3));
        assertEquals(11, doc.offsetAtLineColumn(2, 4));
        assertEquals(11, doc.offsetAtLineColumn(3, 1));
    }

    @Test
    public void testCoordinateRoundTripWithEndOfLine() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noa\n", dummyVersion);
        TextRange2d inputRange = TextRange2d.fullLine(1, "bonjour\n".length());

        TextRegion lineRange = doc.createLineRange(1, 1);
        TextRegion region = doc.toRegion(inputRange);

        assertEquals(TextRegion.fromOffsetLength(0, "bonjour\n".length()), region);
        assertEquals(TextRegion.fromOffsetLength(0, "bonjour\n".length()), lineRange);
        TextRange2d roundTrip = doc.toRange2d(region);
        assertEquals(inputRange, roundTrip);

    }

    @Test
    public void testCoordinateRoundTripSimple() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noa\n", dummyVersion);
        TextRange2d inputRange = TextRange2d.fullLine(1, "bonjour".length());

        TextRegion region = doc.toRegion(inputRange);
        assertEquals(TextRegion.fromOffsetLength(0, "bonjour".length()), region);

        TextRange2d roundTrip = doc.toRange2d(region);
        assertEquals(inputRange, roundTrip);
    }

    @Test
    public void testLineRange() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion);

        assertEquals(Chars.wrap("bonjour\n"), doc.sliceTranslatedText(doc.createLineRange(1, 1)));
        assertEquals(Chars.wrap("bonjour\noha\n"), doc.sliceTranslatedText(doc.createLineRange(1, 2)));
        assertEquals(Chars.wrap("oha\n"), doc.sliceTranslatedText(doc.createLineRange(2, 2)));
        assertEquals(Chars.wrap("oha\ntristesse"), doc.sliceTranslatedText(doc.createLineRange(2, 3)));
        assertThrows(IndexOutOfBoundsException.class, () -> doc.createLineRange(2, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> doc.createLineRange(1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> doc.createLineRange(0, 2));
    }

    @Test
    public void testRegionOutOfBounds() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion);

        expect.expect(AssertionError.class);

        TextRegion.isValidRegion(0, 40, doc);
    }

}
