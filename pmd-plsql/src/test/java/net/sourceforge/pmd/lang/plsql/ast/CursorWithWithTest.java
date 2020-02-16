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
        ASTSelectStatement select = (ASTSelectStatement) cursor.getChild(1);
        ASTWithClause with = (ASTWithClause) select.getChild(0);
        ASTName queryName = (ASTName) with.getChild(0);
        Assert.assertEquals("risk_set", queryName.getImage());
    }
}
