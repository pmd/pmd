/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class CursorWithWithTest extends AbstractPLSQLParserTst {

    @Test
    public void parseCursorWithWith() {
        ASTInput input = plsql.parseResource("CursorWithWith.pls");
        ASTCursorUnit cursor = input.getFirstDescendantOfType(ASTCursorUnit.class);
        ASTSelectStatement select = (ASTSelectStatement) cursor.jjtGetChild(1);
        ASTWithClause with = (ASTWithClause) select.jjtGetChild(0);
        ASTName queryName = (ASTName) with.jjtGetChild(0);
        Assert.assertEquals("risk_set", queryName.getImage());
    }
}
