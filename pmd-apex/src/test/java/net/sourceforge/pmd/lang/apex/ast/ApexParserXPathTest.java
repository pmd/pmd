/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ApexParserXPathTest extends ApexParserTestBase {

    @Test
    public void testBooleanExpressions() throws Exception {
        ApexNode<Compilation> node = parse(IOUtils.toString(ApexParserXPathTest.class.getResourceAsStream("BooleanExpressions.cls"),
                StandardCharsets.UTF_8));
        List<ASTBooleanExpression> booleanExpressions = node.findDescendantsOfType(ASTBooleanExpression.class);
        Assert.assertEquals(2, booleanExpressions.size());
        Assert.assertEquals("&&", booleanExpressions.get(0).getOperator().toString());
        Assert.assertEquals("!=", booleanExpressions.get(1).getOperator().toString());

        List<? extends Node> xpathResult = node.findChildNodesWithXPath("//BooleanExpression[@Operator='&&']");
        Assert.assertEquals(1, xpathResult.size());
        Assert.assertSame(booleanExpressions.get(0), xpathResult.get(0));
    }
}
