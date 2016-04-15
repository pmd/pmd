/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.style;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.GLOBAL;

import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class AvoidGlobalModifierRule extends AbstractApexRule {
	
	public AvoidGlobalModifierRule() {
		setProperty(REMEDIATION_MULTIPLIER, 10);
	}

	@Override
	public Object visit(ASTUserClass node, Object data) {
		return checkForGlobal(node, data);
	}

	@Override
	public Object visit(ASTUserInterface node, Object data) {
		return checkForGlobal(node, data);
	}

	private Object checkForGlobal(ApexNode<?> node, Object data) {
		ASTModifierNode modifierNode = node.getFirstChildOfType(ASTModifierNode.class);

		if (modifierNode != null && modifierNode.getNode().getModifiers().has(GLOBAL)) {
			addViolation(data, node);
		}

		return data;
	}
}
