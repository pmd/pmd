/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ExecuteImmediateBulkCollectTest extends AbstractPLSQLParserTst {

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
