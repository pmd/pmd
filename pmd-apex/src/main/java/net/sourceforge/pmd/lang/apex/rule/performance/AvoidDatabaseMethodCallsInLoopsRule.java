/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

public class AvoidDatabaseMethodCallsInLoopsRule extends AbstractAvoidNodeInLoopsRule {
    public AvoidDatabaseMethodCallsInLoopsRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        if (Helper.isAnyDatabaseMethodCall(node)) {
            return checkForViolation(node, data);
        } else {
            return data;
        }
    }
}
