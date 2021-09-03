/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class MultipleDDLStatementsTest extends AbstractPLSQLParserTst {

    @Test
    public void parseDDLCommands() throws Exception {
        ASTInput input = plsql.parseResource("DDLCommands.sql");
        List<ASTDDLCommand> ddlcommands = input.findDescendantsOfType(ASTDDLCommand.class);
        Assert.assertEquals(6, ddlcommands.size());
        List<ASTComment> comments = input.findDescendantsOfType(ASTComment.class);
        Assert.assertEquals(5, comments.size());
        Assert.assertEquals("'abbreviated job title'", comments.get(0).getFirstChildOfType(ASTStringLiteral.class).getImage());
    }
}
