/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;

class ParenthesisGroupTest extends BaseTreeDumpTest {

    ParenthesisGroupTest() {
        super(new RelevantAttributePrinter(), ".pls");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return PlsqlParsingHelper.DEFAULT.withResourceContext(getClass());
    }

    @Test
    void parseParenthesisGroup0() {
        doTest("ParenthesisGroup0");
    }

    @Test
    void parseParenthesisGroup1() {
        doTest("ParenthesisGroup1");
    }

    @Test
    void parseParenthesisGroup2() {
        doTest("ParenthesisGroup2");
    }

}
