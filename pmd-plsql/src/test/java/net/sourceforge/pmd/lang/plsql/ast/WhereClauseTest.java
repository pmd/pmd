/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class WhereClauseTest extends AbstractPLSQLParserTst {

    @Test
    void testFunctionCall() {
        ASTInput input = plsql.parseResource("WhereClauseFunctionCall.pls");
        List<ASTSelectIntoStatement> selectStatements = input.findDescendantsOfType(ASTSelectIntoStatement.class);
        assertEquals(4, selectStatements.size());

        ASTFunctionCall functionCall = selectStatements.get(0).getFirstDescendantOfType(ASTFunctionCall.class);
        assertEquals("UPPER", functionCall.getImage());

        ASTFunctionCall functionCall2 = selectStatements.get(2).getFirstDescendantOfType(ASTFunctionCall.class);
        assertEquals("utils.get_colname", functionCall2.getImage());
    }

    @Test
    void testLikeCondition() {
        plsql.parseResource("WhereClauseLike.pls");
    }

    @Test
    void testNullCondition() {
        plsql.parseResource("WhereClauseIsNull.pls");
    }

    @Test
    void testBetweenCondition() {
        plsql.parseResource("WhereClauseBetween.pls");
    }

    @Test
    void testInCondition() {
        plsql.parseResource("WhereClauseIn.pls");
    }

    @Test
    void testIsOfTypeCondition() {
        plsql.parseResource("WhereClauseIsOfType.pls");
    }

    @Test
    void testConcatenationOperator() {
        plsql.parseResource("WhereClauseConcatenation.pls");
    }

    @Test
    void testExistsCondition() {
        plsql.parseResource("WhereClauseExists.pls");
    }

    @Test
    void testMultisetCondition() {
        plsql.parseResource("WhereClauseMultiset.pls");
    }

    @Test
    void testRegexpLikeCondition() {
        ASTInput input = plsql.parseResource("WhereClauseRegexpLike.pls");
        List<ASTRegexpLikeCondition> regexps = input.findDescendantsOfType(ASTRegexpLikeCondition.class);
        assertEquals(3, regexps.size());
        assertEquals("last_name", regexps.get(1).getSourceChar().getImage());
        assertEquals("'([aeiou])\\1'", regexps.get(1).getPattern().getImage());
        assertEquals("'i'", regexps.get(1).getMatchParam());
    }

    @Test
    void testSubqueries() {
        plsql.parseResource("WhereClauseSubqueries.pls");
    }

    @Test
    void testParentheses() {
        plsql.parseResource("WhereClauseParens.pls");
    }

    @Test
    void testCurrentOf() {
        plsql.parseResource("WhereCurrentOf.pls");
    }
}
