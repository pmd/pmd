/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTSoqlExpressionTest extends ApexParserTestBase {

    @Test
    public void testQuery() {
        ApexNode<?> node = parse("class Foo { void test1() { Account acc = [SELECT col FROM Account]; } }");
        ASTSoqlExpression soqlExpression = node.getFirstDescendantOfType(ASTSoqlExpression.class);
        Assert.assertEquals("SELECT col FROM Account", soqlExpression.getQuery());
    }
}
