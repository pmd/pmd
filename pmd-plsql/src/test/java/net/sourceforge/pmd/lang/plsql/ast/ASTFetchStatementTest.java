/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class ASTFetchStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void testBulkCollectLimit() {
        ASTInput input = plsql.parseResource("FetchStatementBulkCollectLimit.pls");
        List<ASTFetchStatement> fetchStatements = input.findDescendantsOfType(ASTFetchStatement.class);
        Assert.assertEquals(1, fetchStatements.size());
        ASTFetchStatement fetch = fetchStatements.get(0);
        Assert.assertTrue(fetch.isBulkCollect());
        Assert.assertTrue(fetch.isLimit());
    }

    @Test
    public void testFetch() {
        ASTInput input = plsql.parseResource("FetchStatement.pls");
        List<ASTFetchStatement> fetchStatements = input.findDescendantsOfType(ASTFetchStatement.class);
        Assert.assertEquals(1, fetchStatements.size());
        ASTFetchStatement fetch = fetchStatements.get(0);
        Assert.assertFalse(fetch.isBulkCollect());
        Assert.assertFalse(fetch.isLimit());
    }
}
