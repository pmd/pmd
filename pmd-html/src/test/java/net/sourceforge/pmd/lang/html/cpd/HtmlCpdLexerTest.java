/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.cpd;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.html.HtmlLanguageModule;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;

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

    /**
     * @see <a href="https://github.com/pmd/pmd/issues/6135">Issue 6135</a>
     */
    @Test
    void unescapedTagInScript() {
        doTest("UnescapedTagInScript");
    }
}
