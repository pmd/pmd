/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

class CsTokenizerTest extends CpdTextComparisonTest {

    CsTokenizerTest() {
        super(".cs");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/cs/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        CsTokenizer tok = new CsTokenizer();
        tok.setProperties(properties);
        return tok;
    }

    @Test
    void testSimpleClass() {
        doTest("simpleClass");
    }

    @Test
    void testSimpleClassMethodMultipleLines() {
        doTest("simpleClassMethodMultipleLines");
    }

    @Test
    void testStrings() {
        doTest("strings");
    }

    @Test
    void testOpenString() {
        assertThrows(TokenMgrError.class, () -> doTest("unlexable_string"));
    }

    @Test
    void testCommentsIgnored1() {
        doTest("comments");
    }

    @Test
    void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    @Test
    void testOperators() {
        doTest("operatorsAndStuff");
    }


    @Test
    void testLineNumberAfterMultilineString() {
        doTest("strings");
    }

    @Test
    void testDoNotIgnoreUsingDirectives() {
        doTest("usingDirectives");
    }

    @Test
    void testIgnoreUsingDirectives() {
        doTest("usingDirectives", "_ignored", ignoreUsings());
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
    void testCSharp7And8Additions() {
        doTest("csharp7And8Additions");
    }

    @Test
    void testAttributesAreNotIgnored() {
        doTest("attributes");
    }

    @Test
    void testAttributesAreIgnored() {
        doTest("attributes", "_ignored", skipAttributes());
    }

    private Properties ignoreUsings() {
        return properties(true, false, false);
    }

    private Properties skipLiteralSequences() {
        return properties(false, true, false);
    }

    private Properties skipAttributes() {
        return properties(false, false, true);
    }

    private Properties properties(boolean ignoreUsings, boolean ignoreLiteralSequences, boolean ignoreAttributes) {
        Properties properties = new Properties();
        properties.setProperty(Tokenizer.IGNORE_USINGS, Boolean.toString(ignoreUsings));
        properties.setProperty(Tokenizer.OPTION_IGNORE_LITERAL_SEQUENCES, Boolean.toString(ignoreLiteralSequences));
        properties.setProperty(Tokenizer.IGNORE_ANNOTATIONS, Boolean.toString(ignoreAttributes));
        return properties;
    }
}
