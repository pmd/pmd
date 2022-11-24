/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ASTSqlStatementTest extends AbstractPLSQLParserTst {

    @Test
    void testCommit() {
        ASTInput input = plsql.parseResource("CommitStatement.pls");
        List<ASTSqlStatement> sqlStatements = input.findDescendantsOfType(ASTSqlStatement.class);
        assertEquals(1, sqlStatements.size());
        assertType(sqlStatements, 0, ASTSqlStatement.Type.COMMIT);
    }

    @Test
    void testRollback() {
        ASTInput input = plsql.parseResource("RollbackStatement.pls");
        List<ASTSqlStatement> sqlStatements = input.findDescendantsOfType(ASTSqlStatement.class);
        assertEquals(1, sqlStatements.size());
        assertType(sqlStatements, 0, ASTSqlStatement.Type.ROLLBACK);
    }

    @Test
    void testSavepoint() {
        ASTInput input = plsql.parseResource("SavepointStatement.pls");
        List<ASTSqlStatement> sqlStatements = input.findDescendantsOfType(ASTSqlStatement.class);
        assertEquals(2, sqlStatements.size());
        assertType(sqlStatements, 0, ASTSqlStatement.Type.SAVEPOINT);
        assertType(sqlStatements, 1, ASTSqlStatement.Type.ROLLBACK);
    }

    @Test
    void testSetTransaction() {
        ASTInput input = plsql.parseResource("SetTransactionStatement.pls");
        List<ASTSqlStatement> sqlStatements = input.findDescendantsOfType(ASTSqlStatement.class);
        assertEquals(3, sqlStatements.size());
        assertType(sqlStatements, 0, ASTSqlStatement.Type.COMMIT);
        assertType(sqlStatements, 1, ASTSqlStatement.Type.SET_TRANSACTION);
        assertType(sqlStatements, 2, ASTSqlStatement.Type.COMMIT);
    }

    private void assertType(List<ASTSqlStatement> sqlStatements, int index, ASTSqlStatement.Type expectedType) {
        assertEquals(expectedType, sqlStatements.get(index).getType());
    }
}
