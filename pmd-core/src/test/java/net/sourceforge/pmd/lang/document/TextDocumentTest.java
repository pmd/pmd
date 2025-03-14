/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.PmdCoreTestUtils.dummyVersion;
import static net.sourceforge.pmd.lang.document.TextPos2d.pos2d;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;

class TextDocumentTest {

    @Test
    void testSingleLineRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion());

        TextRegion region = TextRegion.fromOffsetLength(0, "bonjour".length());

        assertEquals(0, region.getStartOffset());
        assertEquals("bonjour".length(), region.getLength());
        assertEquals("bonjour".length(), region.getEndOffset());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1, withLines.getStartColumn());
        assertEquals(1 + "bonjour".length(), withLines.getEndColumn());
        assertEquals("bonjour".length(), withLines.getEndColumn() - withLines.getStartColumn());
    }

    @Test
    void testRegionAtEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion());

        TextRegion region = TextRegion.fromOffsetLength(0, "bonjour\n".length());
        assertEquals("bonjour\n", doc.sliceOriginalText(region).toString());
        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1, withLines.getStartColumn());
        assertEquals(1 + "bonjour\n".length(), withLines.getEndColumn());
        assertEquals("bonjour\n".length(), withLines.getEndColumn() - withLines.getStartColumn());
    }

    @Test
    void testEmptyRegionAtEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion());
        //                                                             ^ The caret position right after the \n
        //                                                               We consider it's part of the next line

        TextRegion region = TextRegion.fromOffsetLength("bonjour\n".length(), 0);
        assertEquals("", doc.sliceOriginalText(region).toString());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(2, withLines.getStartLine());
        assertEquals(2, withLines.getEndLine());
        assertEquals(1, withLines.getStartColumn());
        assertEquals(1, withLines.getEndColumn());
    }

    @Test
    void testRegionForEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion());
        //                                                           [ [ The region containing the \n
        //                                                               We consider it ends on the same line, not the next one


        TextRegion region = TextRegion.fromOffsetLength("bonjour".length(), 1);
        assertEquals("\n", doc.sliceOriginalText(region).toString());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1 + "bonjour".length(), withLines.getStartColumn());
        assertEquals(1 + "bonjour\n".length(), withLines.getEndColumn());
    }

    @Test
    void testRegionAtEndOfFile() {
        TextDocument doc = TextDocument.readOnlyString("flemme", dummyVersion());

        TextRegion region = TextRegion.fromOffsetLength(0, doc.getLength());
        assertEquals(doc.getText(), doc.sliceOriginalText(region));

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1, withLines.getStartColumn());
        assertEquals(1 + doc.getLength(), withLines.getEndColumn());
    }

    @Test
    void testMultiLineRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion());

        TextRegion region = TextRegion.fromOffsetLength("bonjou".length(), "r\noha\ntri".length());

        assertEquals("bonjou".length(), region.getStartOffset());
        assertEquals("r\noha\ntri".length(), region.getLength());
        assertEquals("bonjour\noha\ntri".length(), region.getEndOffset());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getStartLine());
        assertEquals(3, withLines.getEndLine());
        assertEquals(1 + "bonjou".length(), withLines.getStartColumn());
        assertEquals(1 + "tri".length(), withLines.getEndColumn());
    }


    @Test
    void testLineColumnFromOffset() {
        TextDocument doc = TextDocument.readOnlyString("ab\ncd\n", dummyVersion());

        assertPos2dEqualsAt(doc, 0, "a", pos2d(1, 1), true);
        assertPos2dEqualsAt(doc, 0, "a", pos2d(1, 1), false);
        assertPos2dEqualsAt(doc, 1, "b", pos2d(1, 2), true);
        assertPos2dEqualsAt(doc, 2, "\n", pos2d(1, 3), true);
        assertPos2dEqualsAt(doc, 3, "c", pos2d(2, 1), true);
        assertPos2dEqualsAt(doc, 3, "c", pos2d(1, 4), false);
        assertPos2dEqualsAt(doc, 4, "d", pos2d(2, 2), true);
        assertPos2dEqualsAt(doc, 5, "\n", pos2d(2, 3), true);
        // EOF caret position
        assertEquals(pos2d(3, 1), doc.lineColumnAtOffset(6));
        assertThrows(IndexOutOfBoundsException.class, () -> doc.lineColumnAtOffset(7));
    }

    private void assertPos2dEqualsAt(TextDocument doc, int offset, String c, TextPos2d pos, boolean inclusive) {
        Chars slicedChar = doc.sliceTranslatedText(TextRegion.fromOffsetLength(offset, 1));
        assertEquals(c, slicedChar.toString());
        assertEquals(pos, doc.lineColumnAtOffset(offset, inclusive));
    }

    @Test
    void testEmptyRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion());

        TextRegion region = TextRegion.fromOffsetLength("bonjour".length(), 0);

        assertEquals("bonjour".length(), region.getStartOffset());
        assertEquals(0, region.getLength());
        assertEquals(region.getStartOffset(), region.getEndOffset());

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
        assertEquals(1 + "bonjour".length(), withLines.getStartColumn());
        assertEquals(1 + "bonjour".length(), withLines.getEndColumn());
    }


    @Test
    void testLineRange() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion());

        assertEquals(Chars.wrap("bonjour\n"), doc.sliceTranslatedText(doc.createLineRange(1, 1)));
        assertEquals(Chars.wrap("bonjour\noha\n"), doc.sliceTranslatedText(doc.createLineRange(1, 2)));
        assertEquals(Chars.wrap("oha\n"), doc.sliceTranslatedText(doc.createLineRange(2, 2)));
        assertEquals(Chars.wrap("oha\ntristesse"), doc.sliceTranslatedText(doc.createLineRange(2, 3)));
        assertThrows(IndexOutOfBoundsException.class, () -> doc.createLineRange(2, 1));
        assertThrows(IndexOutOfBoundsException.class, () -> doc.createLineRange(1, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> doc.createLineRange(0, 2));
    }

    @ParameterizedTest
    @MethodSource("documentProvider")
    void testEntireRegion(TextDocument doc) {
        assertEquals(TextRegion.fromOffsetLength(0, doc.getLength()),
                doc.getEntireRegion(),
                "getEntireRegion should return something based on length");
    }

    @ParameterizedTest
    @MethodSource("documentProvider")
    void testReader(TextDocument doc) throws IOException {

        assertEquals(doc.getText().toString(),
                     IOUtil.readToString(doc.newReader()),
                    "NewReader should read the text");

    }

    @Test
    void testRegionOutOfBounds() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion());

        assertThrows(AssertionError.class, () -> TextRegion.isValidRegion(0, 40, doc));
    }

    @SuppressWarnings("resource")
    static Object[] documentProvider() {
        LanguageVersion dummyVersion = DummyLanguageModule.getInstance().getDefaultVersion();
        return new TextDocument[] {
            TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion),
            TextDocument.readOnlyString("bonjour\n", dummyVersion),
            TextDocument.readOnlyString("\n", dummyVersion),
            TextDocument.readOnlyString("", dummyVersion),
            };
    }

}
