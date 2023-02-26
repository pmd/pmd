/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.jsp.JspLanguageModule;


class JSPTokenizerTest extends CpdTextComparisonTest {

    JSPTokenizerTest() {
        super(JspLanguageModule.getInstance(), ".jsp");
    }

    @Test
    void scriptletWithString() {
        doTest("scriptletWithString");
    }
}
