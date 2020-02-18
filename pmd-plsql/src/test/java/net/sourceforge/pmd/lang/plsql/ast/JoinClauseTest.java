/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class JoinClauseTest extends AbstractPLSQLParserTst {

    @Test
    public void testInnerCrossJoin() {
        ASTInput input = plsql.parseResource("InnerCrossJoin.pls");
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        Assert.assertEquals(1, joins.size());
        Assert.assertTrue(joins.get(0).isCross());
        Assert.assertFalse(joins.get(0).isNatural());
    }

    @Test
    public void testInnerNaturalJoin() {
        ASTInput input = plsql.parseResource("InnerNaturalJoin.pls");
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        Assert.assertEquals(2, joins.size());
        Assert.assertFalse(joins.get(0).isCross());
        Assert.assertTrue(joins.get(0).isNatural());
    }

    @Test
    public void testInnerJoinUsing() {
        ASTInput input = plsql.parseResource("InnerJoinUsing.pls");
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        Assert.assertEquals(3, joins.size());
        Assert.assertFalse(joins.get(0).isCross());
        Assert.assertFalse(joins.get(0).isNatural());
        List<ASTColumn> columns = joins.get(0).findChildrenOfType(ASTColumn.class);
        Assert.assertEquals(1, columns.size());
        Assert.assertEquals("department_id", columns.get(0).getImage());
    }

    @Test
    public void testOuterJoinUsing() {
        ASTInput input = plsql.parseResource("OuterJoinUsing.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.FULL, type.getType());
        List<ASTColumn> columns = joins.get(0).findChildrenOfType(ASTColumn.class);
        Assert.assertEquals(1, columns.size());
        Assert.assertEquals("department_id", columns.get(0).getImage());
    }

    @Test
    public void testRightOuterJoin() {
        ASTInput input = plsql.parseResource("RightOuterJoin.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(2, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
    }

    @Test
    public void testLeftOuterJoin() {
        ASTInput input = plsql.parseResource("LeftOuterJoin.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(2, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.LEFT, type.getType());

        List<ASTSelectStatement> selects = input.findDescendantsOfType(ASTSelectStatement.class);
        Assert.assertEquals(2, selects.size());
        Assert.assertTrue(selects.get(0).getFromClause().getChild(0) instanceof ASTJoinClause);
        Assert.assertTrue(selects.get(1).getFromClause().getChild(0) instanceof ASTJoinClause);
    }

    @Test
    public void testNaturalRightOuterJoin() {
        ASTInput input = plsql.parseResource("NaturalRightOuterJoin.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
        Assert.assertTrue(joins.get(0).isNatural());
    }

    @Test
    public void testOuterJoinPartitioned() {
        ASTInput input = plsql.parseResource("OuterJoinPartitioned.pls");
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
        Assert.assertNotNull(joins.get(0).getFirstChildOfType(ASTQueryPartitionClause.class));
    }

    @Test
    public void testFullOuterJoin() {
        plsql.parseResource("FullOuterJoin.pls");
    }

    @Test
    public void testInnerJoinSubquery() {
        plsql.parseResource("InnerJoinSubquery.pls");
    }

    @Test
    public void testJoinOperator() {
        ASTInput input = plsql.parseResource("JoinOperator.pls");
        List<ASTOuterJoinExpression> expressions = input.findDescendantsOfType(ASTOuterJoinExpression.class);
        Assert.assertEquals(4, expressions.size());
        Assert.assertEquals("h.opp_id", expressions.get(3).getImage());
    }
}
