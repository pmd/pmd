/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.jsp.JspLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;


class JspCpdLexerTest extends CpdTextComparisonTest {

    JspCpdLexerTest() {
        super(JspLanguageModule.getInstance(), ".jsp");
    }

    @Test
    void scriptletWithString() {
        doTest("scriptletWithString");
    }
}
