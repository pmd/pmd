/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.plsql.AbstractPLSQLParserTst;

class WhereClauseTest extends AbstractPLSQLParserTst {

    @Test
    void testFunctionCall() {
        doTest("WhereClauseFunctionCall");
    }

    @Test
    void testLikeCondition() {
        doTest("WhereClauseLike");
    }

    @Test
    void testNullCondition() {
        doTest("WhereClauseIsNull");
    }

    @Test
    void testBetweenCondition() {
        doTest("WhereClauseBetween");
    }

    @Test
    void testInCondition() {
        doTest("WhereClauseIn");
    }

    @Test
    void testIsOfTypeCondition() {
        doTest("WhereClauseIsOfType");
    }

    @Test
    void testConcatenationOperator() {
        doTest("WhereClauseConcatenation");
    }

    @Test
    void testExistsCondition() {
        doTest("WhereClauseExists");
    }

    @Test
    void testMultisetCondition() {
        doTest("WhereClauseMultiset");
    }

    @Test
    void testRegexpLikeCondition() {
        doTest("WhereClauseRegexpLike");
    }

    @Test
    void testSubqueries() {
        doTest("WhereClauseSubqueries");
    }

    @Test
    void testParentheses() {
        doTest("WhereClauseParens");
    }

    @Test
    void testCurrentOf() {
        doTest("WhereCurrentOf");
    }
}
