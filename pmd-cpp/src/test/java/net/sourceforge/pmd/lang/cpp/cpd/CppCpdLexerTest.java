/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cpp.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.cpd.Tokens;
import net.sourceforge.pmd.lang.cpp.CppLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.test.cpd.LanguagePropertyConfig;

class CppCpdLexerTest extends CpdTextComparisonTest {

    CppCpdLexerTest() {
        super(CppLanguageModule.getInstance(), ".cpp");
    }

    @Override
    public @NonNull LanguagePropertyConfig defaultProperties() {
        return dontSkipBlocks();
    }

    @Test
    void testUTFwithBOM() {
        CpdLexer cpdLexer = newCpdLexer(dontSkipBlocks());
        Tokens tokens = tokenize(cpdLexer, sourceCodeOf("\ufeffint start()\n{ int ret = 1;\nreturn ret;\n}\n"));
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
        expectLexException(" void main() { int ⚜ = __; }");
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
        expectLexException(sourceText("issue-1559"), dontSkipBlocks());
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

    @Test
    void testLongListsOfNumbersAndIdentifiersAreIgnored() {
        doTest("listOfNumbers", "_ignored_identifiers", skipIdentifierAndLiteralsSequences());
    }

    @Test
    void testLongListsOfIdentifiersAreIgnored() {
        doTest("listOfNumbers", "_ignored_identifiers", skipIdentifierSequences());
    }

    private static LanguagePropertyConfig skipBlocks(String skipPattern) {
        return properties(true, skipPattern, false, false);
    }

    private static LanguagePropertyConfig skipBlocks() {
        return skipBlocks(null);
    }

    private static LanguagePropertyConfig dontSkipBlocks() {
        return properties(false, null, false, false);
    }

    private static LanguagePropertyConfig skipLiteralSequences() {
        return properties(false, null, true, false);
    }

    private static LanguagePropertyConfig skipIdentifierAndLiteralsSequences() {
        return properties(false, null, true, true);
    }

    private static LanguagePropertyConfig skipIdentifierSequences() {
        return properties(false, null, false, true);
    }

    private static LanguagePropertyConfig properties(boolean skipBlocks, String skipPattern, boolean skipLiteralSequences, boolean skipSequences) {
        return properties -> {
            if (!skipBlocks) {
                properties.setProperty(CppLanguageModule.CPD_SKIP_BLOCKS, "");
            } else if (skipPattern != null) {
                properties.setProperty(CppLanguageModule.CPD_SKIP_BLOCKS, skipPattern);
            }
            properties.setProperty(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES, skipLiteralSequences);
            properties.setProperty(CpdLanguageProperties.CPD_IGNORE_LITERAL_AND_IDENTIFIER_SEQUENCES, skipSequences);
        };
    }
}
