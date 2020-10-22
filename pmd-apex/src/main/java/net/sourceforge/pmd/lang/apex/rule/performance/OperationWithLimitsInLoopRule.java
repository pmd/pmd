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
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoqlExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTSoslExpression;
import net.sourceforge.pmd.lang.apex.rule.internal.Helper;

/**
 * Warn users when code that could trigger governor limits is executing within a looping construct.
 */
public class OperationWithLimitsInLoopRule extends AbstractAvoidNodeInLoopsRule {
    public OperationWithLimitsInLoopRule() {
        setProperty(CODECLIMATE_CATEGORIES, "Performance");
        // Note: Often more complicated as just moving a few lines.
        // Involves Maps...
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);

        // DML
        addRuleChainVisit(ASTDmlDeleteStatement.class);
        addRuleChainVisit(ASTDmlInsertStatement.class);
        addRuleChainVisit(ASTDmlMergeStatement.class);
        addRuleChainVisit(ASTDmlUndeleteStatement.class);
        addRuleChainVisit(ASTDmlUpdateStatement.class);
        addRuleChainVisit(ASTDmlUpsertStatement.class);
        // Database methods
        addRuleChainVisit(ASTMethodCallExpression.class);
        // SOQL
        addRuleChainVisit(ASTSoqlExpression.class);
        // SOSL
        addRuleChainVisit(ASTSoslExpression.class);
    }

    // Begin DML Statements
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
    // End DML Statements

    // Begin Database method invocations
    @Override
    public Object visit(ASTMethodCallExpression node, Object data) {
        if (Helper.isAnyDatabaseMethodCall(node)) {
            return checkForViolation(node, data);
        } else {
            return data;
        }
    }
    // End Database method invocations

    // Begin SOQL method invocations
    @Override
    public Object visit(ASTSoqlExpression node, Object data) {
        return checkForViolation(node, data);
    }
    // End SOQL method invocations

    // Begin SOSL method invocations
    @Override
    public Object visit(ASTSoslExpression node, Object data) {
        return checkForViolation(node, data);
    }
    // End SOSL method invocations
}
