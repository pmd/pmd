/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectExpressionsTest extends AbstractPLSQLParserTst {

    @Test
    public void parseSelectExpression() {
        plsql.parseResource("SelectExpressions.pls");
    }

    @Test
    public void parseSelectSimpleExpression() {
        ASTInput input = plsql.parseResource("SelectSimpleExpression.pls");
        Assert.assertNotNull(input);

        List<ASTSimpleExpression> simpleExpressions = input.findDescendantsOfType(ASTSimpleExpression.class);
        Assert.assertEquals(1, simpleExpressions.size());
        ASTSimpleExpression exp = simpleExpressions.get(0);
        Assert.assertEquals("e.first_name", exp.getImage());
        Assert.assertEquals(2, exp.jjtGetNumChildren());
        Assert.assertEquals(ASTTableName.class, exp.jjtGetChild(0).getClass());
        Assert.assertEquals(ASTColumn.class, exp.jjtGetChild(1).getClass());
    }

    @Test
    public void parseSelectCount() {
        plsql.parseResource("SelectCount.pls");
    }

    @Test
    public void parseSelectSubqueryExpression() {
        plsql.parseResource("SelectSubqueryExpressions.pls");
    }
}
