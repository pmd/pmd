/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.Assert.*;

import org.junit.Test;

public class SourceCodePositionerTest {

    private final String SOURCE_CODE = "abcd\ndefghi\n\njklmn\nopq";

    @Test
    public void testLineNumberFromOffset() {
	SourceCodePositioner positioner = new SourceCodePositioner(SOURCE_CODE);
	
	int offset;

	offset = SOURCE_CODE.indexOf('a');
	assertEquals(1, positioner.lineNumberFromOffset(offset));
	assertEquals(1, positioner.columnFromOffset(offset));

	offset = SOURCE_CODE.indexOf('b');
	assertEquals(1, positioner.lineNumberFromOffset(offset));
	assertEquals(2, positioner.columnFromOffset(offset));

	offset = SOURCE_CODE.indexOf('e');
	assertEquals(2, positioner.lineNumberFromOffset(offset));
	assertEquals(2, positioner.columnFromOffset(offset));

	offset = SOURCE_CODE.indexOf('q');
	assertEquals(5, positioner.lineNumberFromOffset(offset));
	assertEquals(3, positioner.columnFromOffset(offset));
    }
}
