/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ASTFunctionNodeTest extends EcmascriptParserTestBase {

    @Test
    void testGetBody() {
        ASTAstRoot node = js.parse("function foo() { var a = 'a'; }");
        ASTFunctionNode fn = node.getFirstDescendantOfType(ASTFunctionNode.class);
        assertFalse(fn.isClosure());
        EcmascriptNode<?> body = fn.getBody();
        assertTrue(body instanceof ASTBlock);
    }

    @Test
    void testGetBodyFunctionClosureExpression() {
        ASTAstRoot node = js.parse("(function(x) x*x)");
        ASTFunctionNode fn = node.getFirstDescendantOfType(ASTFunctionNode.class);
        assertTrue(fn.isClosure());
        EcmascriptNode<?> body = fn.getBody();
        assertTrue(body instanceof ASTBlock);
        assertTrue(body.getChild(0) instanceof ASTReturnStatement);
    }
}
