/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class SelectIntoWithGroupByTest extends AbstractPLSQLParserTst {

    @Test
    public void testExample1() {
        ASTInput input = plsql.parseResource("SelectIntoWithGroupBy1.pls");
        ASTGroupByClause groupByClause = input.getFirstDescendantOfType(ASTGroupByClause.class);
        Assert.assertNotNull(groupByClause);
    }

    @Test
    public void testExample2() {
        ASTInput input = plsql.parseResource("SelectIntoWithGroupBy2.pls");
        ASTGroupByClause groupByClause = input.getFirstDescendantOfType(ASTGroupByClause.class);
        Assert.assertNotNull(groupByClause);
    }

    @Test
    public void testExample3WithCube() {
        ASTInput input = plsql.parseResource("SelectIntoWithGroupBy3.pls");
        ASTRollupCubeClause cubeClause = input.getFirstDescendantOfType(ASTRollupCubeClause.class);
        Assert.assertNotNull(cubeClause);
        Assert.assertEquals("CUBE", cubeClause.getImage());
    }

    @Test
    public void testExample4WithGroupingSets() {
        ASTInput input = plsql.parseResource("SelectIntoWithGroupBy4.pls");
        ASTGroupingSetsClause groupingSetsClause = input.getFirstDescendantOfType(ASTGroupingSetsClause.class);
        Assert.assertNotNull(groupingSetsClause);

        List<ASTFromClause> fromClauses = input.findDescendantsOfType(ASTFromClause.class);
        Assert.assertEquals(1, fromClauses.size());
        Assert.assertEquals(5, fromClauses.get(0).getNumChildren());
    }
}
