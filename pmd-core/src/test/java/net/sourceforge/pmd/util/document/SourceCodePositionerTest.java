/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for {@link SourceCodePositioner}.
 */
public class SourceCodePositionerTest {

    @Test
    public void testLineNumberFromOffset() {
        final String source = "abcd\ndefghi\n\njklmn\nopq";

        SourceCodePositioner positioner = new SourceCodePositioner(source);

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
        assertEquals(5, positioner.lineNumberFromOffset(offset));
        assertEquals(3, positioner.columnFromOffset(5, offset));

        offset = source.length();
        assertEquals(5, positioner.lineNumberFromOffset(offset));
        assertEquals(4, positioner.columnFromOffset(5, offset));

        offset = source.length() + 1;
        assertEquals(-1, positioner.lineNumberFromOffset(offset));
        assertEquals(-1, positioner.columnFromOffset(5, offset));
    }

    @Test
    public void testOffsetFromLineColumn() {
        final String source = "abcd\ndefghi\r\njklmn\nopq";

        SourceCodePositioner positioner = new SourceCodePositioner(source);

        assertEquals(0, positioner.offsetFromLineColumn(1, 1));
        assertEquals(2, positioner.offsetFromLineColumn(1, 3));


        assertEquals("abcd\n".length(), positioner.offsetFromLineColumn(2, 1));
        assertEquals("abcd\nd".length(), positioner.offsetFromLineColumn(2, 2));
        assertEquals("abcd\nde".length(), positioner.offsetFromLineColumn(2, 3));
        assertEquals("abcd\ndef".length(), positioner.offsetFromLineColumn(2, 4));

        assertEquals("abcd\ndefghi\r\n".length(), positioner.offsetFromLineColumn(3, 1));
    }


    @Test
    public void testWrongOffsets() {
        final String source = "abcd\ndefghi\r\njklmn\nopq";

        SourceCodePositioner positioner = new SourceCodePositioner(source);

        assertEquals(0, positioner.offsetFromLineColumn(1, 1));
        assertEquals(1, positioner.offsetFromLineColumn(1, 2));
        assertEquals(2, positioner.offsetFromLineColumn(1, 3));
        assertEquals(3, positioner.offsetFromLineColumn(1, 4));
        assertEquals(4, positioner.offsetFromLineColumn(1, 5));
        assertEquals(5, positioner.offsetFromLineColumn(1, 6)); // this is right after the '\n'


        assertEquals(-1, positioner.offsetFromLineColumn(1, 7));
    }


    @Test
    public void testEmptyDocument() {

        SourceCodePositioner positioner = new SourceCodePositioner("");

        assertEquals(0, positioner.offsetFromLineColumn(1, 1));
        assertEquals(-1, positioner.offsetFromLineColumn(1, 2));

        assertEquals(1, positioner.lineNumberFromOffset(0));
        assertEquals(-1, positioner.lineNumberFromOffset(1));

        assertEquals(1, positioner.columnFromOffset(1, 0));
        assertEquals(-1, positioner.columnFromOffset(1, 1));

    }


    @Test
    public void lineToOffsetMappingWithLineFeedShouldSucceed() {
        final String code = "public static int main(String[] args) {\n"
            + "int var;\n"
            + "}";

        SourceCodePositioner positioner = new SourceCodePositioner(code);

        assertArrayEquals(new int[] { 0, 40, 49 }, positioner.getLineOffsets());
    }

    @Test
    public void lineToOffsetMappingWithCarriageReturnFeedLineFeedShouldSucceed() {
        final String code = "public static int main(String[] args) {\r\n"
            + "int var;\r\n"
            + "}";

        SourceCodePositioner positioner = new SourceCodePositioner(code);

        assertArrayEquals(new int[] { 0, 41, 51 }, positioner.getLineOffsets());
    }

    @Test
    public void lineToOffsetMappingWithMixedLineSeparatorsShouldSucceed() {
        final String code = "public static int main(String[] args) {\r\n"
            + "int var;\n"
            + "}";

        SourceCodePositioner positioner = new SourceCodePositioner(code);

        assertArrayEquals(new int[] { 0, 41, 50 }, positioner.getLineOffsets());
    }

}
