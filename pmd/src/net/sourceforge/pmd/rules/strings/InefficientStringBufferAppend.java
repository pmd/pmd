/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlockStatement;
import net.sourceforge.pmd.ast.ASTLiteral;

/*
 * This rule finds the following:
 * <pre>
 * StringBuffer.append("c"); // appends a single character
 * </pre>
 * It is preferable to use
 * StringBuffer.append('c'); // appends a single character
 * Implementation of PMD RFE 1373863 
 */
public class InefficientStringBufferAppend extends AbstractRule {

	public Object visit(ASTLiteral node, Object data) {
		ASTBlockStatement bs = (ASTBlockStatement) node.getFirstParentOfType(ASTBlockStatement.class);
		if (bs == null) {
			return data;
		}

		String str = node.getImage();
		if (str != null && str.length() == 3 && str.charAt(0) == '\"'
				&& str.charAt(2) == '"') {
			if (!InefficientStringBuffering.isInStringBufferAppend(node, 10)) {
				return data;
			}
			addViolation(data, node);
		}
		return data;
	}
}
