package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.JavaTokensTokenizer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.cpd.CPD;

import java.io.StringReader;

public class SourceCodeTest extends TestCase {

    public void testSimple() throws Throwable {
        String code = MatchAlgorithmTest.getSampleCode();
        JavaTokensTokenizer tokenizer = new JavaTokensTokenizer();
        SourceCode sourceCode = new SourceCode("Foo.java");
        assertEquals("Foo.java", sourceCode.getFileName());
        tokenizer.tokenize(sourceCode, new Tokens(), new StringReader(code));

        assertEquals(MatchAlgorithmTest.LINE_1, sourceCode.getSlice(0,0));
        assertEquals(MatchAlgorithmTest.LINE_2, sourceCode.getSlice(1,1));
        assertEquals(MatchAlgorithmTest.LINE_1 + CPD.EOL + MatchAlgorithmTest.LINE_2, sourceCode.getSlice(0,1));
    }
}
