/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.ast;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.junit.Test;
public class JspPageStyleTest extends AbstractJspNodesTst {

    /**
     * Test parsing of a JSP comment.
     */
    @Test
    public void testComment() {
        Set comments = getNodes(ASTJspComment.class, JSP_COMMENT);
        assertEquals("One comment expected!", 1, comments.size());
        ASTJspComment comment = (ASTJspComment) comments.iterator().next();
        assertEquals("Correct comment content expected!", "some comment", comment.getImage());
    }

    /**
     * Test parsing a JSP directive.
     */
    @Test
    public void testDirective() {
        Set nodes = getNodes(null, JSP_DIRECTIVE);

        Set<ASTJspDirective> directives = getNodesOfType(ASTJspDirective.class, nodes);
        assertEquals("One directive expected!", 1, directives.size());
        ASTJspDirective directive = directives.iterator().next();
        assertEquals("Correct directive name expected!",
                "page", directive.getName());

        Set<ASTJspDirectiveAttribute> directiveAttrs = getNodesOfType(ASTJspDirectiveAttribute.class, nodes);
        assertEquals("Two directive attributes expected!", 2, directiveAttrs.size());

        List<ASTJspDirectiveAttribute> attrsList = new ArrayList<ASTJspDirectiveAttribute>(directiveAttrs);
        Collections.sort(attrsList, new Comparator<ASTJspDirectiveAttribute>() {
            public int compare(ASTJspDirectiveAttribute arg0, ASTJspDirectiveAttribute arg1) {
                return arg0.getName().compareTo(arg1.getName());
            }
        });

        ASTJspDirectiveAttribute attr = attrsList.get(0);
        assertEquals("Correct directive attribute name expected!",
                "language", attr.getName());
        assertEquals("Correct directive attribute value expected!",
                "java", attr.getValue());

        attr = attrsList.get(1);
        assertEquals("Correct directive attribute name expected!",
                "session", attr.getName());
        assertEquals("Correct directive attribute value expected!",
                "true", attr.getValue());


    }

    /**
     * Test parsing of a JSP declaration.
     */
    @Test
    public void testDeclaration() {
        Set declarations = getNodes(ASTJspDeclaration.class, JSP_DECLARATION);
        assertEquals("One declaration expected!", 1, declarations.size());
        ASTJspDeclaration declaration = (ASTJspDeclaration) declarations.iterator().next();
        assertEquals("Correct declaration content expected!",
                "String someString = \"s\";", declaration.getImage());
    }

    /**
     * Test parsing of a JSP scriptlet.
     */
    @Test
    public void testScriptlet() {
        Set scriptlets = getNodes(ASTJspScriptlet.class, JSP_SCRIPTLET);
        assertEquals("One scriptlet expected!", 1, scriptlets.size());
        ASTJspScriptlet scriptlet = (ASTJspScriptlet) scriptlets.iterator().next();
        assertEquals("Correct scriptlet content expected!",
                "someString = someString + \"suffix\";", scriptlet.getImage());
    }

    /**
     * Test parsing of a JSP expression.
     */
    @Test
    public void testExpression() {
        Set expressions = getNodes(ASTJspExpression.class, JSP_EXPRESSION);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTJspExpression expression = (ASTJspExpression) expressions.iterator().next();
        assertEquals("Correct expression content expected!",
                "someString", expression.getImage());
    }

    /**
     * Test parsing of a JSP expression in an attribute.
     */
    @Test
    public void testExpressionInAttribute() {
        Set expressions = getNodes(ASTJspExpressionInAttribute.class,
                JSP_EXPRESSION_IN_ATTRIBUTE);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTJspExpressionInAttribute expression = (ASTJspExpressionInAttribute) expressions.iterator().next();
        assertEquals("Correct expression content expected!",
                "style.getClass()", expression.getImage());
    }

    /**
     * Test parsing of a EL expression.
     */
    @Test
    public void testElExpression() {
        Set expressions = getNodes(ASTElExpression.class, JSP_EL_EXPRESSION);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = (ASTElExpression) expressions.iterator().next();
        assertEquals("Correct expression content expected!",
                "myBean.get(\"${ World }\")", expression.getImage());
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    public void testElExpressionInAttribute() {
        Set expressions = getNodes(ASTElExpression.class, JSP_EL_EXPRESSION_IN_ATTRIBUTE);
        assertEquals("One expression expected!", 1, expressions.size());
        ASTElExpression expression = (ASTElExpression) expressions.iterator().next();
        assertEquals("Correct expression content expected!",
                "myValidator.find(\"'jsp'\")", expression.getImage());
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    public void testJsfValueBinding() {
        Set valueBindings = getNodes(ASTValueBinding.class, JSF_VALUE_BINDING);
        assertEquals("One value binding expected!", 1, valueBindings.size());
        ASTValueBinding valueBinding = (ASTValueBinding) valueBindings.iterator().next();
        assertEquals("Correct expression content expected!",
                "myValidator.find(\"'jsf'\")", valueBinding.getImage());
    }

    private static final String JSP_COMMENT
            = "<html> <%-- some comment --%> </html>";

    private static final String JSP_DIRECTIVE
            = "<html> <%@ page language=\"java\" session='true'%> </html>";

    private static final String JSP_DECLARATION
            = "<html><%! String someString = \"s\"; %></html>";

    private static final String JSP_SCRIPTLET
            = "<html> <% someString = someString + \"suffix\"; %> </html>";

    private static final String JSP_EXPRESSION
            = "<html><head><title> <%= someString %> </title></head></html>";

    private static final String JSP_EXPRESSION_IN_ATTRIBUTE
            = "<html> <body> <p class='<%= style.getClass() %>'> Hello </p> </body> </html>";

    private static final String JSP_EL_EXPRESSION
            = "<html><title>Hello ${myBean.get(\"${ World }\") } .jsp</title></html>";

    private static final String JSP_EL_EXPRESSION_IN_ATTRIBUTE
            = "<html> <f:validator type=\"get('type').${myValidator.find(\"'jsp'\")}\" /> </html>";

    private static final String JSF_VALUE_BINDING
            = "<html> <body> <p class='#{myValidator.find(\"'jsf'\")}'> Hello </p> </body> </html>";

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JspPageStyleTest.class);
    }
}
