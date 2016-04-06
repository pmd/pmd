/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.rule.design.ExcessiveLengthRule;

/**
 * This rule detects when a method exceeds a certain threshold. i.e. if a method
 * has more than x lines of code.
 */
public class ExcessiveMethodLengthRule extends ExcessiveLengthRule {
	public ExcessiveMethodLengthRule() {
		super(ASTMethod.class);
		setProperty(MINIMUM_DESCRIPTOR, 100d);
	}

	@Override
	public Object visit(ASTMethod node, Object data) {
		if (node.getImage().matches("<clinit>|<init>|clone")) {
			return data;
		}
		return super.visit(node, data);
	}
}
