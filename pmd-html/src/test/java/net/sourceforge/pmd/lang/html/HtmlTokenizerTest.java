/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.html.ast.HtmlTokenizer;

public class HtmlTokenizerTest extends CpdTextComparisonTest {

    public HtmlTokenizerTest() {
        super(".html");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new HtmlTokenizer();
    }

    @Override
    protected String getResourcePrefix() {
        return "cpd";
    }

    @Test
    public void testSimpleHtmlFile() {
        doTest("SimpleHtmlFile");
    }

}
