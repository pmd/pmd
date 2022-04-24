/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 * @author Clément Fournier
 */
public class TextRange2dTest {

    @Test
    public void testCtors() {
        TextRange2d pos = TextRange2d.range2d(1, 2, 3, 4);
        TextRange2d pos2 = TextRange2d.range2d(TextPos2d.pos2d(1, 2), TextPos2d.pos2d(3, 4));
        assertEquals(pos, pos2);
    }

    @Test
    public void testToString() {
        TextRange2d range = TextRange2d.range2d(1, 2, 3, 4);
        assertEquals(
            "1:2-3:4",
            range.toDisplayStringWithColon()
        );
        MatcherAssert.assertThat(range.toString(), containsString("!debug only!"));
    }

}
