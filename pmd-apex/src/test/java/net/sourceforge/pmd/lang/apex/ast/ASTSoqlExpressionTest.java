/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTSoqlExpressionTest extends ApexParserTestBase {

    @Test
    public void testQuery() {
        ApexNode<?> root = parse("class Foo { void test1() { Account acc = [SELECT col FROM Account]; } }");
        ASTSoqlExpression soqlExpression = root.descendants(ASTSoqlExpression.class).firstOrThrow();
        Assert.assertEquals("SELECT col FROM Account", soqlExpression.getQuery());
    }
}
