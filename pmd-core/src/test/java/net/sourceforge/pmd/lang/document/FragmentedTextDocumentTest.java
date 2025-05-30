/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static net.sourceforge.pmd.lang.document.TextPos2d.pos2d;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;

class FragmentedTextDocumentTest {

    LanguageVersion dummyVersion = DummyLanguageModule.getInstance().getDefaultVersion();

    @Test
    void testSimple() throws IOException {

        try (TextDocument base = TextDocument.readOnlyString("abc", dummyVersion)) {
            FragmentedDocBuilder builder = new FragmentedDocBuilder(base);
            builder.recordDelta(1, 2, Chars.wrap("abx"));
            try (TextDocument doc = builder.build()) {
                assertEquals("aabxc", doc.getText().toString());

                assertEquals(pos2d(1, 1), doc.lineColumnAtOffset(0));
                assertEquals(pos2d(1, 2), doc.lineColumnAtOffset(1, true));
                assertEquals(pos2d(1, 3), doc.lineColumnAtOffset(2, true));
                assertEquals(pos2d(1, 3), doc.lineColumnAtOffset(2, false));
                assertEquals(pos2d(1, 4), doc.lineColumnAtOffset(3, true));
                assertEquals(pos2d(1, 4), doc.lineColumnAtOffset(3, false));
                assertEquals(pos2d(1, 4), doc.lineColumnAtOffset(5));
            }

        }


    }

    @Test
    void testToLocationWithCaret() throws IOException {

        try (TextDocument base = TextDocument.readOnlyString("abc", dummyVersion)) {
            FragmentedDocBuilder builder = new FragmentedDocBuilder(base);
            builder.recordDelta(1, 2, Chars.wrap("abx"));
            try (TextDocument doc = builder.build()) {
                assertEquals("aabxc", doc.getText().toString());

                TextRegion region = TextRegion.caretAt(4);
                assertEquals(pos2d(1, 3), doc.toLocation(region).getStartPos());
            }

        }
    }

    @Test
    void testToLocationWithCaretBetweenEscapes() throws IOException {

        try (TextDocument base = TextDocument.readOnlyString("aBBCCd", dummyVersion)) {
            FragmentedDocBuilder builder = new FragmentedDocBuilder(base);
            builder.recordDelta(1, 3, Chars.wrap("X"));
            builder.recordDelta(3, 5, Chars.wrap("Y"));
            try (TextDocument doc = builder.build()) {
                assertEquals("aXYd", doc.getText().toString());

                TextRegion region = TextRegion.caretAt(2);
                assertEquals(pos2d(1, 4), doc.toLocation(region).getStartPos());
            }

        }
    }

    @Test
    void offsetAtLineColumn() throws IOException {
        try (TextDocument base = TextDocument.readOnlyString("ABC\n123\n", dummyVersion)) {
            FragmentedDocBuilder builder = new FragmentedDocBuilder(base);
            builder.recordDelta(3, 3, Chars.wrap("X"));
            builder.recordDelta(7, 7, Chars.wrap("Y"));
            try (TextDocument doc = builder.build()) {
                assertEquals("ABCX\n123Y\n", doc.getText().toString());

                assertEquals(0, doc.offsetAtLineColumn(pos2d(1, 1)));
                assertEquals(0, base.offsetAtLineColumn(pos2d(1, 1)));
                // pos2d is always in the coordinate system of the base document
                assertEquals(4, doc.offsetAtLineColumn(pos2d(2, 1)));
                assertEquals(4, base.offsetAtLineColumn(pos2d(2, 1)));
            }

        }
    }
}
