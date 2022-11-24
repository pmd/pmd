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
class TextRange2dTest {

    @Test
    void testCtors() {
        TextRange2d pos = TextRange2d.range2d(1, 2, 3, 4);
        TextRange2d pos2 = TextRange2d.range2d(TextPos2d.pos2d(1, 2), TextPos2d.pos2d(3, 4));
        assertEquals(pos, pos2);
    }

    @Test
    void testEquals() {
        TextRange2d pos = TextRange2d.range2d(1, 1, 1, 1);
        TextRange2d pos2 = TextRange2d.range2d(1, 1, 1, 2);
        assertNotEquals(pos, pos2);
        assertEquals(pos, pos);
        assertEquals(pos2, pos2);
    }

    @Test
    void testCompareTo() {
        TextRange2d pos = TextRange2d.range2d(1, 1, 1, 1);
        TextRange2d pos2 = TextRange2d.range2d(1, 1, 1, 2);

        assertEquals(-1, pos.compareTo(pos2));
        assertEquals(1, pos2.compareTo(pos));
        assertEquals(0, pos.compareTo(pos));
    }

    @Test
    void testToString() {
        TextRange2d range = TextRange2d.range2d(1, 2, 3, 4);
        assertEquals(
            "1:2-3:4",
            range.toDisplayStringWithColon()
        );
        assertThat(range.toString(), containsString("!debug only!"));
    }

}
