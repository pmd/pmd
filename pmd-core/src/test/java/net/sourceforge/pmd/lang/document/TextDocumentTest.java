/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.lang.document.TextPos2d.pos2d;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class TextDocumentTest {

    private final LanguageVersion dummyVersion = LanguageRegistry.getDefaultLanguage().getDefaultVersion();

    @Test
    public void testSingleLineRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);

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
    public void testRegionAtEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);

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
    public void testEmptyRegionAtEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);
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
    public void testRegionForEol() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\ntristesse", dummyVersion);
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
    public void testRegionAtEndOfFile() {
        TextDocument doc = TextDocument.readOnlyString("flemme", dummyVersion);

        TextRegion region = TextRegion.fromOffsetLength(0, doc.getLength());
        assertEquals(doc.getText(), doc.sliceOriginalText(region));

        FileLocation withLines = doc.toLocation(region);

        assertEquals(1, withLines.getStartLine());
        assertEquals(1, withLines.getEndLine());
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

        assertEquals(1, withLines.getStartLine());
        assertEquals(3, withLines.getEndLine());
        assertEquals(1 + "bonjou".length(), withLines.getStartColumn());
        assertEquals(1 + "tri".length(), withLines.getEndColumn());
    }


    @Test
    public void testLineColumnFromOffset() {
        TextDocument doc = TextDocument.readOnlyString("ab\ncd\n", dummyVersion);

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
    public void testEmptyRegion() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion);

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
    @Parameters(source = DocumentsProvider.class)
    public void testEntireRegion(TextDocument doc) {
        assertEquals("getEntireRegion should return something based on length",
                     TextRegion.fromOffsetLength(0, doc.getLength()),
                     doc.getEntireRegion());
    }

    @Test
    @Parameters(source = DocumentsProvider.class)
    public void testReader(TextDocument doc) throws IOException {

        assertEquals("NewReader should read the text",
                     doc.getText().toString(),
                     IOUtils.toString(doc.newReader())
        );

    }

    @Test
    public void testRegionOutOfBounds() {
        TextDocument doc = TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion);

        assertThrows(AssertionError.class, () -> TextRegion.isValidRegion(0, 40, doc));
    }

    // for junit params runner
    public static final class DocumentsProvider {


        @SuppressWarnings("resource")
        public static Object[] provideParameters() {
            LanguageVersion dummyVersion = DummyLanguageModule.getInstance().getDefaultVersion();
            return new TextDocument[] {
                TextDocument.readOnlyString("bonjour\noha\ntristesse", dummyVersion),
                TextDocument.readOnlyString("bonjour\n", dummyVersion),
                TextDocument.readOnlyString("\n", dummyVersion),
                TextDocument.readOnlyString("", dummyVersion),
                };
        }
    }

}
