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
        String code = loadTestResource("CursorAttributes.pls");
        ASTInput input = parsePLSQL(code);
        ASTExpression exp = input.getFirstDescendantOfType(ASTIfStatement.class).getFirstChildOfType(ASTExpression.class);
        Assert.assertEquals("TestSearch%notfound", exp.getImage());
    }

    @Test
    public void parseImplicitCursorAttributeBulkExceptions() {
        String code = loadTestResource("CursorAttributesBulkExceptions.pls");
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }

}
