/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

class MatchTest {

    @Test
    void testSimple() {
        int lineCount1 = 10;
        String codeFragment1 = "code fragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 1, lineCount1, codeFragment1);

        int lineCount2 = 20;
        String codeFragment2 = "code fragment 2";
        Mark mark2 = createMark("class", "/var/Foo.java", 1, lineCount2, codeFragment2);
        Match match = new Match(1, mark1, mark2);

        assertEquals(1, match.getTokenCount());
        // Returns the line count of the first mark
        assertEquals(lineCount1, match.getLineCount());
        // Returns the source code of the first mark
        assertEquals(codeFragment1, match.getSourceCodeSlice());
        Iterator<Mark> i = match.iterator();
        Mark occurrence1 = i.next();
        Mark occurrence2 = i.next();

        assertFalse(i.hasNext());

        assertEquals(mark1, occurrence1);
        assertEquals(lineCount1, occurrence1.getLineCount());
        assertEquals(codeFragment1, occurrence1.getSourceCodeSlice());

        assertEquals(mark2, occurrence2);
        assertEquals(lineCount2, occurrence2.getLineCount());
        assertEquals(codeFragment2, occurrence2.getSourceCodeSlice());
    }

    @Test
    void testCompareTo() {
        Match m1 = new Match(1, new TokenEntry("public", "/var/Foo.java", 1),
                new TokenEntry("class", "/var/Foo.java", 1));
        Match m2 = new Match(2, new TokenEntry("Foo", "/var/Foo.java", 1), new TokenEntry("{", "/var/Foo.java", 1));
        assertTrue(m2.compareTo(m1) < 0);
    }

    private Mark createMark(String image, String tokenSrcID, int beginLine, int lineCount, String code) {
        Mark result = new Mark(new TokenEntry(image, tokenSrcID, beginLine));

        result.setLineCount(lineCount);
        result.setSourceCode(new SourceCode(new SourceCode.StringCodeLoader(code)));
        return result;
    }
}
