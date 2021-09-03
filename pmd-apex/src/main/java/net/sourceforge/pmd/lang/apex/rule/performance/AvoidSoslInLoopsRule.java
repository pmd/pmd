/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;

/**
 * @deprecated use {@link OperationWithLimitsInLoopRule}
 */
@Deprecated
public class AvoidSoslInLoopsRule extends AbstractAvoidNodeInLoopsRule {

    @Override
    public Object visit(ASTSoslExpression node, Object data) {
        return checkForViolation(node, data);
    }
}
