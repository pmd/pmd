/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.security;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Flags dangerous method calls, e.g. FinancialForce
 * Configuration.disableTriggerCRUDSecurity()
 * 
 * 
 * @author sergey.gorbaty
 *
 */
public class ApexDangerousMethodsRule extends AbstractApexRule {
	private static final String DISABLE_CRUD = "disableTriggerCRUDSecurity";
	private static final String CONFIGURATION = "Configuration";

	public ApexDangerousMethodsRule() {
		setProperty(CODECLIMATE_CATEGORIES, new String[] { "Security" });
		setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 100);
		setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);

	}

	public Object visit(ASTUserClass node, Object data) {
		if (Helper.isTestMethodOrClass(node)) {
			return data;
		}

		List<ASTMethodCallExpression> methodCalls = node.findDescendantsOfType(ASTMethodCallExpression.class);
		for (ASTMethodCallExpression methodCall : methodCalls) {
			if (Helper.isMethodName(methodCall, CONFIGURATION, DISABLE_CRUD)) {
				addViolation(data, methodCall);
			}
		}

		return data;
	}

}
