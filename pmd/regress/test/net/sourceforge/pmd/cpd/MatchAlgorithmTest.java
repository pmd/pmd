/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.JavaTokenizer;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.MatchAlgorithm;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.Tokens;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MatchAlgorithmTest extends TestCase {

    public static final String LINE_1 = "public class Foo { ";
    public static final String LINE_2 = " public void bar() {";
    public static final String LINE_3 = "  System.out.println(\"hello\");";
    public static final String LINE_4 = "  System.out.println(\"hello\");";
    public static final String LINE_5 = " }";
    public static final String LINE_6 = "}";

    public static String getSampleCode() {
        return
            LINE_1 + PMD.EOL +
            LINE_2 + PMD.EOL +
            LINE_3 + PMD.EOL +
            LINE_4 + PMD.EOL +
            LINE_5 + PMD.EOL +
            LINE_6;
    }

    public void testSimple() throws Throwable {
        String code = getSampleCode();
        JavaTokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode("Foo.java");
        Tokens tokens = new Tokens();
        TokenEntry.clearImages();
        sourceCode.readSource(new StringReader(code));
        tokenizer.tokenize(sourceCode, tokens);
        assertEquals(29, tokens.size());
        Map codeMap = new HashMap();
        codeMap.put("Foo.java", sourceCode);

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens, 5);
        matchAlgorithm.findMatches();
        Iterator matches = matchAlgorithm.matches();
        Match match = (Match)matches.next();
        assertFalse(matches.hasNext());

        Iterator marks = match.iterator();
        TokenEntry mark1 = (TokenEntry)marks.next();
        TokenEntry mark2 = (TokenEntry)marks.next();
        assertTrue(!marks.hasNext());

        assertEquals(3, mark1.getBeginLine());
        assertEquals(4, mark2.getBeginLine());
        assertTrue("Foo.java" == mark1.getTokenSrcID() && "Foo.java" == mark2.getTokenSrcID());
        assertEquals(LINE_3, match.getSourceCodeSlice());
    }
}
