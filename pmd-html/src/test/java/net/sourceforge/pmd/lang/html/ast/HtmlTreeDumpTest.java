/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

class HtmlTreeDumpTest extends BaseTreeDumpTest {
    HtmlTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".html");
    }

    @Override
    public BaseParsingHelper<HtmlParsingHelper, ASTHtmlDocument> getParser() {
        return HtmlParsingHelper.DEFAULT.withResourceContext(HtmlTreeDumpTest.class, "testdata");
    }

    @Test
    void simpleHtmlFile() {
        doTest("SimpleHtmlFile");
    }

    @Test
    void templateFragment() {
        doTest("TemplateFragment");
    }

    @Test
    void simpleXmlFile() {
        doTest("SimpleXmlFile");
    }
}
