/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static java.util.Arrays.asList;
import static net.sourceforge.pmd.properties.PropertyFactory.enumProperty;
import static net.sourceforge.pmd.util.CollectionUtil.associateBy;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalClassStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.util.StringUtil.CaseConvention;

public class AvoidReassigningLoopVariablesRule extends AbstractJavaRulechainRule {

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
        super(ASTForStatement.class, ASTForeachStatement.class);
        definePropertyDescriptor(FOREACH_REASSIGN);
        definePropertyDescriptor(FOR_REASSIGN);
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
        NodeStream<ASTVariableDeclaratorId> loopVars = JavaRuleUtil.getLoopVariables(loopStmt);
        if (behavior == ForReassignOption.DENY) {
            for (ASTVariableDeclaratorId loopVar : loopVars) {
                for (ASTNamedReferenceExpr usage : loopVar.getUsages()) {
                    if (usage.getAccessType() == AccessType.WRITE) {
                        if (update != null && usage.ancestors(ASTForUpdate.class).first() == update) {
                            continue;
                        }
                        addViolation(data, usage, loopVar.getName());
                    }
                }
            }
        } else {
            Set<String> loopVarNames = loopVars.collect(Collectors.mapping(ASTVariableDeclaratorId::getName, Collectors.toSet()));
            Set<String> labels = JavaRuleUtil.getStatementLabels(loopStmt);
            new ControlFlowCtx(false, loopVarNames, (RuleContext) data, labels, false, false).roamStatementsForExit(loopStmt.getBody());
        }
        return null;
    }

    class ControlFlowCtx {

        private final boolean guarded;
        private boolean mayExit;
        private final Set<String> loopVarNames;
        private final RuleContext ruleCtx;

        private final Set<String> outerLoopNames;
        private final boolean breakHidden;
        private final boolean continueHidden;

        ControlFlowCtx(boolean guarded, Set<String> loopVarNames, RuleContext ctx, Set<String> outerLoopNames, boolean breakHidden, boolean continueHidden) {
            this.guarded = guarded;
            this.loopVarNames = loopVarNames;
            this.ruleCtx = ctx;
            this.outerLoopNames = outerLoopNames;
            this.breakHidden = breakHidden;
            this.continueHidden = continueHidden;
        }

        ControlFlowCtx withGuard(boolean isGuarded) {
            return copy(isGuarded, breakHidden, continueHidden);
        }

        ControlFlowCtx copy(boolean isGuarded, boolean breakHidden, boolean continueHidden) {
            return new ControlFlowCtx(isGuarded, loopVarNames, ruleCtx, outerLoopNames, breakHidden, continueHidden);
        }


        private boolean roamStatementsForExit(JavaNode node) {
            if (node == null) {
                return false;
            }

            NodeStream<? extends JavaNode> unwrappedBlock =
                node instanceof ASTBlock
                ? ((ASTBlock) node).toStream()
                : NodeStream.of(node);

            return roamStatementsForExit(unwrappedBlock);
        }

        // return true if any statement may exit the outer loop abruptly
        // This way increments of variables are allowed if they are guarded by a conditional
        private boolean roamStatementsForExit(NodeStream<? extends JavaNode> stmts) {
            for (JavaNode stmt : stmts) {
                if (stmt instanceof ASTThrowStatement
                    || stmt instanceof ASTReturnStatement) {
                    return true;
                } else if (stmt instanceof ASTBreakStatement) {
                    String label = ((ASTBreakStatement) stmt).getLabel();
                    return label != null && outerLoopNames.contains(label) || !breakHidden;
                } else if (stmt instanceof ASTContinueStatement) {
                    String label = ((ASTContinueStatement) stmt).getLabel();
                    return label != null && outerLoopNames.contains(label) || !continueHidden;
                }

                // note that we mean to use |= and not shortcut evaluation

                if (stmt instanceof ASTLoopStatement) {

                    ASTStatement body = ((ASTLoopStatement) stmt).getBody();
                    for (JavaNode child : stmt.children()) {
                        if (child != body) {
                            checkVorViolations(child);
                        }
                    }

                    mayExit |= copy(true, true, true).roamStatementsForExit(body);

                } else if (stmt instanceof ASTSwitchStatement) {

                    ASTSwitchStatement switchStmt = (ASTSwitchStatement) stmt;
                    checkVorViolations(switchStmt.getTestedExpression());

                    mayExit |= copy(true, true, false).roamStatementsForExit(switchStmt.getBranches());

                } else if (stmt instanceof ASTIfStatement) {

                    ASTIfStatement ifStmt = (ASTIfStatement) stmt;
                    checkVorViolations(ifStmt.getCondition());
                    mayExit |= withGuard(true).roamStatementsForExit(ifStmt.getThenBranch());
                    mayExit |= withGuard(this.guarded).roamStatementsForExit(ifStmt.getElseBranch());

                }
                // these two catch-all clauses implement other statements & eg switch branches
                else if (stmt instanceof ASTExpression) {
                    checkVorViolations(stmt);
                } else if (!(stmt instanceof ASTLocalClassStatement)) {
                    mayExit |= roamStatementsForExit(stmt.children());
                }
            }
            return mayExit;
        }

        private void checkVorViolations(JavaNode node) {
            if (node == null) {
                return;
            }
            node.descendants(ASTNamedReferenceExpr.class)
                .filter(it -> loopVarNames.contains(it.getName()))
                .filter(it -> (guarded || mayExit) ? JavaRuleUtil.isVarAccessStrictlyWrite(it)
                                                   : JavaRuleUtil.isVarAccessReadAndWrite(it))
                .forEach(it -> addViolation(ruleCtx, it, it.getName()));
        }
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
