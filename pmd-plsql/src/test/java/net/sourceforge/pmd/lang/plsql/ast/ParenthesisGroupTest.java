/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;

public class ParenthesisGroupTest extends BaseTreeDumpTest {

    public ParenthesisGroupTest() {
        super(new RelevantAttributePrinter(), ".pls");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return PlsqlParsingHelper.DEFAULT.withResourceContext(getClass());
    }

    @Test
    public void parseParenthesisGroup0() {
        doTest("ParenthesisGroup0");
    }

    @Test
    public void parseParenthesisGroup1() {
        doTest("ParenthesisGroup1");
    }

    @Test
    public void parseParenthesisGroup2() {
        doTest("ParenthesisGroup2");
    }

}
