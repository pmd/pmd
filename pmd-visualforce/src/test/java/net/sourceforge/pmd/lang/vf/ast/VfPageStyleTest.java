/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.Assert.assertEquals;
import java.util.Set;
import org.junit.Test;

import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;

public class VfPageStyleTest extends AbstractVfNodesTest {

    /**
     * Test parsing of a EL expression.
     */
    @Test
    public void testElExpression() {
        Set<ASTElExpression> expressions = getNodes(ASTElExpression.class, VF_EL_EXPRESSION);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = expressions.iterator().next();
        assertEquals("Correct expression content expected!", "myBean.get(\"{! World }\")", expression.getImage());
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    public void testElExpressionInAttribute() {
        Set<ASTElExpression> expressions = getNodes(ASTElExpression.class, VF_EL_EXPRESSION_IN_ATTRIBUTE);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = expressions.iterator().next();
        assertEquals("Correct expression content expected!", "myValidator.find(\"'vf'\")", expression.getImage());
    }

    private static final String VF_EL_EXPRESSION = "<html><title>Hello {!myBean.get(\"{! World }\") } .jsp</title></html>";

    private static final String VF_EL_EXPRESSION_IN_ATTRIBUTE = "<html> <f:validator type=\"get('type').{!myValidator.find(\"'vf'\")}\" /> </html>";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(VfPageStyleTest.class);
    }
}
