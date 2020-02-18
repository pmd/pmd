/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.junit.Assert;
import org.junit.Test;

public class ASTFunctionNodeTest extends EcmascriptParserTestBase {

    @Test
    public void testGetBody() {
        ASTAstRoot node = js.parse("function foo() { var a = 'a'; }");
        ASTFunctionNode fn = node.getFirstDescendantOfType(ASTFunctionNode.class);
        Assert.assertFalse(fn.isClosure());
        EcmascriptNode<?> body = fn.getBody();
        Assert.assertTrue(body instanceof ASTBlock);
    }

    @Test
    public void testGetBodyFunctionClosureExpression() {
        ASTAstRoot node = js18.parse("(function(x) x*x)");
        ASTFunctionNode fn = node.getFirstDescendantOfType(ASTFunctionNode.class);
        Assert.assertTrue(fn.isClosure());
        EcmascriptNode<?> body = fn.getBody();
        Assert.assertTrue(body instanceof ASTBlock);
        Assert.assertTrue(body.getChild(0) instanceof ASTReturnStatement);
    }
}
