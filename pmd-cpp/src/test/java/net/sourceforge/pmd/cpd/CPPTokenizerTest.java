/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class CPPTokenizerTest extends CpdTextComparisonTest {

    CPPTokenizerTest() {
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

    @Override
    public Properties defaultProperties() {
        return dontSkipBlocks();
    }

    @Test
    void testUTFwithBOM() {
        Tokenizer tokenizer = newTokenizer(dontSkipBlocks());
        Tokens tokens = tokenize(tokenizer, "\ufeffint start()\n{ int ret = 1;\nreturn ret;\n}\n");
        assertEquals(15, tokens.size());
    }

    @Test
    void testContinuation() {
        doTest("continuation");
    }

    @Test
    void testContinuationInIdent() {
        doTest("continuation_intra_token");
    }

    @Test
    void testContinuationBetweenTokens() {
        doTest("continuation_inter_token");
    }

    @Test
    void testUnicodeStringSupport() {
        doTest("unicodeStrings");
    }

    @Test
    void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    @Test
    void testMultiLineMacros() {
        doTest("multilineMacros");
    }

    @Test
    void testIdentifierValidChars() {
        doTest("identifierChars");
    }

    @Test
    void testWrongUnicodeInIdentifier() {
        expectTokenMgrError(" void main() { int ⚜ = __; }");
    }

    @Test
    void testTokenizerWithSkipBlocks() {
        doTest("simpleSkipBlocks", "_skipDefault", skipBlocks());
    }

    @Test
    void testTokenizerWithSkipBlocksPattern() {
        doTest("simpleSkipBlocks", "_skipDebug", skipBlocks("#if debug|#endif"));
    }

    @Test
    void testTokenizerWithoutSkipBlocks() {
        doTest("simpleSkipBlocks", "_noSkip", dontSkipBlocks());
    }

    @Test
    void testAsm() {
        // ASM code containing the '@' character
        doTest("asm", "", dontSkipBlocks());
    }

    @Test
    void testPreprocessingDirectives() {
        doTest("preprocessorDirectives");
    }

    @Test
    void testLiterals() {
        doTest("literals");
    }

    @Test
    void testLexicalErrorFilename() {
        expectTokenMgrError(sourceText("issue-1559"), dontSkipBlocks());
    }


    @Test
    void testRawStringLiterals() {
        doTest("issue-1784");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }

    @Test
    void testLongListsOfNumbersAreNotIgnored() {
        doTest("listOfNumbers");
    }

    @Test
    void testLongListsOfNumbersAreIgnored() {
        doTest("listOfNumbers", "_ignored", skipLiteralSequences());
    }

    private static Properties skipBlocks(String skipPattern) {
        return properties(true, skipPattern, false);
    }

    private static Properties skipBlocks() {
        return skipBlocks(null);
    }

    private static Properties dontSkipBlocks() {
        return properties(false, null, false);
    }

    private static Properties skipLiteralSequences() {
        return properties(false, null, true);
    }

    private static Properties properties(boolean skipBlocks, String skipPattern, boolean skipLiteralSequences) {
        Properties properties = new Properties();
        properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS, Boolean.toString(skipBlocks));
        if (skipPattern != null) {
            properties.setProperty(Tokenizer.OPTION_SKIP_BLOCKS_PATTERN, skipPattern);
        }
        properties.setProperty(Tokenizer.OPTION_IGNORE_LITERAL_SEQUENCES, Boolean.toString(skipLiteralSequences));
        return properties;
    }
}
