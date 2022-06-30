/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link SourceCodePositioner}.
 */
class SourceCodePositionerTest {

    private static final String SOURCE_CODE = "abcd\ndefghi\n\njklmn\nopq";

    /**
     * Tests whether the lines and columns are calculated correctly.
     */
    @Test
    void testLineNumberFromOffset() {
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
}
