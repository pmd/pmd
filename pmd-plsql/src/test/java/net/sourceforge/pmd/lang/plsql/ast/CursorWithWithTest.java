/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class CursorWithWithTest extends AbstractPLSQLParserTst {

    @Test
    public void parseCursorWithWith() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("CursorWithWith.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
        ASTCursorUnit cursor = input.getFirstDescendantOfType(ASTCursorUnit.class);
        ASTSelectStatement select = (ASTSelectStatement) cursor.jjtGetChild(1);
        ASTWithClause with = (ASTWithClause) select.jjtGetChild(0);
        ASTName queryName = (ASTName) with.jjtGetChild(0);
        Assert.assertEquals("risk_set", queryName.getImage());
    }
}
