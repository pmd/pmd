/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.performance;

import org.checkerframework.checker.nullness.qual.NonNull;

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
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Warn users when code that could trigger governor limits is executing within a looping construct.
 */
public class OperationWithLimitsInLoopRule extends AbstractAvoidNodeInLoopsRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(
                // DML
                ASTDmlDeleteStatement.class,
                ASTDmlInsertStatement.class,
                ASTDmlMergeStatement.class,
                ASTDmlUndeleteStatement.class,
                ASTDmlUpdateStatement.class,
                ASTDmlUpsertStatement.class,
                // Database methods
                ASTMethodCallExpression.class,
                // SOQL
                ASTSoqlExpression.class,
                // SOSL
                ASTSoslExpression.class
        );
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
