/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.cpd;

import net.sourceforge.pmd.lang.html.HtmlLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import org.junit.jupiter.api.Test;

class HtmlCpdLexerTest extends CpdTextComparisonTest {

    HtmlCpdLexerTest() {
        super(HtmlLanguageModule.getInstance(), ".html");
    }

    @Test
    void testSimpleHtmlFile() {
        doTest("SimpleHtmlFile");
    }

    @Test
    void invalidHtml() {
        doTest("InvalidHtml");
    }

    @Test
    void metaTag() {
        doTest("MetaTag");
    }
}
