/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.html.HtmlLanguageModule;

class HtmlTokenizerTest extends CpdTextComparisonTest {

    HtmlTokenizerTest() {
        super(HtmlLanguageModule.getInstance(), ".html");
    }

    @Test
    void testSimpleHtmlFile() {
        doTest("SimpleHtmlFile");
    }

}
