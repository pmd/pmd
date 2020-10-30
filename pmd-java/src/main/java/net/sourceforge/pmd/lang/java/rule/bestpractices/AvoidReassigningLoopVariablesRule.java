/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static java.util.Arrays.asList;
import static net.sourceforge.pmd.properties.PropertyFactory.enumProperty;
import static net.sourceforge.pmd.util.CollectionUtil.associateBy;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.rule.performance.AbstractOptimizationRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.StringUtil.CaseConvention;

public class AvoidReassigningLoopVariablesRule extends AbstractOptimizationRule {

    private static final Map<String, ForeachReassignOption> FOREACH_REASSIGN_VALUES =
        associateBy(asList(ForeachReassignOption.values()), ForeachReassignOption::getDisplayName);

    private static final PropertyDescriptor<ForeachReassignOption> FOREACH_REASSIGN
        = enumProperty("foreachReassign", FOREACH_REASSIGN_VALUES)
        .defaultValue(ForeachReassignOption.DENY)
        .desc("how/if foreach control variables may be reassigned")
        .build();

    private static final Map<String, ForReassignOption> FOR_REASSIGN_VALUES =
        associateBy(asList(ForReassignOption.values()), ForReassignOption::getDisplayName);

    private static final PropertyDescriptor<ForReassignOption> FOR_REASSIGN
        = enumProperty("forReassign", FOR_REASSIGN_VALUES)
        .defaultValue(ForReassignOption.DENY)
        .desc("how/if for control variables may be reassigned")
        .build();

    public AvoidReassigningLoopVariablesRule() {
        definePropertyDescriptor(FOREACH_REASSIGN);
        definePropertyDescriptor(FOR_REASSIGN);
    }

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTForStatement.class, ASTForeachStatement.class);
    }

    @Override
    public Object visit(ASTForeachStatement loopStmt, Object data) {
        ForeachReassignOption behavior = getProperty(FOREACH_REASSIGN);
        if (behavior == ForeachReassignOption.ALLOW) {
            return data;
        }
        ASTVariableDeclaratorId loopVar = loopStmt.getVarId();
        boolean ignoreNext = behavior == ForeachReassignOption.FIRST_ONLY;
        for (ASTNamedReferenceExpr usage : loopVar.getUsages()) {
            if (usage.getAccessType() == AccessType.WRITE) {
                if (ignoreNext) {
                    ignoreNext = false;
                    continue;
                }
                addViolation(data, usage, loopVar.getName());
            } else {
                ignoreNext = false;
            }
        }
        return null;
    }

    @Override
    public Object visit(ASTForStatement loopStmt, Object data) {
        ForReassignOption behavior = getProperty(FOR_REASSIGN);
        if (behavior == ForReassignOption.ALLOW) {
            return data;
        }
        ASTForUpdate update = loopStmt.getFirstChildOfType(ASTForUpdate.class);
        ASTStatement body = loopStmt.getBody();
        for (ASTVariableDeclaratorId loopVar : JavaRuleUtil.getLoopVariables(loopStmt)) {
            for (ASTNamedReferenceExpr usage : loopVar.getUsages()) {
                if (usage.getAccessType() == AccessType.WRITE) {
                    if (update != null && usage.ancestors(ASTForUpdate.class).first() == update) {
                        continue;
                    }

                    if (behavior == ForReassignOption.SKIP
                        && JavaRuleUtil.isVarAccessReadAndWrite(usage)
                        && isConditionallyGuarded(usage, loopStmt)) {
                        continue;
                    }
                    addViolation(data, usage, loopVar.getName());

                }

            }
        }
        return null;
    }

    private static boolean isConditionallyGuarded(JavaNode node, ASTLoopStatement enclosingLoop) {
        JavaNode parent = node.getParent();

        if (parent == enclosingLoop) {
            return false;
        }

        if (parent instanceof ASTLoopStatement) {
            return node == ((ASTLoopStatement) parent).getBody() || isConditionallyGuarded(parent, enclosingLoop);
        }

        if (parent instanceof ASTSwitchStatement) {
            return node.getIndexInParent() != 0 || isConditionallyGuarded(parent, enclosingLoop);
        }

        if (parent instanceof ASTIfStatement) {
            return node.getIndexInParent() != 0 || isConditionallyGuarded(parent, enclosingLoop);

//            if (node.getIndexInParent() == 0) {// condition
//                return isConditionallyGuarded(parent, enclosingLoop);
//            }
//            while (parent.getParent() instanceof ASTIfStatement) {
//                parent = parent.getParent();
//            }
//            return isConditionallyGuarded(parent, enclosingLoop);
        }

        return isConditionallyGuarded(parent, enclosingLoop);
    }

    private static boolean isConditionallyGuarded(ASTLoopStatement loop, ASTExpression expr) {
        ASTIfStatement enclosingIf = JavaRuleUtil.getIfStmtIfExprInCondition(expr);
        JavaNode previous = expr;
        for (JavaNode parent : expr.ancestors()) {
            if (parent == loop) {
                break;
            }
            if (parent instanceof ASTIfStatement) {
                if (previous instanceof ASTIfStatement && previous.getIndexInParent() == 1
                    || !(previous instanceof ASTExpression)) {
                    return true;
                }
            } else if (parent instanceof ASTSwitchStatement) {
                if (previous != parent.getFirstChild()) {
                    return true;
                }
            } else if (parent instanceof ASTLoopStatement) {
                // we didn't come from the condition expr
                if (previous == ((ASTLoopStatement) parent).getBody()) {
                    return true;
                }
            }

            previous = parent;
        }
        return false;
    }

    private enum ForeachReassignOption {
        /**
         * Deny reassigning the 'foreach' control variable
         */
        DENY,

        /**
         * Allow reassigning the 'foreach' control variable if it is the first statement in the loop body.
         */
        FIRST_ONLY,

        /**
         * Allow reassigning the 'foreach' control variable.
         */
        ALLOW;

        /**
         * The RuleDocGenerator uses toString() to determine the default value.
         *
         * @return the mapped property value instead of the enum name
         */
        @Override
        public String toString() {
            return getDisplayName();
        }

        public String getDisplayName() {
            return CaseConvention.SCREAMING_SNAKE_CASE.convertTo(CaseConvention.CAMEL_CASE, name());
        }
    }

    private enum ForReassignOption {
        /**
         * Deny reassigning a 'for' control variable.
         */
        DENY,

        /**
         * Allow skipping elements by incrementing/decrementing the 'for' control variable.
         */
        SKIP,

        /**
         * Allow reassigning the 'for' control variable.
         */
        ALLOW;

        /**
         * The RuleDocGenerator uses toString() to determine the default value.
         *
         * @return the mapped property value instead of the enum name
         */
        @Override
        public String toString() {
            return getDisplayName();
        }

        public String getDisplayName() {
            return CaseConvention.SCREAMING_SNAKE_CASE.convertTo(CaseConvention.CAMEL_CASE, name());
        }
    }

}
