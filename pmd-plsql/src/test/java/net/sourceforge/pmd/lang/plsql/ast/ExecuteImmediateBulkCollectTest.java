/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;

public class ExecuteImmediateBulkCollectTest extends BaseTreeDumpTest {

    public ExecuteImmediateBulkCollectTest() {
        super(new RelevantAttributePrinter(), ".pls");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return PlsqlParsingHelper.DEFAULT.withResourceContext(getClass());
    }

    @Test
    public void testExecuteImmediateBulkCollect1() {
        doTest("ExecuteImmediateBulkCollect1");
    }

    @Test
    public void testExecuteImmediateBulkCollect2() {
        doTest("ExecuteImmediateBulkCollect2");
    }

    @Test
    public void testExecuteImmediateBulkCollect3() {
        doTest("ExecuteImmediateBulkCollect3");
    }
}
