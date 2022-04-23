/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class ExecuteImmediateBulkCollectTest extends AbstractPLSQLParserTst {
    @Test
    public void testExecuteImmediateBulkCollect1() {
        ASTInput input = plsql.parseResource("ExecuteImmediateBulkCollect1.pls");
        Assert.assertNotNull(input);
    }

    @Test
    public void testExecuteImmediateBulkCollect2() {
        ASTInput input = plsql.parseResource("ExecuteImmediateBulkCollect2.pls");
        Assert.assertNotNull(input);
    }

    @Test
    public void testExecuteImmediateBulkCollect3() {
        ASTInput input = plsql.parseResource("ExecuteImmediateBulkCollect3.pls");
        Assert.assertNotNull(input);
    }
}
