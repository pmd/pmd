/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.rule.basic;

import net.sourceforge.pmd.lang.jsp.ast.ASTElExpression;
import net.sourceforge.pmd.lang.jsp.ast.ASTElement;
import net.sourceforge.pmd.lang.jsp.rule.AbstractJspRule;

/**
 * This rule detects unsanitized JSP Expressions (can lead to Cross Site Scripting (XSS) attacks)
 *
 * @author maxime_robert
 */
public class NoUnsanitizedJSPExpressionRule extends AbstractJspRule {
	/**
	 * Reference to the parent node (unavailable in the AST?).
	 * TODO: find a way to access parent node from the AST and then remove this attribute
	 */
	private ASTElement lastNode;

	/**
	 * TODO: remove this method if the parent node is available from the AST
	 */
	@Override
	public Object visit(ASTElement node, Object data) {
		lastNode = node;
		return super.visit(node, data);
	}

	@Override
	public Object visit(ASTElExpression node, Object data) {
		if (elOutsideTaglib(node)) {
			addViolation(data, node);
		}

		return super.visit(node, data);
	}

	private boolean elOutsideTaglib(ASTElExpression node) {
		// TODO: add the case of "${fn:escapeXml()} (in which a taglib isn't required)
		boolean elInTaglib = lastNode != null && lastNode.getName() != null && lastNode.getName().contains(":");

		// Node parentTagNode = node.getFirstParentOfType(ASTElement.class);
		return !elInTaglib;
	}

}
