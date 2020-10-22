/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.ecmascript.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.NodePrintersKt;

public class JsTreeDumpTest extends BaseTreeDumpTest {
    public JsTreeDumpTest() {
        super(NodePrintersKt.getSimpleNodePrinter(), ".js");
    }

    @Override
    public BaseParsingHelper<JsParsingHelper, ASTAstRoot> getParser() {
        return JsParsingHelper.DEFAULT.withResourceContext(JsTreeDumpTest.class, "testdata");
    }

    @Test
    public void simpleJavascriptFile() {
        doTest("SimpleJavascriptFile");
    }

    @Test
    public void jquerySelector() {
        doTest("jquery-selector");
    }
}
