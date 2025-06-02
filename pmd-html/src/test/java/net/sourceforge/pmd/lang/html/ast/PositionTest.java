/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.html.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.CoordinatesPrinter;

class PositionTest extends BaseTreeDumpTest {
    PositionTest() {
        super(CoordinatesPrinter.INSTANCE, ".html");
    }

    @Override
    public BaseParsingHelper<HtmlParsingHelper, ASTHtmlDocument> getParser() {
        return HtmlParsingHelper.DEFAULT.withResourceContext(HtmlTreeDumpTest.class, "testdata");
    }

    @Test
    void testPositions() {
        doTest("SimpleHtmlFile2");
    }
}
