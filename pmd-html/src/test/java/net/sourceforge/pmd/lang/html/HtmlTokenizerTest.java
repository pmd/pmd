/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class HtmlTokenizerTest extends CpdTextComparisonTest {

    HtmlTokenizerTest() {
        super(".html");
    }

    @Override
    protected String getResourcePrefix() {
        return "cpd";
    }

    @Test
    void testSimpleHtmlFile() {
        doTest("SimpleHtmlFile");
    }

}
