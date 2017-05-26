/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.Assert.assertEquals;

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
}
