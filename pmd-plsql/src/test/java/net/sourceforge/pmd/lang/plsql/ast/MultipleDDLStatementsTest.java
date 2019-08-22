/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class MultipleDDLStatementsTest extends AbstractPLSQLParserTst {

    @Test
    public void parseDDLCommands() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("DDLCommands.sql"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTDDLCommand> ddlcommands = input.findDescendantsOfType(ASTDDLCommand.class);
        Assert.assertEquals(3, ddlcommands.size());
    }
}
