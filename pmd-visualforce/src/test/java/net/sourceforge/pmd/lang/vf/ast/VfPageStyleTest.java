/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class VfPageStyleTest extends AbstractVfNodesTest {

    /**
     * Test parsing of a EL expression.
     */
    @Test
    public void testElExpression() {
        List<ASTElExpression> expressions = vf.getNodes(ASTElExpression.class, VF_EL_EXPRESSION);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = expressions.iterator().next();
        ASTExpression exp = expression.getFirstChildOfType(ASTExpression.class);
        ASTIdentifier id = exp.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("Correct expression content expected!", "myBean", id.getImage());
        ASTDotExpression dot = exp.getFirstChildOfType(ASTDotExpression.class);
        ASTIdentifier dotid = dot.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("Correct expression content expected!", "get", dotid.getImage());
        ASTArguments arguments = exp.getFirstChildOfType(ASTArguments.class);
        ASTExpression innerExpression = arguments.getFirstChildOfType(ASTExpression.class);
        ASTLiteral literal = innerExpression.getFirstChildOfType(ASTLiteral.class);
        assertEquals("Correct expression content expected!", "\"{! World }\"", literal.getImage());
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    public void testElExpressionInAttribute() {
        List<ASTElExpression> expressions = vf.getNodes(ASTElExpression.class, VF_EL_EXPRESSION_IN_ATTRIBUTE);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = expressions.iterator().next();
        ASTExpression exp = expression.getFirstChildOfType(ASTExpression.class);
        ASTIdentifier id = exp.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("Correct expression content expected!", "myValidator", id.getImage());
        ASTDotExpression dot = exp.getFirstChildOfType(ASTDotExpression.class);
        ASTIdentifier dotid = dot.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("Correct expression content expected!", "find", dotid.getImage());
        ASTArguments arguments = exp.getFirstChildOfType(ASTArguments.class);
        ASTExpression innerExpression = arguments.getFirstChildOfType(ASTExpression.class);
        ASTLiteral literal = innerExpression.getFirstChildOfType(ASTLiteral.class);
        assertEquals("Correct expression content expected!", "\"'vf'\"", literal.getImage());
    }

    private static final String VF_EL_EXPRESSION = "<html><title>Hello {!myBean.get(\"{! World }\") } .vf</title></html>";

    private static final String VF_EL_EXPRESSION_IN_ATTRIBUTE = "<html> <f:validator type=\"get('type').{!myValidator.find(\"'vf'\")}\" /> </html>";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(VfPageStyleTest.class);
    }
}
