/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ASTSoqlExpressionTest extends ApexParserTestBase {

    @Test
    void testQuery1() {
        ApexNode<?> root = parse("class Foo { void test1() { Account acc = [SeLeCt cOl fRoM Account where a = 1]; } }");
        ASTSoqlExpression soqlExpression = root.descendants(ASTSoqlExpression.class).firstOrThrow();
        assertEquals("SeLeCt cOl fRoM Account where a = 1", soqlExpression.getQuery());
        assertEquals("SELECT cOl FROM Account WHERE a = 1", soqlExpression.getCanonicalQuery());
    }

    @Test
    void testQuery2() {
        ApexNode<?> root = parse("class Foo { void test1() { Integer i = [select count() from Account]; } }");
        ASTSoqlExpression soqlExpression = root.descendants(ASTSoqlExpression.class).firstOrThrow();
        assertEquals("select count() from Account", soqlExpression.getQuery());
        assertEquals("SELECT COUNT() FROM Account", soqlExpression.getCanonicalQuery());
    }

    @Test
    void testQuery3() {
        ApexNode<?> root = parse("class Foo { void test1() { String name = [SELECT Name FROM Account WHERE Id = :accId and Name = :myName]; } }");
        ASTSoqlExpression soqlExpression = root.descendants(ASTSoqlExpression.class).firstOrThrow();
        assertEquals("SELECT Name FROM Account WHERE Id = :accId and Name = :myName", soqlExpression.getQuery());
        assertEquals("SELECT Name FROM Account WHERE Id = :tmpVar1 AND Name = :tmpVar2", soqlExpression.getCanonicalQuery());
    }
}
