/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for {@link SourceCodePositioner}.
 */
public class SourceCodePositionerTest {

    private static final String SOURCE_CODE = "abcd\ndefghi\n\njklmn\nopq";

    /**
     * Tests whether the lines and columns are calculated correctly.
     */
    @Test
    public void testLineNumberFromOffset() {
        SourceCodePositioner positioner = new SourceCodePositioner(SOURCE_CODE);

        int offset;

        offset = SOURCE_CODE.indexOf('a');
        assertEquals(1, positioner.lineNumberFromOffset(offset));
        assertEquals(1, positioner.columnFromOffset(1, offset));

        offset = SOURCE_CODE.indexOf('b');
        assertEquals(1, positioner.lineNumberFromOffset(offset));
        assertEquals(2, positioner.columnFromOffset(1, offset));

        offset = SOURCE_CODE.indexOf('e');
        assertEquals(2, positioner.lineNumberFromOffset(offset));
        assertEquals(2, positioner.columnFromOffset(2, offset));

        offset = SOURCE_CODE.indexOf('q');
        assertEquals(5, positioner.lineNumberFromOffset(offset));
        assertEquals(3, positioner.columnFromOffset(5, offset));
    }


    @Test
    public void lineToOffsetMappingWithLineFeedShouldSucceed() {
        final String code = "public static int main(String[] args) {" + '\n'
            + "int var;" + '\n'
            + "}";

        final List<Integer> expectedLineToOffset = new ArrayList<>();
        expectedLineToOffset.add(0);
        expectedLineToOffset.add(40);
        expectedLineToOffset.add(49);

        SourceCodePositioner positioner = new SourceCodePositioner(code);

        assertEquals(expectedLineToOffset, positioner.getLineOffsets());
    }

    @Test
    public void lineToOffsetMappingWithCarriageReturnFeedLineFeedShouldSucceed() {
        final String code = "public static int main(String[] args) {" + "\r\n"
            + "int var;" + "\r\n"
            + "}";

        final List<Integer> expectedLineToOffset = new ArrayList<>();
        expectedLineToOffset.add(0);
        expectedLineToOffset.add(41);
        expectedLineToOffset.add(51);

        SourceCodePositioner positioner = new SourceCodePositioner(code);

        assertEquals(expectedLineToOffset, positioner.getLineOffsets());
    }

    @Test
    public void lineToOffsetMappingWithMixedLineSeparatorsShouldSucceed() {
        final String code = "public static int main(String[] args) {" + "\r\n"
            + "int var;" + "\n"
            + "}";

        final List<Integer> expectedLineToOffset = new ArrayList<>();
        expectedLineToOffset.add(0);
        expectedLineToOffset.add(41);
        expectedLineToOffset.add(50);

        SourceCodePositioner positioner = new SourceCodePositioner(code);

        assertEquals(expectedLineToOffset, positioner.getLineOffsets());
    }

}
