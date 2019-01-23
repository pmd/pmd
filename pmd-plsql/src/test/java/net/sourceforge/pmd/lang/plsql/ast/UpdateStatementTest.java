/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class UpdateStatementTest extends AbstractPLSQLParserTst {
    @Test
    public void parseUpdateStatementExample() {
        String code = loadTestResource("UpdateStatementExample.pls");
        ASTInput input = parsePLSQL(code);
        List<ASTUpdateStatement> updateStatements = input.findDescendantsOfType(ASTUpdateStatement.class);
        Assert.assertEquals(2, updateStatements.size());
        Assert.assertEquals(2, updateStatements.get(1).getFirstChildOfType(ASTUpdateSetClause.class)
                .findChildrenOfType(ASTColumn.class).size());
    }
}
