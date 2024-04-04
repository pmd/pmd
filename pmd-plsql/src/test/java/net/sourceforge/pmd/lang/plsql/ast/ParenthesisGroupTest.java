/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.RelevantAttributePrinter;

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
