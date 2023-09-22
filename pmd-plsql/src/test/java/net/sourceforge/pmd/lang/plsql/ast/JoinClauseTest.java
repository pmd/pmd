/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class JoinClauseTest extends AbstractPLSQLParserTst {

    @Test
    void testInnerCrossJoin() {
        ASTInput input = plsql.parseResource("InnerCrossJoin.pls");
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        assertEquals(1, joins.size());
        assertTrue(joins.get(0).isCross());
        assertFalse(joins.get(0).isNatural());
    }

    @Test
    void testInnerNaturalJoin() {
        ASTInput input = plsql.parseResource("InnerNaturalJoin.pls");
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        assertEquals(2, joins.size());
        assertFalse(joins.get(0).isCross());
        assertTrue(joins.get(0).isNatural());
    }

    @Test
    void testInnerJoinUsing() {
        ASTInput input = plsql.parseResource("InnerJoinUsing.pls");
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        assertEquals(3, joins.size());
        assertFalse(joins.get(0).isCross());
        assertFalse(joins.get(0).isNatural());
        List<ASTColumn> columns = joins.get(0).findChildrenOfType(ASTColumn.class);
        assertEquals(1, columns.size());
        assertEquals("department_id", columns.get(0).getImage());
    }

    @Test
    void testOuterJoinUsing() {
        ASTInput input = plsql.parseResource("OuterJoinUsing.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        assertEquals(ASTOuterJoinType.Type.FULL, type.getType());
        List<ASTColumn> columns = joins.get(0).findChildrenOfType(ASTColumn.class);
        assertEquals(1, columns.size());
        assertEquals("department_id", columns.get(0).getImage());
    }

    @Test
    void testRightOuterJoin() {
        ASTInput input = plsql.parseResource("RightOuterJoin.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        assertEquals(2, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
    }

    @Test
    void testLeftOuterJoin() {
        ASTInput input = plsql.parseResource("LeftOuterJoin.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        assertEquals(2, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        assertEquals(ASTOuterJoinType.Type.LEFT, type.getType());

        List<ASTSelectStatement> selects = input.findDescendantsOfType(ASTSelectStatement.class);
        assertEquals(2, selects.size());
        assertTrue(selects.get(0).getFromClause().getChild(0) instanceof ASTJoinClause);
        assertTrue(selects.get(1).getFromClause().getChild(0) instanceof ASTJoinClause);
    }

    @Test
    void testNaturalRightOuterJoin() {
        ASTInput input = plsql.parseResource("NaturalRightOuterJoin.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
        assertTrue(joins.get(0).isNatural());
    }

    @Test
    void testOuterJoinPartitioned() {
        ASTInput input = plsql.parseResource("OuterJoinPartitioned.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
        assertNotNull(joins.get(0).getFirstChildOfType(ASTQueryPartitionClause.class));
    }

    @Test
    void testFullOuterJoin() {
        plsql.parseResource("FullOuterJoin.pls");
    }

    @Test
    void testInnerJoinSubquery() {
        plsql.parseResource("InnerJoinSubquery.pls");
    }

    @Test
    void testJoinOperator() {
        ASTInput input = plsql.parseResource("JoinOperator.pls");
        List<ASTOuterJoinExpression> expressions = input.findDescendantsOfType(ASTOuterJoinExpression.class);
        assertEquals(4, expressions.size());
        assertEquals("h.opp_id", expressions.get(3).getImage());
    }
}
