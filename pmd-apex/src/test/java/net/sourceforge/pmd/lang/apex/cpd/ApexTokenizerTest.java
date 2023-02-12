/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.cpd;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.cpd.test.LanguagePropertyConfig;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;

class ApexTokenizerTest extends CpdTextComparisonTest {

    ApexTokenizerTest() {
        super(ApexLanguageModule.getInstance(), ".cls");
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

    @Override
    public @NonNull LanguagePropertyConfig defaultProperties() {
        return properties(false);
    }

    private LanguagePropertyConfig properties(boolean caseSensitive) {
        return properties -> properties.setProperty(Tokenizer.CPD_CASE_SENSITIVE, caseSensitive);
    }

}
