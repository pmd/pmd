/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.css.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.css.CssLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

class CssCpdLexerTest extends CpdTextComparisonTest {

    CssCpdLexerTest() {
        super(CssLanguageModule.getInstance(), ".css");
    }

    @Test
    void testLiterals() {
        doTest("literals");
    }
}
