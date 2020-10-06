/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;

public class AvoidSoqlInLoopsRule extends AbstractAvoidNodeInLoopsRule {
    public AvoidSoqlInLoopsRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving the SOQL a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        return checkForViolation(node, data);
    }
}
