/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.cpd.test.LanguagePropertyConfig;
import net.sourceforge.pmd.lang.cpp.CppLanguageModule;

class CPPTokenizerTest extends CpdTextComparisonTest {

    CPPTokenizerTest() {
        super(CppLanguageModule.getInstance(), ".cpp");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/cpp/cpd/testdata";
    }

    @Override
    public @NonNull LanguagePropertyConfig defaultProperties() {
        return dontSkipBlocks();
    }

    @Test
    void testUTFwithBOM() {
        Tokenizer tokenizer = newTokenizer(dontSkipBlocks());
        Tokens tokens = tokenize(tokenizer, sourceCodeOf("\ufeffint start()\n{ int ret = 1;\nreturn ret;\n}\n"));
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

    private static LanguagePropertyConfig skipBlocks(String skipPattern) {
        return properties(true, skipPattern, false);
    }

    private static LanguagePropertyConfig skipBlocks() {
        return skipBlocks(null);
    }

    private static LanguagePropertyConfig dontSkipBlocks() {
        return properties(false, null, false);
    }

    private static LanguagePropertyConfig skipLiteralSequences() {
        return properties(false, null, true);
    }

    private static LanguagePropertyConfig properties(boolean skipBlocks, String skipPattern, boolean skipLiteralSequences) {
        return properties -> {
            if (!skipBlocks) {
                properties.setProperty(CppLanguageModule.CPD_SKIP_BLOCKS, "");
            } else if (skipPattern != null) {
                properties.setProperty(CppLanguageModule.CPD_SKIP_BLOCKS, skipPattern);
            }
            properties.setProperty(Tokenizer.CPD_IGNORE_LITERAL_SEQUENCES, skipLiteralSequences);
        };
    }
}
