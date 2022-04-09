/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class ApexTokenizerTest extends CpdTextComparisonTest {

    public ApexTokenizerTest() {
        super(".cls");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/apex/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        ApexTokenizer tokenizer = new ApexTokenizer();
        tokenizer.setProperties(properties);
        return tokenizer;
    }


    @Test
    public void testTokenize() {
        doTest("Simple");
    }

    @Test
    public void testTokenizeCaseSensitive() {
        doTest("Simple", "_caseSensitive", caseSensitive());
    }

    /**
     * Comments are ignored since using ApexLexer.
     */
    @Test
    public void testTokenizeWithComments() {
        doTest("comments");
    }

    @Test
    public void testTabWidth() {
        doTest("tabWidth");
    }

    private Properties caseSensitive() {
        return properties(true);
    }

    private Properties properties(boolean caseSensitive) {
        Properties properties = new Properties();
        properties.setProperty(ApexTokenizer.CASE_SENSITIVE, Boolean.toString(caseSensitive));
        return properties;
    }

}
