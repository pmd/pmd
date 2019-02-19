/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectExpressionsTest extends AbstractPLSQLParserTst {

    @Test
    public void parseSelectExpression() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectExpressions.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }

    @Test
    public void parseSelectSimpleExpression() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectSimpleExpression.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
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
    public void parseSelectCount() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectCount.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }

    @Test
    public void parseSelectSubqueryExpression() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectSubqueryExpressions.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        Assert.assertNotNull(input);
    }
}
