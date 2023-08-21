/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.plsql.PlsqlParsingHelper;

class ExecuteImmediateBulkCollectTest extends BaseTreeDumpTest {

    ExecuteImmediateBulkCollectTest() {
        super(new RelevantAttributePrinter(), ".pls");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return PlsqlParsingHelper.DEFAULT.withResourceContext(getClass());
    }

    @Test
    void testExecuteImmediateBulkCollect1() {
        doTest("ExecuteImmediateBulkCollect1");
    }

    @Test
    void testExecuteImmediateBulkCollect2() {
        doTest("ExecuteImmediateBulkCollect2");
    }

    @Test
    void testExecuteImmediateBulkCollect3() {
        doTest("ExecuteImmediateBulkCollect3");
    }
}
