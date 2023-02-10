/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.cpd.test.LanguagePropertyConfig;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;

class ApexTokenizerTest extends CpdTextComparisonTest {

    ApexTokenizerTest() {
        super(ApexLanguageModule.getInstance(), ".cls");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/apex/cpd/testdata";
    }


    @Test
    void testTokenize() {
        doTest("Simple");
    }

    @Test
    void testTokenizeCaseSensitive() {
        doTest("Simple", "_caseSensitive", caseSensitive());
    }

    /**
     * Comments are ignored since using ApexLexer.
     */
    @Test
    void testTokenizeWithComments() {
        doTest("comments");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }

    private LanguagePropertyConfig caseSensitive() {
        return properties(true);
    }

    private LanguagePropertyConfig properties(boolean caseSensitive) {
        return properties -> properties.setProperty(Tokenizer.CPD_CASE_SENSITIVE, caseSensitive);
    }

}
