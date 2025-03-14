/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class MultipleDDLStatementsTest extends AbstractPLSQLParserTst {

    @Test
    void parseDDLCommands() throws Exception {
        ASTInput input = plsql.parseResource("DDLCommands.sql");
        List<ASTDDLCommand> ddlcommands = input.descendants(ASTDDLCommand.class).toList();
        assertEquals(6, ddlcommands.size());
        List<ASTComment> comments = input.descendants(ASTComment.class).toList();
        assertEquals(5, comments.size());
        assertEquals("'abbreviated job title'", comments.get(0).firstChild(ASTStringLiteral.class).getImage());
    }
}
