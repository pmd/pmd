/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

public class WhereClauseTest extends AbstractPLSQLParserTst {

    @Test
    public void testFunctionCall() {
        ASTInput input = plsql.parseResource("WhereClauseFunctionCall.pls");
        List<ASTSelectIntoStatement> selectStatements = input.findDescendantsOfType(ASTSelectIntoStatement.class);
        Assert.assertEquals(4, selectStatements.size());

        ASTFunctionCall functionCall = selectStatements.get(0).getFirstDescendantOfType(ASTFunctionCall.class);
        Assert.assertEquals("UPPER", functionCall.getImage());

        ASTFunctionCall functionCall2 = selectStatements.get(2).getFirstDescendantOfType(ASTFunctionCall.class);
        Assert.assertEquals("utils.get_colname", functionCall2.getImage());
    }

    @Test
    public void testLikeCondition() {
        plsql.parseResource("WhereClauseLike.pls");
    }

    @Test
    public void testNullCondition() {
        plsql.parseResource("WhereClauseIsNull.pls");
    }

    @Test
    public void testBetweenCondition() {
        plsql.parseResource("WhereClauseBetween.pls");
    }

    @Test
    public void testInCondition() {
        plsql.parseResource("WhereClauseIn.pls");
    }

    @Test
    public void testIsOfTypeCondition() {
        plsql.parseResource("WhereClauseIsOfType.pls");
    }

    @Test
    public void testConcatenationOperator() {
        plsql.parseResource("WhereClauseConcatenation.pls");
    }

    @Test
    public void testExistsCondition() {
        plsql.parseResource("WhereClauseExists.pls");
    }

    @Test
    public void testMultisetCondition() {
        plsql.parseResource("WhereClauseMultiset.pls");
    }

    @Test
    public void testRegexpLikeCondition() {
        ASTInput input = plsql.parseResource("WhereClauseRegexpLike.pls");
        List<ASTRegexpLikeCondition> regexps = input.findDescendantsOfType(ASTRegexpLikeCondition.class);
        Assert.assertEquals(3, regexps.size());
        Assert.assertEquals("last_name", regexps.get(1).getSourceChar().getImage());
        Assert.assertEquals("'([aeiou])\\1'", regexps.get(1).getPattern().getImage());
        Assert.assertEquals("'i'", regexps.get(1).getMatchParam());
    }

    @Test
    public void testSubqueries() {
        plsql.parseResource("WhereClauseSubqueries.pls");
    }

    @Test
    public void testParentheses() {
        plsql.parseResource("WhereClauseParens.pls");
    }

    @Test
    public void testCurrentOf() {
        plsql.parseResource("WhereCurrentOf.pls");
    }
}
