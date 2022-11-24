/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class JspPageStyleTest extends AbstractJspNodesTst {

    /**
     * Test parsing of a JSP comment.
     */
    @Test
    void testComment() {
        List<ASTJspComment> comments = jsp.getNodes(ASTJspComment.class, JSP_COMMENT);
        assertEquals(1, comments.size(), "One comment expected!");
        ASTJspComment comment = comments.iterator().next();
        assertEquals("some comment", comment.getImage(), "Correct comment content expected!");
    }

    /**
     * Test parsing a JSP directive.
     */
    @Test
    void testDirective() {
        ASTCompilationUnit root = jsp.parse(JSP_DIRECTIVE);

        List<ASTJspDirective> directives = root.findDescendantsOfType(ASTJspDirective.class);
        assertEquals(1, directives.size(), "One directive expected!");
        ASTJspDirective directive = directives.iterator().next();
        assertEquals("page", directive.getName(), "Correct directive name expected!");

        List<ASTJspDirectiveAttribute> directiveAttrs = root.findDescendantsOfType(ASTJspDirectiveAttribute.class);
        assertEquals(2, directiveAttrs.size(), "Two directive attributes expected!");

        ASTJspDirectiveAttribute attr = directiveAttrs.get(0);
        assertEquals("language", attr.getName(), "Correct directive attribute name expected!");
        assertEquals("java", attr.getValue(), "Correct directive attribute value expected!");

        attr = directiveAttrs.get(1);
        assertEquals("session", attr.getName(), "Correct directive attribute name expected!");
        assertEquals("true", attr.getValue(), "Correct directive attribute value expected!");

    }

    /**
     * Test parsing of a JSP declaration.
     */
    @Test
    void testDeclaration() {
        List<ASTJspDeclaration> declarations = jsp.getNodes(ASTJspDeclaration.class, JSP_DECLARATION);
        assertEquals(1, declarations.size(), "One declaration expected!");
        ASTJspDeclaration declaration = declarations.iterator().next();
        assertEquals("String someString = \"s\";", declaration.getImage(), "Correct declaration content expected!");
    }

    /**
     * Test parsing of a JSP scriptlet.
     */
    @Test
    void testScriptlet() {
        List<ASTJspScriptlet> scriptlets = jsp.getNodes(ASTJspScriptlet.class, JSP_SCRIPTLET);
        assertEquals(1, scriptlets.size(), "One scriptlet expected!");
        ASTJspScriptlet scriptlet = scriptlets.iterator().next();
        assertEquals("someString = someString + \"suffix\";",
                scriptlet.getImage(), "Correct scriptlet content expected!");
    }

    /**
     * Test parsing of a JSP expression.
     */
    @Test
    void testExpression() {
        List<ASTJspExpression> expressions = jsp.getNodes(ASTJspExpression.class, JSP_EXPRESSION);
        assertEquals(1, expressions.size(), "One expression expected!");
        ASTJspExpression expression = expressions.iterator().next();
        assertEquals("someString", expression.getImage(), "Correct expression content expected!");
    }

    /**
     * Test parsing of a JSP expression in an attribute.
     */
    @Test
    void testExpressionInAttribute() {
        List<ASTJspExpressionInAttribute> expressions = jsp.getNodes(ASTJspExpressionInAttribute.class, JSP_EXPRESSION_IN_ATTRIBUTE);
        assertEquals(1, expressions.size(), "One expression expected!");
        ASTJspExpressionInAttribute expression = expressions.iterator().next();
        assertEquals("style.getClass()", expression.getImage(), "Correct expression content expected!");
    }

    /**
     * Test parsing of a EL expression.
     */
    @Test
    void testElExpression() {
        List<ASTElExpression> expressions = jsp.getNodes(ASTElExpression.class, JSP_EL_EXPRESSION);
        assertEquals(1, expressions.size(), "One expression expected!");
        ASTElExpression expression = expressions.iterator().next();
        assertEquals("myBean.get(\"${ World }\")", expression.getImage(), "Correct expression content expected!");
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    void testElExpressionInAttribute() {
        List<ASTElExpression> expressions = jsp.getNodes(ASTElExpression.class, JSP_EL_EXPRESSION_IN_ATTRIBUTE);
        assertEquals(1, expressions.size(), "One expression expected!");
        ASTElExpression expression = expressions.iterator().next();
        assertEquals("myValidator.find(\"'jsp'\")", expression.getImage(), "Correct expression content expected!");
    }

    /**
     * Test parsing of a EL expression in an attribute.
     */
    @Test
    void testJsfValueBinding() {
        List<ASTValueBinding> valueBindings = jsp.getNodes(ASTValueBinding.class, JSF_VALUE_BINDING);
        assertEquals(1, valueBindings.size(), "One value binding expected!");
        ASTValueBinding valueBinding = valueBindings.iterator().next();
        assertEquals("myValidator.find(\"'jsf'\")", valueBinding.getImage(), "Correct expression content expected!");
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
}
