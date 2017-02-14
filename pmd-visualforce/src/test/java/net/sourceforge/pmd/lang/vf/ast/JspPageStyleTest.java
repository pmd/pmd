/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.Assert.assertEquals;
import java.util.Set;
import org.junit.Test;

import net.sourceforge.pmd.lang.vf.ast.ASTElExpression;

public class JspPageStyleTest extends AbstractJspNodesTst {

    /**
     * Test parsing of a EL expression.
     */
    @Test
    public void testElExpression() {
        Set<ASTElExpression> expressions = getNodes(ASTElExpression.class, JSP_EL_EXPRESSION);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = expressions.iterator().next();
        assertEquals("Correct expression content expected!", "myBean.get(\"${ World }\")", expression.getImage());
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    public void testElExpressionInAttribute() {
        Set<ASTElExpression> expressions = getNodes(ASTElExpression.class, JSP_EL_EXPRESSION_IN_ATTRIBUTE);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = expressions.iterator().next();
        assertEquals("Correct expression content expected!", "myValidator.find(\"'jsp'\")", expression.getImage());
    }

    private static final String JSP_COMMENT = "<html> <%-- some comment --%> </html>";

    private static final String JSP_DIRECTIVE = "<html> <%@ page language=\"java\" session='true'%> </html>";

    private static final String JSP_DECLARATION = "<html><%! String someString = \"s\"; %></html>";

    private static final String JSP_SCRIPTLET = "<html> <% someString = someString + \"suffix\"; %> </html>";

    private static final String JSP_EXPRESSION = "<html><head><title> <%= someString %> </title></head></html>";

    private static final String JSP_EXPRESSION_IN_ATTRIBUTE = "<html> <body> <p class='<%= style.getClass() %>'> Hello </p> </body> </html>";

    private static final String JSP_EL_EXPRESSION = "<html><title>Hello ${myBean.get(\"${ World }\") } .jsp</title></html>";

    private static final String JSP_EL_EXPRESSION_IN_ATTRIBUTE = "<html> <f:validator type=\"get('type').${myValidator.find(\"'jsp'\")}\" /> </html>";

    private static final String JSF_VALUE_BINDING = "<html> <body> <p class='#{myValidator.find(\"'jsf'\")}'> Hello </p> </body> </html>";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JspPageStyleTest.class);
    }
}
