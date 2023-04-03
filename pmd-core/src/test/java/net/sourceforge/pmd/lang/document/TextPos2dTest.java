/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Clément Fournier
 */
class TextPos2dTest {

    @Test
    void testToString() {
        TextPos2d pos = TextPos2d.pos2d(1, 2);
        assertEquals(
            "line 1, column 2",
            pos.toDisplayStringInEnglish()
        );
        assertEquals(
            "1:2",
            pos.toDisplayStringWithColon()
        );
        assertEquals(
            "(line=1, column=2)",
            pos.toTupleString()
        );
        assertThat(pos.toString(), containsString("!debug only!"));
    }

    @Test
    void testEquals() {
        TextPos2d pos = TextPos2d.pos2d(1, 1);
        TextPos2d pos2 = TextPos2d.pos2d(1, 2);
        assertNotEquals(pos, pos2);
        assertEquals(pos, TextPos2d.pos2d(1, 1));
        assertEquals(pos2, pos2);
    }

    @Test
    void testCompareTo() {
        TextPos2d pos = TextPos2d.pos2d(1, 1);
        TextPos2d pos2 = TextPos2d.pos2d(1, 2);
        TextPos2d pos3 = TextPos2d.pos2d(2, 1);

        assertEquals(-1, pos.compareTo(pos2));
        assertEquals(-1, pos.compareTo(pos3));
        assertEquals(-1, pos2.compareTo(pos3));
        assertEquals(1, pos2.compareTo(pos));
        assertEquals(0, pos.compareTo(pos));
    }
}
