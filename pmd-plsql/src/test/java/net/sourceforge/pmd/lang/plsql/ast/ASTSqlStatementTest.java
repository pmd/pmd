/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class ASTSqlStatementTest extends AbstractPLSQLParserTst {

    @Test
    public void testCommit() {
        ASTInput input = plsql.parseResource("CommitStatement.pls");
        List<ASTSqlStatement> sqlStatements = input.findDescendantsOfType(ASTSqlStatement.class);
        Assert.assertEquals(1, sqlStatements.size());
        assertType(sqlStatements, 0, ASTSqlStatement.Type.COMMIT);
    }

    @Test
    public void testRollback() {
        ASTInput input = plsql.parseResource("RollbackStatement.pls");
        List<ASTSqlStatement> sqlStatements = input.findDescendantsOfType(ASTSqlStatement.class);
        Assert.assertEquals(1, sqlStatements.size());
        assertType(sqlStatements, 0, ASTSqlStatement.Type.ROLLBACK);
    }

    @Test
    public void testSavepoint() {
        ASTInput input = plsql.parseResource("SavepointStatement.pls");
        List<ASTSqlStatement> sqlStatements = input.findDescendantsOfType(ASTSqlStatement.class);
        Assert.assertEquals(2, sqlStatements.size());
        assertType(sqlStatements, 0, ASTSqlStatement.Type.SAVEPOINT);
        assertType(sqlStatements, 1, ASTSqlStatement.Type.ROLLBACK);
    }

    @Test
    public void testSetTransaction() {
        ASTInput input = plsql.parseResource("SetTransactionStatement.pls");
        List<ASTSqlStatement> sqlStatements = input.findDescendantsOfType(ASTSqlStatement.class);
        Assert.assertEquals(3, sqlStatements.size());
        assertType(sqlStatements, 0, ASTSqlStatement.Type.COMMIT);
        assertType(sqlStatements, 1, ASTSqlStatement.Type.SET_TRANSACTION);
        assertType(sqlStatements, 2, ASTSqlStatement.Type.COMMIT);
    }

    private void assertType(List<ASTSqlStatement> sqlStatements, int index, ASTSqlStatement.Type expectedType) {
        Assert.assertEquals(expectedType, sqlStatements.get(index).getType());
    }
}
