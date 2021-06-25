/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class MatchAlgorithmTest {

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

    @Test
    public void testSimple() throws IOException {
        JavaTokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(getSampleCode(), "Foo.java"));
        Tokens tokens = new Tokens();
        TokenEntry.clearImages();
        tokenizer.tokenize(sourceCode, tokens);
        assertEquals(41, tokens.size());
        Map<String, SourceCode> codeMap = new HashMap<>();
        codeMap.put("Foo.java", sourceCode);

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens, 5);
        matchAlgorithm.findMatches();
        Iterator<Match> matches = matchAlgorithm.matches();
        Match match = matches.next();
        assertFalse(matches.hasNext());

        Iterator<Mark> marks = match.iterator();
        Mark mark1 = marks.next();
        Mark mark2 = marks.next();
        assertFalse(marks.hasNext());

        assertEquals(3, mark1.getBeginLine());
        assertEquals("Foo.java", mark1.getFilename());
        assertEquals(LINE_3, mark1.getSourceCodeSlice());

        assertEquals(4, mark2.getBeginLine());
        assertEquals("Foo.java", mark2.getFilename());
        assertEquals(LINE_4, mark2.getSourceCodeSlice());
    }

    @Test
    public void testIgnore() throws IOException {
        JavaTokenizer tokenizer = new JavaTokenizer();
        tokenizer.setIgnoreLiterals(true);
        tokenizer.setIgnoreIdentifiers(true);
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(getSampleCode(), "Foo.java"));
        Tokens tokens = new Tokens();
        TokenEntry.clearImages();
        tokenizer.tokenize(sourceCode, tokens);
        Map<String, SourceCode> codeMap = new HashMap<>();
        codeMap.put("Foo.java", sourceCode);

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens, 5);
        matchAlgorithm.findMatches();
        Iterator<Match> matches = matchAlgorithm.matches();
        Match match = matches.next();
        assertFalse(matches.hasNext());

        Iterator<Mark> marks = match.iterator();
        marks.next();
        marks.next();
        marks.next();
        assertFalse(marks.hasNext());
    }
}
