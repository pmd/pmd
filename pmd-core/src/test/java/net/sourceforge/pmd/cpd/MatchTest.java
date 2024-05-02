/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdTestUtils.CpdReportBuilder;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;

class MatchTest {

    @Test
    void testSimple() {
        String codeFragment1 = "1234567890\n1234567890";
        FileId fileName = CpdTestUtils.FOO_FILE_ID;
        CpdReportBuilder test = new CpdReportBuilder();
        TextFile tf = TextFile.forCharSeq(codeFragment1, fileName, DummyLanguageModule.getInstance().getDefaultVersion());
        SourceManager sourceManager = new SourceManager(listOf(tf));

        Mark mark1 = test.createMark("public", fileName, 1, 1);
        Mark mark2 = test.createMark("public", fileName, 2, 1);
        Match match = Match.of(mark1, mark2);

        assertEquals(1, match.getMinTokenCount());
        // Returns the line count of the first mark
        assertEquals(1, match.getLineCount());
        // Returns the source code of the first mark (the entire line)
        assertEquals(Chars.wrap("1234567890\n"), sourceManager.getSlice(match.getFirstMark()));
        assertThat(match.getMarks(), hasSize(2));
        assertSame(mark1, match.getMarks().get(0));
        assertSame(mark2, match.getMarks().get(1));

        assertEquals(1, mark1.getLocation().getLineCount());
        assertEquals(Chars.wrap("1234567890\n"), sourceManager.getSlice(mark1));

        assertEquals(1, mark2.getLocation().getLineCount());
        assertEquals(Chars.wrap("1234567890"), sourceManager.getSlice(mark2));
    }

    @Test
    void testCompareTo() {
        FileId fileName = CpdTestUtils.FOO_FILE_ID;
        CpdReportBuilder test = new CpdReportBuilder();

        Match m1 = Match.of(test.createMark("public", fileName, 1, 1),
                            test.createMark("public", fileName, 4, 1));
        Match m2 = Match.of(test.createMark("Foo", fileName, 1, 2),
                            test.createMark("Foo", fileName, 1, 2));
        assertThat(m1, lessThan(m2));
    }
}
