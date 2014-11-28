/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Properties;

import net.sourceforge.pmd.PMD;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class CPPTokenizerTest {

    @Test
    public void testUTFwithBOM() {
        Tokens tokens = parse("\ufeffint start()\n{ int ret = 1;\nreturn ret;\n}\n");
        assertTrue(TokenEntry.getEOF() != tokens.getTokens().get(0));
        assertEquals(15, tokens.size());
    }

    @Test
    public void testMultiLineMacros() throws Throwable {
        Tokens tokens = parse(TEST1);
        assertEquals(7, tokens.size());
    }

    @Test
    public void testDollarSignInIdentifier() {
        parse(TEST2);
    }

    @Test
    public void testDollarSignStartingIdentifier() {
        parse(TEST3);
    }

    @Test
    public void testWideCharacters() {
        parse(TEST4);
    }

    @Test
    public void testContinuation_IntraToken() {
    	Tokens tokens = parse(TEST5);
        assertEquals(7, tokens.size());
    }
    
    @Test
    public void testContinuation_InterToken() {
    	Tokens tokens = parse(TEST6);
    	assertEquals(17, tokens.size());
    }

    @Test
    public void testTokenizerWithSkipBlocks() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"));
        Tokens tokens = parse(test, true);
        assertEquals(19, tokens.size());
    }

    @Test
    public void testTokenizerWithSkipBlocksPattern() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"));
        Tokens tokens = parse(test, true, "#if debug|#endif");
        assertEquals(31, tokens.size());
    }

    @Test
    public void testTokenizerWithoutSkipBlocks() throws Exception {
        String test = IOUtils.toString(CPPTokenizerTest.class.getResourceAsStream("cpp/cpp_with_asm.cpp"));
        Tokens tokens = parse(test, false);
        assertEquals(37, tokens.size());
    }

    private Tokens parse(String snippet) {
        return parse(snippet, false);
    }
    private Tokens parse(String snippet, boolean skipBlocks) {
        return parse(snippet, skipBlocks, null);
    }
    private Tokens parse(String snippet, boolean skipBlocks, String skipPattern) {
        Properties properties = new Properties();
        properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS, Boolean.toString(skipBlocks));
        if (skipPattern != null) {
            properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS_PATTERN, skipPattern);
        }

        CPPTokenizer tokenizer = new CPPTokenizer();
        tokenizer.setProperties(properties);

        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(snippet));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(code, tokens);
        return tokens;
    }

    private static final String TEST1 =
            "#define FOO a +\\" + PMD.EOL +
            "            b +\\" + PMD.EOL +
            "            c +\\" + PMD.EOL +
            "            d +\\" + PMD.EOL +
            "            e +\\" + PMD.EOL +
            "            f +\\" + PMD.EOL +
            "            g" + PMD.EOL +
            " void main() {}";

    private static final String TEST2 =
            " void main() { int x$y = 42; }";

    private static final String TEST3 =
            " void main() { int $x = 42; }";

    private static final String TEST4 =
            " void main() { char x = L'a'; }";
    
    private static final String TEST5 =
            "v\\" + PMD.EOL +
            "o\\" + PMD.EOL +
            "i\\" + PMD.EOL +
            "d\\" + PMD.EOL +
            " \\" + PMD.EOL +
            "m\\" + PMD.EOL +
            "a\\" + PMD.EOL +
            "i\\" + PMD.EOL +
            "n\\" + PMD.EOL +
            "(\\" + PMD.EOL +
            ")\\" + PMD.EOL +
            " \\" + PMD.EOL +
            "{\\" + PMD.EOL +
            " \\" + PMD.EOL +
            "}\\" + PMD.EOL;
    
    private static final String TEST6 =
            "#include <iostream>" + PMD.EOL +
            PMD.EOL +
            "int main()" + PMD.EOL +
            "{" + PMD.EOL +
            "   std::cout << \"Hello, \" \\" + PMD.EOL +
            "                \"world!\\n\";" + PMD.EOL +
            "   return 0;" + PMD.EOL +
            "}";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(CPPTokenizerTest.class);
    }
}
