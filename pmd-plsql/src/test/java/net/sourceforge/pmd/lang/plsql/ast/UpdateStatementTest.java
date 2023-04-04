/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class UpdateStatementTest extends AbstractPLSQLParserTst {
    @Test
    void parseUpdateStatementExample() {
        ASTInput input = plsql.parseResource("UpdateStatementExample.pls");
        List<ASTUpdateStatement> updateStatements = input.findDescendantsOfType(ASTUpdateStatement.class);
        assertEquals(2, updateStatements.size());
        assertEquals(2, updateStatements.get(1).getFirstChildOfType(ASTUpdateSetClause.class)
                .findChildrenOfType(ASTColumn.class).size());
    }

    @Test
    void parseUpdateStatementExample2() {
        ASTInput input = plsql.parseResource("UpdateStatementExample2.pls");
        assertNotNull(input);
    }

    @Test
    void parseUpdateStatementRef() {
        ASTInput input = plsql.parseResource("UpdateStatementRef.pls");
        assertNotNull(input);
    }
}
