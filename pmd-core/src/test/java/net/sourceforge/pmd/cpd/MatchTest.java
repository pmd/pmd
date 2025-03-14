/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;

class MatchTest {

    @Test
    void testSimple() {
        String codeFragment1 = "1234567890";
        FileId fileName = CpdTestUtils.FOO_FILE_ID;
        TextFile tf = TextFile.forCharSeq(codeFragment1, fileName, DummyLanguageModule.getInstance().getDefaultVersion());
        SourceManager sourceManager = new SourceManager(listOf(tf));
        Tokens tokens = new Tokens();
        Mark mark1 = new Mark(tokens.addToken("public", fileName, 1, 1, 1, 1 + "public".length()));

        Mark mark2 = new Mark(tokens.addToken("public", fileName, 1, 1, 1, 1 + "public".length()));
        Match match = new Match(1, mark1, mark2);

        assertEquals(1, match.getTokenCount());
        // Returns the line count of the first mark
        assertEquals(1, match.getLineCount());
        // Returns the source code of the first mark (the entire line)
        assertEquals(Chars.wrap("1234567890"), sourceManager.getSlice(match.getFirstMark()));
        Iterator<Mark> i = match.iterator();
        Mark occurrence1 = i.next();
        Mark occurrence2 = i.next();

        assertFalse(i.hasNext());

        assertEquals(mark1, occurrence1);
        assertEquals(1, occurrence1.getLocation().getLineCount());
        assertEquals(Chars.wrap("1234567890"), sourceManager.getSlice(mark1));

        assertEquals(mark2, occurrence2);
        assertEquals(1, occurrence2.getLocation().getLineCount());
        assertEquals(Chars.wrap("1234567890"), sourceManager.getSlice(mark2));
    }

    @Test
    void testCompareTo() {
        Tokens tokens = new Tokens();

        FileId fileName = CpdTestUtils.FOO_FILE_ID;
        Match m1 = new Match(1,
                             tokens.addToken("public", fileName, 1, 2, 3, 4),
                             tokens.addToken("class", fileName, 1, 2, 3, 4));
        Match m2 = new Match(2, tokens.addToken("Foo", fileName, 1, 2, 3, 4),
                             tokens.addToken("{", fileName, 1, 2, 3, 4));
        assertTrue(m2.compareTo(m1) < 0);
    }
}
