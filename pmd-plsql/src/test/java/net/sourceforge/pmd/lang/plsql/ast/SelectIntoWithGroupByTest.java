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

public class SelectIntoWithGroupByTest extends AbstractPLSQLParserTst {

    @Test
    public void testExample1() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoWithGroupBy1.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        ASTGroupByClause groupByClause = input.getFirstDescendantOfType(ASTGroupByClause.class);
        Assert.assertNotNull(groupByClause);
    }

    @Test
    public void testExample2() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoWithGroupBy2.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        ASTGroupByClause groupByClause = input.getFirstDescendantOfType(ASTGroupByClause.class);
        Assert.assertNotNull(groupByClause);
    }

    @Test
    public void testExample3WithCube() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoWithGroupBy3.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        ASTRollupCubeClause cubeClause = input.getFirstDescendantOfType(ASTRollupCubeClause.class);
        Assert.assertNotNull(cubeClause);
        Assert.assertEquals("CUBE", cubeClause.getImage());
    }

    @Test
    public void testExample4WithGroupingSets() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("SelectIntoWithGroupBy4.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        ASTGroupingSetsClause groupingSetsClause = input.getFirstDescendantOfType(ASTGroupingSetsClause.class);
        Assert.assertNotNull(groupingSetsClause);

        List<ASTFromClause> fromClauses = input.findDescendantsOfType(ASTFromClause.class);
        Assert.assertEquals(1, fromClauses.size());
        Assert.assertEquals(5, fromClauses.get(0).jjtGetNumChildren());
    }
}
