/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ASTFetchStatementTest extends AbstractPLSQLParserTst {

    @Test
    void testBulkCollectLimit() {
        ASTInput input = plsql.parseResource("FetchStatementBulkCollectLimit.pls");
        List<ASTFetchStatement> fetchStatements = input.findDescendantsOfType(ASTFetchStatement.class);
        assertEquals(1, fetchStatements.size());
        ASTFetchStatement fetch = fetchStatements.get(0);
        assertTrue(fetch.isBulkCollect());
        assertTrue(fetch.isLimit());
    }

    @Test
    void testFetch() {
        ASTInput input = plsql.parseResource("FetchStatement.pls");
        List<ASTFetchStatement> fetchStatements = input.findDescendantsOfType(ASTFetchStatement.class);
        assertEquals(1, fetchStatements.size());
        ASTFetchStatement fetch = fetchStatements.get(0);
        assertFalse(fetch.isBulkCollect());
        assertFalse(fetch.isLimit());
    }
}
