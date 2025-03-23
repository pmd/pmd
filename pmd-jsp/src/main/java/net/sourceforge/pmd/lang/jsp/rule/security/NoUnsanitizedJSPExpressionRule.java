/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule.security;

import net.sourceforge.pmd.lang.jsp.ast.ASTElExpression;
import net.sourceforge.pmd.lang.jsp.ast.ASTElement;
import net.sourceforge.pmd.lang.jsp.rule.AbstractJspRule;

/**
 * This rule detects unsanitized JSP Expressions (can lead to Cross Site
 * Scripting (XSS) attacks)
 *
 * @author maxime_robert
 */
public class NoUnsanitizedJSPExpressionRule extends AbstractJspRule {
    @Override
    public Object visit(ASTElExpression node, Object data) {
        if (elOutsideTaglib(node)) {
            asCtx(data).addViolation(node);
        }

        return super.visit(node, data);
    }

    private boolean elOutsideTaglib(ASTElExpression node) {
        ASTElement parentASTElement = node.ancestors(ASTElement.class).first();

        boolean elInTaglib = parentASTElement != null && parentASTElement.getName() != null
                && parentASTElement.getName().contains(":");

        boolean elWithFnEscapeXml = node.getContent() != null && node.getContent().matches("^fn:escapeXml\\(.+\\)$");

        return !elInTaglib && !elWithFnEscapeXml;
    }

}
