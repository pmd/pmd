/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.style;

import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserTrigger;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.ast.Node;

public class AvoidLogicInTriggerRule extends AbstractApexRule {

	@Override
	public Object visit(ASTBlockStatement node, Object data) {
		if (insideTrigger(node)) {
			addViolation(data, node);
		}

		return data;
	}

	private boolean insideTrigger(ApexNode<?> node) {
		Node n = node.jjtGetParent();

		while (n != null) {
			if (n instanceof ASTUserTrigger) {
				return true;
			}
			n = n.jjtGetParent();
		}

		return false;
	}
}
