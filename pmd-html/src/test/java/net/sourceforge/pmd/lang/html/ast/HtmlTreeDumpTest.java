/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.html.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;

public class HtmlTreeDumpTest extends BaseTreeDumpTest {
    public HtmlTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".html");
    }

    @Override
    public BaseParsingHelper<HtmlParsingHelper, ASTHtmlDocument> getParser() {
        return HtmlParsingHelper.DEFAULT.withResourceContext(HtmlTreeDumpTest.class, "testdata");
    }

    @Test
    public void simpleHtmlFile() {
        doTest("SimpleHtmlFile");
    }

    @Test
    public void templateFragment() {
        doTest("TemplateFragment");
    }

    @Test
    public void simpleXmlFile() {
        doTest("SimpleXmlFile");
    }
}
