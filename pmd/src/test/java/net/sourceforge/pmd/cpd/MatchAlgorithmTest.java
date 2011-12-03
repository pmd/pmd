/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.JavaTokenizer;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.MatchAlgorithm;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokens;

import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MatchAlgorithmTest {

    public static final String LINE_1 = "public class Foo { ";
    public static final String LINE_2 = " public void bar() {";
    public static final String LINE_3 = "  System.out.println(\"hello\");";
    public static final String LINE_4 = "  System.out.println(\"hello\");";
    public static final String LINE_5 = "  int i = 5";
    public static final String LINE_6 = "  System.out.print(\"hello\");";
    public static final String LINE_7 = " }";
    public static final String LINE_8 = "}";

    public static String getSampleCode() {
        return
                LINE_1 + PMD.EOL +
                LINE_2 + PMD.EOL +
                LINE_3 + PMD.EOL +
                LINE_4 + PMD.EOL +
                LINE_5 + PMD.EOL +
                LINE_6 + PMD.EOL +
                LINE_7 + PMD.EOL +
                LINE_8;
    }

    @Test
    public void testSimple() throws Throwable {
        JavaTokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(getSampleCode(), "Foo.java"));
        Tokens tokens = new Tokens();
        TokenEntry.clearImages();
        tokenizer.tokenize(sourceCode, tokens);
        assertEquals(41, tokens.size());
        Map<String, SourceCode> codeMap = new HashMap<String, SourceCode>();
        codeMap.put("Foo.java", sourceCode);

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens, 5);
        matchAlgorithm.findMatches();
        Iterator matches = matchAlgorithm.matches();
        Match match = (Match) matches.next();
        assertFalse(matches.hasNext());

        Iterator marks = match.iterator();
        TokenEntry mark1 = (TokenEntry) marks.next();
        TokenEntry mark2 = (TokenEntry) marks.next();
        assertFalse(marks.hasNext());

        assertEquals(3, mark1.getBeginLine());
        assertEquals(4, mark2.getBeginLine());
        assertTrue("Foo.java" == mark1.getTokenSrcID() && "Foo.java" == mark2.getTokenSrcID());
        assertEquals(LINE_3, match.getSourceCodeSlice());
    }

    @Test
    public void testIgnore() throws Throwable {
        JavaTokenizer tokenizer = new JavaTokenizer();
        tokenizer.setIgnoreLiterals(true);
        tokenizer.setIgnoreIdentifiers(true);
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(getSampleCode(), "Foo.java"));
        Tokens tokens = new Tokens();
        TokenEntry.clearImages();
        tokenizer.tokenize(sourceCode, tokens);
        Map<String, SourceCode> codeMap = new HashMap<String, SourceCode>();
        codeMap.put("Foo.java", sourceCode);

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens, 5);
        matchAlgorithm.findMatches();
        Iterator matches = matchAlgorithm.matches();
        Match match = (Match) matches.next();
        assertFalse(matches.hasNext());

        Iterator marks = match.iterator();
        marks.next();
        marks.next();
        marks.next();
        assertFalse(marks.hasNext());
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(MatchAlgorithmTest.class);
    }
}
