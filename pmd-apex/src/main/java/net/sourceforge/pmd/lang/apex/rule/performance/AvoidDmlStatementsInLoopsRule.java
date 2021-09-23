/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import net.sourceforge.pmd.lang.apex.ast.ASTDmlDeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlInsertStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlMergeStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUndeleteStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpdateStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDmlUpsertStatement;

/**
 * @deprecated use {@link OperationWithLimitsInLoopRule}
 */
@Deprecated
public class AvoidDmlStatementsInLoopsRule extends AbstractAvoidNodeInLoopsRule {
    public AvoidDmlStatementsInLoopsRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving the SOQL a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    // CPD-OFF - the same visits are in the replacement rule OperationWithLimitsInLoopRule
    @Override
    public Object visit(ASTDmlDeleteStatement node, Object data) {
        return checkForViolation(node, data);
    }

    @Override
    public Object visit(ASTDmlInsertStatement node, Object data) {
        return checkForViolation(node, data);
    }

    @Override
    public Object visit(ASTDmlMergeStatement node, Object data) {
        return checkForViolation(node, data);
    }

    @Override
    public Object visit(ASTDmlUndeleteStatement node, Object data) {
        return checkForViolation(node, data);
    }

    @Override
    public Object visit(ASTDmlUpdateStatement node, Object data) {
        return checkForViolation(node, data);
    }

    @Override
    public Object visit(ASTDmlUpsertStatement node, Object data) {
        return checkForViolation(node, data);
    }
    // CPD-ON
}
