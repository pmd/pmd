/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.security;

import apex.jorje.semantic.ast.modifier.OldModifiers.ModifierType;
import apex.jorje.semantic.symbol.type.ModifierOrAnnotationTypeInfo;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Finds Apex class that do not define sharing
 * 
 * @author sergey.gorbaty
 */
public class ApexSharingViolationsRule extends AbstractApexRule {

	public ApexSharingViolationsRule() {
		setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
		setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
		setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
	}

	@Override
	public Object visit(ASTUserClass node, Object data) {
		if (!Helper.isTestMethodOrClass(node)) {
			checkForSharingDeclaration(node, data);
		}
		return data;
	}

	/**
	 * Check if class has no sharing declared
	 * 
	 * @param node
	 * @param data
	 */
	private void checkForSharingDeclaration(ApexNode<?> node, Object data) {
		final boolean foundAnyDMLorSOQL = Helper.foundAnyDML(node) && Helper.foundAnySOQLorSOSL(node);
		boolean sharingFound = false;

		for (ModifierOrAnnotationTypeInfo type : node.getNode().getDefiningType().getModifiers().all()) {
			if (type.getBytecodeName().equalsIgnoreCase(ModifierType.WithoutSharing.toString())) {
				sharingFound = true;
			}
			if (type.getBytecodeName().equalsIgnoreCase(ModifierType.WithSharing.toString())) {
				sharingFound = true;
			}

		}

		if (!sharingFound && !Helper.isTestMethodOrClass(node) && foundAnyDMLorSOQL) {
			addViolation(data, node);
		}
	}

}
