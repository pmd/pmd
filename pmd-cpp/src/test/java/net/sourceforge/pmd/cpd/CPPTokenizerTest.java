/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.IOException;
import java.util.Properties;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

public class CPPTokenizerTest extends CpdTextComparisonTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public CPPTokenizerTest() {
        super(".cpp");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/cpp/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties props) {
        CPPTokenizer tok = new CPPTokenizer();
        tok.setProperties(props);
        return tok;
    }

    @Test
    public void testUTFwithBOM() {
        Tokens tokens = parse("\ufeffint start()\n{ int ret = 1;\nreturn ret;\n}\n");
        assertNotSame(TokenEntry.getEOF(), tokens.getTokens().get(0));
        assertEquals(15, tokens.size());
    }

    @Test
    public void testContinuation() {
        doTest("continuation");
    }

    @Test
    public void testContinuationInIdent() {
        doTest("continuation_intra_token");
    }

    @Test
    public void testContinuationBetweenTokens() {
        doTest("continuation_inter_token");
    }

    @Test
    public void testUnicodeSupport() {
        doTest("unicodeStrings");
    }

    @Test
    public void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    @Test
    public void testMultiLineMacros() {
        doTest("multilineMacros");
    }

    @Test
    public void testDollarSignInIdentifier() {
        doTest("identifierChars");
    }


    @Test
    public void testTokenizerWithSkipBlocks() {
        doTest("simpleSkipBlocks", "_skipDefault", skipBlocks());
    }

    @Test
    public void testTokenizerWithSkipBlocksPattern() {
        doTest("simpleSkipBlocks", "_skipDebug", skipBlocks("#if debug|#endif"));
    }

    @Test
    public void testTokenizerWithoutSkipBlocks() {
        doTest("simpleSkipBlocks", "_noSkip", dontSkipBlocks());
    }

    @Test
    public void testAsm() {
        // ASM code containing the '@' character
        doTest("asm", "", dontSkipBlocks());
    }

    @Test
    public void testEOLCommentInPreprocessingDirective() {
        parse("#define LSTFVLES_CPP  //*" + PMD.EOL);
    }

    @Test
    public void testLiterals() {
        doTest("literals");
    }

    @Test
    public void testLexicalErrorFilename() {
        expectedException.expect(TokenMgrError.class);
        expectedException.expectMessage("Lexical error in file issue-1559.cpp at");

        doTest("issue-1559", "", dontSkipBlocks());
    }


    @Test
    public void testRawStringLiterals() {
        doTest("issue-1784");
    }


    private Tokens parse(String snippet) {
        try {
            return parse(snippet, false, new Tokens());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Tokens parse(String snippet, boolean skipBlocks, Tokens tokens) throws IOException {
        return parse(snippet, skipBlocks, null, tokens);
    }

    private Tokens parse(String snippet, boolean skipBlocks, String skipPattern, Tokens tokens) throws IOException {

        Properties properties = properties(skipBlocks, skipPattern);

        CPPTokenizer tokenizer = new CPPTokenizer();
        tokenizer.setProperties(properties);

        SourceCode code = new SourceCode(new SourceCode.StringCodeLoader(snippet));
        tokenizer.tokenize(code, tokens);
        return tokens;
    }

    private static Properties skipBlocks(String skipPattern) {
        return properties(true, skipPattern);
    }

    private static Properties skipBlocks() {
        return skipBlocks(null);
    }

    private static Properties dontSkipBlocks() {
        return properties(false, null);
    }

    private static Properties properties(boolean skipBlocks, String skipPattern) {
        Properties properties = new Properties();
        properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS, Boolean.toString(skipBlocks));
        if (skipPattern != null) {
            properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS_PATTERN, skipPattern);
        }
        return properties;
    }
}
