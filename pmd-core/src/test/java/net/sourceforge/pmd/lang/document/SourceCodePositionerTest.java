/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link SourceCodePositioner}.
 */
class SourceCodePositionerTest {

    @Test
    void testLineNumberFromOffset() {
        final String source = "abcd\ndefghi\n\rjklmn\ropq";

        SourceCodePositioner positioner = SourceCodePositioner.create(source);

        int offset;

        offset = source.indexOf('a');
        assertEquals(1, positioner.lineNumberFromOffset(offset));
        assertEquals(1, positioner.columnFromOffset(1, offset));

        offset = source.indexOf('b');
        assertEquals(1, positioner.lineNumberFromOffset(offset));
        assertEquals(2, positioner.columnFromOffset(1, offset));

        offset = source.indexOf('e');
        assertEquals(2, positioner.lineNumberFromOffset(offset));
        assertEquals(2, positioner.columnFromOffset(2, offset));

        offset = source.indexOf('q');
        assertEquals(3, positioner.lineNumberFromOffset(offset));
        assertEquals(10, positioner.columnFromOffset(3, offset));

        offset = source.length();
        assertEquals(3, positioner.lineNumberFromOffset(offset));
        assertEquals(11, positioner.columnFromOffset(3, offset));

        offset = source.length() + 1;
        assertEquals(-1, positioner.lineNumberFromOffset(offset));
        assertEquals(-1, positioner.columnFromOffset(3, offset));
    }

    @Test
    void testOffsetFromLineColumn() {
        final String source = "abcd\ndefghi\r\njklmn\nopq";

        SourceCodePositioner positioner = SourceCodePositioner.create(source);

        assertEquals(0, positioner.offsetFromLineColumn(1, 1));
        assertEquals(2, positioner.offsetFromLineColumn(1, 3));


        assertEquals("abcd\n".length(), positioner.offsetFromLineColumn(2, 1));
        assertEquals("abcd\nd".length(), positioner.offsetFromLineColumn(2, 2));
        assertEquals("abcd\nde".length(), positioner.offsetFromLineColumn(2, 3));
        assertEquals("abcd\ndef".length(), positioner.offsetFromLineColumn(2, 4));

        assertEquals("abcd\ndefghi\r\n".length(), positioner.offsetFromLineColumn(3, 1));
        assertEquals(source.length(), positioner.offsetFromLineColumn(4, 4));
        assertEquals(-1, positioner.offsetFromLineColumn(4, 5));
        assertEquals(source.length(), positioner.offsetFromLineColumn(5, 1));
        assertEquals(-1, positioner.offsetFromLineColumn(5, 2));
    }


    @Test
    void testWrongOffsets() {
        final String source = "abcd\ndefghi\r\njklmn\nopq";

        SourceCodePositioner positioner = SourceCodePositioner.create(source);

        assertEquals(0, positioner.offsetFromLineColumn(1, 1));
        assertEquals(1, positioner.offsetFromLineColumn(1, 2));
        assertEquals(2, positioner.offsetFromLineColumn(1, 3));
        assertEquals(3, positioner.offsetFromLineColumn(1, 4));
        assertEquals(4, positioner.offsetFromLineColumn(1, 5));
        assertEquals(5, positioner.offsetFromLineColumn(1, 6)); // this is right after the '\n'


        assertEquals(-1, positioner.offsetFromLineColumn(1, 7));
    }


    @Test
    void testEmptyDocument() {

        SourceCodePositioner positioner = SourceCodePositioner.create("");

        assertEquals(0, positioner.offsetFromLineColumn(1, 1));
        assertEquals(-1, positioner.offsetFromLineColumn(1, 2));

        assertEquals(1, positioner.lineNumberFromOffset(0));
        assertEquals(-1, positioner.lineNumberFromOffset(1));

        assertEquals(1, positioner.columnFromOffset(1, 0));
        assertEquals(-1, positioner.columnFromOffset(1, 1));

    }

    @Test
    void testDocumentStartingWithNl() {

        SourceCodePositioner positioner = SourceCodePositioner.create("\n");

        assertEquals(0, positioner.offsetFromLineColumn(1, 1));
        assertEquals(1, positioner.offsetFromLineColumn(1, 2));
        assertEquals(-1, positioner.offsetFromLineColumn(1, 3));

        assertEquals(1, positioner.lineNumberFromOffset(0));
        assertEquals(2, positioner.lineNumberFromOffset(1));
        assertEquals(-1, positioner.lineNumberFromOffset(2));

    }


    @Test
    void lineToOffsetMappingWithLineFeedShouldSucceed() {
        final String code = "public static int main(String[] args) {\n"
            + "int var;\n"
            + "}";

        SourceCodePositioner positioner = SourceCodePositioner.create(code);

        assertArrayEquals(new int[] { 0, 40, 49, 50 }, positioner.getLineOffsets());
    }

    @Test
    void lineToOffsetMappingWithCarriageReturnFeedLineFeedShouldSucceed() {
        final String code = "public static int main(String[] args) {\r\n"
            + "int var;\r\n"
            + "}";

        SourceCodePositioner positioner = SourceCodePositioner.create(code);

        assertArrayEquals(new int[] { 0, 41, 51, 52 }, positioner.getLineOffsets());
    }

    @Test
    void lineToOffsetMappingWithMixedLineSeparatorsShouldSucceed() {
        final String code = "public static int main(String[] args) {\r\n"
            + "int var;\n"
            + "}";

        SourceCodePositioner positioner = SourceCodePositioner.create(code);

        assertArrayEquals(new int[] { 0, 41, 50, 51 }, positioner.getLineOffsets());
    }

}
