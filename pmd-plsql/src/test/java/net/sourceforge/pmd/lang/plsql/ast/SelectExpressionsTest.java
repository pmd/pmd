/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class SelectExpressionsTest extends AbstractPLSQLParserTst {

    @Test
    void parseSelectExpression() {
        plsql.parseResource("SelectExpressions.pls");
    }

    @Test
    void parseSelectSimpleExpression() {
        ASTInput input = plsql.parseResource("SelectSimpleExpression.pls");
        assertNotNull(input);

        List<ASTSimpleExpression> simpleExpressions = input.findDescendantsOfType(ASTSimpleExpression.class);
        assertEquals(1, simpleExpressions.size());
        ASTSimpleExpression exp = simpleExpressions.get(0);
        assertEquals("e.first_name", exp.getImage());
        assertEquals(2, exp.getNumChildren());
        assertEquals(ASTTableName.class, exp.getChild(0).getClass());
        assertEquals(ASTColumn.class, exp.getChild(1).getClass());
    }

    @Test
    void parseSelectCount() {
        plsql.parseResource("SelectCount.pls");
    }

    @Test
    void parseSelectSubqueryExpression() {
        plsql.parseResource("SelectSubqueryExpressions.pls");
    }
}
