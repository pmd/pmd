/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * @author Clément Fournier
 */
class FileLocationTest {

    public static final FileId FNAME = FileId.fromPathLikeString("fname");

    @Test
    void testSimple() {
        FileLocation loc = FileLocation.range(FNAME, TextRange2d.range2d(1, 1, 1, 2));
        assertEquals(FNAME, loc.getFileId());
        assertEquals(1, loc.getStartLine());
        assertEquals(1, loc.getStartColumn());
        assertEquals(1, loc.getEndLine());
        assertEquals(2, loc.getEndColumn());
    }

    @Test
    void testToRange() {
        TextRange2d range2d = TextRange2d.range2d(1, 1, 1, 2);
        FileLocation loc = FileLocation.range(FNAME, range2d);
        assertEquals(range2d, loc.toRange2d());
    }

    @Test
    void testToString() {
        FileLocation loc = FileLocation.range(FNAME, TextRange2d.range2d(1, 1, 1, 2));

        assertEquals(
            "line 1, column 1",
            loc.startPosToString()
        );
        assertEquals(
            "fname:1:1",
            loc.startPosToStringWithFile()
        );

        assertThat(loc.toString(), containsString("!debug only!"));
    }

}
