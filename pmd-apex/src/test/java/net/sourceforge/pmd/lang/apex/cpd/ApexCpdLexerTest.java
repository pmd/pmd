/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class ApexCpdLexerTest extends CpdTextComparisonTest {

    ApexCpdLexerTest() {
        super(ApexLanguageModule.getInstance(), ".cls");
    }

    @Test
    void testTokenize() {
        doTest("Simple");
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

    @Test
    void lexExceptionExpected() {
        expectLexException("class Foo { String s = \"not a string literal\"; }");
    }

    @Test
    void caseInsensitiveStringLiterals() {
        doTest("StringLiterals5053");
    }
}
