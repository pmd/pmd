/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class VfPageStyleTest extends AbstractVfTest {

    /**
     * Test parsing of a EL expression.
     */
    @Test
    void testElExpression() {
        List<ASTElExpression> expressions = vf.getNodes(ASTElExpression.class, VF_EL_EXPRESSION);
        assertEquals(1, expressions.size(), "One expression expected!");
        ASTElExpression expression = expressions.iterator().next();
        ASTExpression exp = expression.getFirstChildOfType(ASTExpression.class);
        ASTIdentifier id = exp.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("myBean", id.getImage(), "Correct expression content expected!");
        ASTDotExpression dot = exp.getFirstChildOfType(ASTDotExpression.class);
        ASTIdentifier dotid = dot.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("get", dotid.getImage(), "Correct expression content expected!");
        ASTArguments arguments = exp.getFirstChildOfType(ASTArguments.class);
        ASTExpression innerExpression = arguments.getFirstChildOfType(ASTExpression.class);
        ASTLiteral literal = innerExpression.getFirstChildOfType(ASTLiteral.class);
        assertEquals("\"{! World }\"", literal.getImage(), "Correct expression content expected!");
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    void testElExpressionInAttribute() {
        List<ASTElExpression> expressions = vf.getNodes(ASTElExpression.class, VF_EL_EXPRESSION_IN_ATTRIBUTE);
        assertEquals(1, expressions.size(), "One expression expected!");
        ASTElExpression expression = expressions.iterator().next();
        ASTExpression exp = expression.getFirstChildOfType(ASTExpression.class);
        ASTIdentifier id = exp.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("myValidator", id.getImage(), "Correct expression content expected!");
        ASTDotExpression dot = exp.getFirstChildOfType(ASTDotExpression.class);
        ASTIdentifier dotid = dot.getFirstChildOfType(ASTIdentifier.class);
        assertEquals("find", dotid.getImage(), "Correct expression content expected!");
        ASTArguments arguments = exp.getFirstChildOfType(ASTArguments.class);
        ASTExpression innerExpression = arguments.getFirstChildOfType(ASTExpression.class);
        ASTLiteral literal = innerExpression.getFirstChildOfType(ASTLiteral.class);
        assertEquals("\"'vf'\"", literal.getImage(), "Correct expression content expected!");
    }

    private static final String VF_EL_EXPRESSION = "<html><title>Hello {!myBean.get(\"{! World }\") } .vf</title></html>";

    private static final String VF_EL_EXPRESSION_IN_ATTRIBUTE = "<html> <f:validator type=\"get('type').{!myValidator.find(\"'vf'\")}\" /> </html>";
}
