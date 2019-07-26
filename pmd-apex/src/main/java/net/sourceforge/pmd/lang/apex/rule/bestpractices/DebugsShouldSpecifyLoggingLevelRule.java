/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexUnitTestRule;

public class DebugsShouldSpecifyLoggingLevelRule extends AbstractApexUnitTestRule {

	private static final String SYSTEM_DEBUG = "System.debug";

	@Override
	public Object visit(ASTMethodCallExpression node, Object data) {
		String methodName = node.getFullMethodName();

		if (SYSTEM_DEBUG.equalsIgnoreCase(methodName) && node.jjtGetNumChildren() == 2) {
			addViolationWithMessage(data, node,
				"''{0}'' should specify the logging level.",
				new Object[] { SYSTEM_DEBUG });
		}
		return data;
	}
}