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
public class TextPos2dTest {

    @Test
    public void testToString() {
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
        MatcherAssert.assertThat(pos.toString(), containsString("!debug only!"));
    }

}
