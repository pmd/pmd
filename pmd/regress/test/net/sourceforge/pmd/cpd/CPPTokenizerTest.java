package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.CPPTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokens;

public class CPPTokenizerTest extends TestCase{

    public void testMultiLineMacros() throws Throwable {
        CPPTokenizer tokenizer = new CPPTokenizer();
        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(TEST1));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);
        assertEquals(7, tokens.size());
    }

    private static final String TEST1 =
    "#define FOO a +\\" + PMD.EOL +
    "            b +\\" + PMD.EOL +
    "            c +\\" + PMD.EOL +
    "            d +\\" + PMD.EOL +
    "            e +\\" + PMD.EOL +
    "            f +\\" + PMD.EOL +
    "            g"  + PMD.EOL +
    " void main() {}";


}
