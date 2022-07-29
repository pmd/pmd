/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class SelectIntoWithGroupByTest extends AbstractPLSQLParserTst {

    @Test
    void testExample1() {
        ASTInput input = plsql.parseResource("SelectIntoWithGroupBy1.pls");
        ASTGroupByClause groupByClause = input.getFirstDescendantOfType(ASTGroupByClause.class);
        assertNotNull(groupByClause);
    }

    @Test
    void testExample2() {
        ASTInput input = plsql.parseResource("SelectIntoWithGroupBy2.pls");
        ASTGroupByClause groupByClause = input.getFirstDescendantOfType(ASTGroupByClause.class);
        assertNotNull(groupByClause);
    }

    @Test
    void testExample3WithCube() {
        ASTInput input = plsql.parseResource("SelectIntoWithGroupBy3.pls");
        ASTRollupCubeClause cubeClause = input.getFirstDescendantOfType(ASTRollupCubeClause.class);
        assertNotNull(cubeClause);
        assertEquals("CUBE", cubeClause.getImage());
    }

    @Test
    void testExample4WithGroupingSets() {
        ASTInput input = plsql.parseResource("SelectIntoWithGroupBy4.pls");
        ASTGroupingSetsClause groupingSetsClause = input.getFirstDescendantOfType(ASTGroupingSetsClause.class);
        assertNotNull(groupingSetsClause);

        List<ASTFromClause> fromClauses = input.findDescendantsOfType(ASTFromClause.class);
        assertEquals(1, fromClauses.size());
        assertEquals(5, fromClauses.get(0).getNumChildren());
    }
}
