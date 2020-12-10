/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.ast.TokenMgrError;

public class CsTokenizerTest extends CpdTextComparisonTest {

    @org.junit.Rule
    public ExpectedException ex = ExpectedException.none();

    public CsTokenizerTest() {
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
    public void testSimpleClass() {
        doTest("simpleClass");
    }

    @Test
    public void testSimpleClassMethodMultipleLines() {
        doTest("simpleClassMethodMultipleLines");
    }

    @Test
    public void testStrings() {
        doTest("strings");
    }

    @Test
    public void testOpenString() {
        ex.expect(TokenMgrError.class);
        doTest("unlexable_string");
    }

    @Test
    public void testCommentsIgnored1() {
        doTest("comments");
    }

    @Test
    public void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    @Test
    public void testOperators() {
        doTest("operatorsAndStuff");
    }


    @Test
    public void testLineNumberAfterMultilineString() {
        doTest("strings");
    }

    @Test
    public void testDoNotIgnoreUsingDirectives() {
        doTest("usingDirectives");
    }

    @Test
    public void testIgnoreUsingDirectives() {
        doTest("usingDirectives", "_ignored", ignoreUsings());
    }

    @Test
    public void testTabWidth() {
        doTest("tabWidth");
    }

    @Test
    public void testLongListsOfNumbersAreNotIgnored() {
        doTest("listOfNumbers");
    }

    @Test
    public void testLongListsOfNumbersAreIgnored() {
        doTest("listOfNumbers", "_ignored", skipLiteralSequences());
    }

    private Properties ignoreUsings() {
        return properties(true, false);
    }

    private Properties skipLiteralSequences() {
        return properties(false, true);
    }

    private Properties properties(boolean ignoreUsings, boolean ignoreLiteralSequences) {
        Properties properties = new Properties();
        properties.setProperty(Tokenizer.IGNORE_USINGS, Boolean.toString(ignoreUsings));
        properties.setProperty(Tokenizer.OPTION_IGNORE_LITERAL_SEQUENCES, Boolean.toString(ignoreLiteralSequences));
        return properties;
    }
}
