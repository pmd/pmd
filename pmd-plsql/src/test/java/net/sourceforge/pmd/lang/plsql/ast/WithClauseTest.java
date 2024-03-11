/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class WithClauseTest extends AbstractPLSQLParserTst {

    @Test
    void testSelect() {
        ASTInput input = plsql.parseResource("WithClause.pls");

        List<ASTSelectStatement> selectStatements = input.descendants(ASTSelectStatement.class).toList();
        assertEquals(1, selectStatements.size());

        assertNotNull(selectStatements.get(0).descendants(ASTWithClause.class).first());
        assertNotNull(selectStatements.get(0).descendants(ASTSelectList.class).first());
    }

    @Test
    void testSelectInto() {
        ASTInput input = plsql.parseResource("WithClause.pls");

        List<ASTSelectIntoStatement> selectStatements = input.descendants(ASTSelectIntoStatement.class).toList();
        assertEquals(1, selectStatements.size());

        assertNotNull(selectStatements.get(0).descendants(ASTWithClause.class).first());
        assertNotNull(selectStatements.get(0).descendants(ASTSelectList.class).first());
    }
}
