/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class CursorAttributesTest extends AbstractPLSQLParserTst {

    @Test
    public void parseCursorWithAttribute() {
        ASTInput input = plsql.parseResource("CursorAttributes.pls");
        ASTExpression exp = input.getFirstDescendantOfType(ASTIfStatement.class).getFirstChildOfType(ASTExpression.class);
        Assert.assertEquals("TestSearch%notfound", exp.getImage());
    }

    @Test
    public void parseImplicitCursorAttributeBulkExceptions() {
        plsql.parseResource("CursorAttributesBulkExceptions.pls");
    }

}
