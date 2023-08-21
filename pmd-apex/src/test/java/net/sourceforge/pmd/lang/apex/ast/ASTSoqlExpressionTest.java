/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ASTSoqlExpressionTest extends ApexParserTestBase {

    @Test
    void testQuery() {
        ApexNode<?> root = parse("class Foo { void test1() { Account acc = [SELECT col FROM Account]; } }");
        ASTSoqlExpression soqlExpression = root.descendants(ASTSoqlExpression.class).firstOrThrow();
        assertEquals("SELECT col FROM Account", soqlExpression.getQuery());
    }
}
