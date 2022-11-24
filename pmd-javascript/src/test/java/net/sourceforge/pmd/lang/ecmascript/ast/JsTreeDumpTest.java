/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.ecmascript.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.NodePrintersKt;

class JsTreeDumpTest extends BaseTreeDumpTest {
    JsTreeDumpTest() {
        super(NodePrintersKt.getSimpleNodePrinter(), ".js");
    }

    @Override
    public BaseParsingHelper<JsParsingHelper, ASTAstRoot> getParser() {
        return JsParsingHelper.DEFAULT.withResourceContext(JsTreeDumpTest.class, "testdata");
    }

    @Test
    void simpleJavascriptFile() {
        doTest("SimpleJavascriptFile");
    }

    @Test
    void jquerySelector() {
        doTest("jquery-selector");
    }

    @Test
    void decorators() {
        doTest("decorators");
    }

    @Test
    void templateStrings() {
        doTest("templateStrings");
    }

    @Test
    void issue3948() {
        // https://github.com/pmd/pmd/issues/3948
        doTest("issue3948");
    }
}
