/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;

/**
 * @deprecated use {@link OperationWithLimitsInLoopRule}
 */
@Deprecated
public class AvoidSoqlInLoopsRule extends AbstractAvoidNodeInLoopsRule {

    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        return checkForViolation(node, data);
    }
}
