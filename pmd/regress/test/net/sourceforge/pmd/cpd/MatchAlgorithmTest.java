package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.JavaTokensTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.cpd.Mark;
import net.sourceforge.pmd.cpd.MatchAlgorithm;
import net.sourceforge.pmd.cpd.Match;

import java.io.StringReader;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class MatchAlgorithmTest extends TestCase {

    private static final String EOL = System.getProperty("line.separator");
    public void testSimple() throws Throwable {
        String code =
            "public class Foo { " + EOL +
            " public void bar() {" + EOL +
            "  System.out.println(\"hello\");" + EOL +
            "  System.out.println(\"hello\");" + EOL +
            " }" + EOL +
            "}";
        JavaTokensTokenizer tokenizer = new JavaTokensTokenizer();
        SourceCode sourceCode = new SourceCode("Foo.java");
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens, new StringReader(code));
        assertEquals(29, tokens.size());
        Map codeMap = new HashMap();
        codeMap.put("Foo.java", sourceCode);

        MatchAlgorithm matchAlgorithm = new MatchAlgorithm(codeMap, tokens);
        matchAlgorithm.findMatches(5);

        Iterator matches = matchAlgorithm.matches();
        Match match = (Match)matches.next();
        assertFalse(matches.hasNext());

        Iterator marks = match.iterator();
        Mark mark1 = (Mark)marks.next();
        Mark mark2 = (Mark)marks.next();
        assertTrue(!marks.hasNext());

        assertEquals(2, mark2.getBeginLine());
        assertEquals(3, mark1.getBeginLine());
        assertTrue("Foo.java" == mark1.getFile() && "Foo.java" == mark2.getFile());
    }
}
