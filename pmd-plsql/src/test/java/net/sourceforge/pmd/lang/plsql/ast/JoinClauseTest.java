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

public class JoinClauseTest extends AbstractPLSQLParserTst {

    @Test
    public void testInnerCrossJoin() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("InnerCrossJoin.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        Assert.assertEquals(1, joins.size());
        Assert.assertTrue(joins.get(0).isCross());
        Assert.assertFalse(joins.get(0).isNatural());
    }

    @Test
    public void testInnerNaturalJoin() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("InnerNaturalJoin.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        Assert.assertEquals(1, joins.size());
        Assert.assertFalse(joins.get(0).isCross());
        Assert.assertTrue(joins.get(0).isNatural());
    }

    @Test
    public void testInnerJoinUsing() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("InnerJoinUsing.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTInnerCrossJoinClause> joins = input.findDescendantsOfType(ASTInnerCrossJoinClause.class);
        Assert.assertEquals(1, joins.size());
        Assert.assertFalse(joins.get(0).isCross());
        Assert.assertFalse(joins.get(0).isNatural());
        List<ASTColumn> columns = joins.get(0).findChildrenOfType(ASTColumn.class);
        Assert.assertEquals(1, columns.size());
        Assert.assertEquals("department_id", columns.get(0).getImage());
    }

    @Test
    public void testOuterJoinUsing() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("OuterJoinUsing.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.FULL, type.getType());
        List<ASTColumn> columns = joins.get(0).findChildrenOfType(ASTColumn.class);
        Assert.assertEquals(1, columns.size());
        Assert.assertEquals("department_id", columns.get(0).getImage());
    }

    @Test
    public void testRightOuterJoin() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("RightOuterJoin.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
    }

    @Test
    public void testNaturalRightOuterJoin() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("NaturalRightOuterJoin.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
        Assert.assertTrue(joins.get(0).isNatural());
    }

    @Test
    public void testOuterJoinPartitioned() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("OuterJoinPartitioned.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
        List<ASTOuterJoinClause> joins = input.findDescendantsOfType(ASTOuterJoinClause.class);
        Assert.assertEquals(1, joins.size());
        ASTOuterJoinType type = joins.get(0).getFirstChildOfType(ASTOuterJoinType.class);
        Assert.assertEquals(ASTOuterJoinType.Type.RIGHT, type.getType());
        Assert.assertNotNull(joins.get(0).getFirstChildOfType(ASTQueryPartitionClause.class));
    }

    @Test
    public void testFullOuterJoin() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("FullOuterJoin.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }

    @Test
    public void testInnerJoinSubquery() throws Exception {
        String code = IOUtils.toString(this.getClass().getResourceAsStream("InnerJoinSubquery.pls"),
                StandardCharsets.UTF_8);
        ASTInput input = parsePLSQL(code);
    }
}
