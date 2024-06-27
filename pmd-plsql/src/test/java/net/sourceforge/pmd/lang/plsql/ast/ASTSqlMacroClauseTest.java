/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class ASTSqlMacroClauseTest extends AbstractPLSQLParserTst {

    @Test
    void testEmpty() {
        ASTInput input = plsql.parseResource("SqlMacroClause.pls");

        List<ASTSqlMacroClause> sqlMacros = input.descendants(ASTSqlMacroClause.class).toList();
        assertFalse(sqlMacros.isEmpty());
        assertEquals("TABLE", sqlMacros.get(0).getType());
        assertEquals("SCALAR", sqlMacros.get(1).getType());
        assertEquals("SCALAR", sqlMacros.get(2).getType());
        assertEquals("TABLE", sqlMacros.get(3).getType());

        //assertEquals("=", conditions.get(0).getOperator());
    }
}
