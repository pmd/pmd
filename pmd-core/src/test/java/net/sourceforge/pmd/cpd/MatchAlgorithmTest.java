/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextFile;

class MatchAlgorithmTest {

    private static final String LINE_1 = "public class Foo { ";
    private static final String LINE_2 = " public void bar() {";
    private static final String LINE_3 = "  System.out.println(\"hello\");";
    private static final String LINE_4 = "  System.out.println(\"hello\");";
    private static final String LINE_5 = "  int i = 5";
    private static final String LINE_6 = "  System.out.print(\"hello\");";
    private static final String LINE_7 = " }";
    private static final String LINE_8 = "}";

    private static String getSampleCode() {
        return LINE_1 + "\n" + LINE_2 + "\n" + LINE_3 + "\n" + LINE_4 + "\n" + LINE_5 + "\n" + LINE_6
                + "\n" + LINE_7 + "\n" + LINE_8;
    }

    private static String getMultipleRepetitionsCode() {
        return "var x = [\n"
                + "  1, 1, 1, 1, 1, 1, 1, 1,\n"
                + "  0, 0, 0, 0, 0, 0, 0, 0,\n"
                + "  2, 2, 2, 2, 2, 2, 2, 2,\n"
                + "  0, 0, 0, 0, 0, 0, 0, 0,\n"
                + "  3, 3, 3, 3, 3, 3, 3, 3,\n"
                + "  0, 0, 0, 0, 0, 0, 0, 0,\n"
                + "  4, 4, 4, 4, 4, 4, 4, 4\n"
                + "];";
    }

    @Test
    void testSimple() throws IOException {
        DummyLanguageModule dummy = DummyLanguageModule.getInstance();
        CpdLexer cpdLexer = dummy.createCpdLexer(dummy.newPropertyBundle());
        FileId fileName = FileId.fromPathLikeString("Foo.dummy");
        TextFile textFile = TextFile.forCharSeq(getSampleCode(), fileName, dummy.getDefaultVersion());
        SourceManager sourceManager = new SourceManager(listOf(textFile));
        TokenFileSet tokens = new TokenFileSet();
        TextDocument sourceCode = sourceManager.get(textFile);
        TokenFileSet.TokenFile file = tokens.tokenize(cpdLexer, sourceCode);
        assertEquals(43, file.size());

        List<Match> matches = CpdAnalysis.findMatches(sourceManager, new CPDNullListener(), tokens, 5);
        assertEquals(1, matches.size());
        Match match = matches.get(0);

        Iterator<Mark> marks = match.iterator();
        Mark mark1 = marks.next();
        Mark mark2 = marks.next();
        assertFalse(marks.hasNext());

        assertEquals(3, mark1.getLocation().getStartLine());
        assertEquals(fileName, mark1.getLocation().getFileId());
        assertEquals(LINE_3 + "\n", sourceManager.getSlice(mark1).toString());

        assertEquals(4, mark2.getLocation().getStartLine());
        assertEquals(fileName, mark2.getLocation().getFileId());
        assertEquals(LINE_4 + "\n", sourceManager.getSlice(mark2).toString());
    }

    @Test
    void testMultipleMatches() throws IOException {
        DummyLanguageModule dummy = DummyLanguageModule.getInstance();
        CpdLexer cpdLexer = dummy.createCpdLexer(dummy.newPropertyBundle());
        FileId fileName = FileId.fromPathLikeString("Foo.dummy");
        TextFile textFile = TextFile.forCharSeq(getMultipleRepetitionsCode(), fileName, dummy.getDefaultVersion());
        SourceManager sourceManager = new SourceManager(listOf(textFile));
        TokenFileSet tokens = new TokenFileSet();
        TextDocument sourceCode = sourceManager.get(textFile);
        tokens.tokenize(cpdLexer, sourceCode);

        List<Match> matches = CpdAnalysis.findMatches(sourceManager, new CPDNullListener(), tokens, 15);
        assertEquals(1, matches.size());
        Match match = matches.get(0);

        Set<Mark> marks = match.getMarkSet();
        assertThat(marks, hasSize(3));
        Iterator<Mark> iter = marks.iterator();
        Mark mark1 = iter.next();
        Mark mark2 = iter.next();
        Mark mark3 = iter.next();

        assertEquals(2, mark1.getLocation().getStartLine());
        assertEquals(mark1.getLength(), 17);

        assertEquals(4, mark2.getLocation().getStartLine());
        assertEquals(mark2.getLength(), 17);

        assertEquals(6, mark3.getLocation().getStartLine());
        assertEquals(mark3.getLength(), 17);
    }
}
