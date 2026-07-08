/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.conventionalEnumProperty;

import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForUpdate;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalClassStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.AssignmentOp;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.StringUtil.CaseConvention;

public class AvoidReassigningLoopVariablesRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<ForeachReassignOption> FOREACH_REASSIGN
        = conventionalEnumProperty("foreachReassign", ForeachReassignOption.class)
        .defaultValue(ForeachReassignOption.DENY)
        .desc("how/if foreach control variables may be reassigned")
        .build();

    private static final PropertyDescriptor<ForReassignOption> FOR_REASSIGN
        = conventionalEnumProperty("forReassign", ForReassignOption.class)
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
        ASTVariableId loopVar = loopStmt.getVarId();
        boolean ignoreNext = behavior == ForeachReassignOption.FIRST_ONLY;
        for (ASTNamedReferenceExpr usage : loopVar.getLocalUsages()) {
            if (usage.getAccessType() == AccessType.WRITE) {
                if (ignoreNext) {
                    ignoreNext = false;
                    continue;
                }
                asCtx(data).addViolation(usage, loopVar.getName());
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
        NodeStream<ASTVariableId> loopVars = JavaAstUtils.getLoopVariables(loopStmt);
        if (behavior == ForReassignOption.DENY) {
            ASTForUpdate update = loopStmt.firstChild(ASTForUpdate.class);
            for (ASTVariableId loopVar : loopVars) {
                for (ASTNamedReferenceExpr usage : loopVar.getLocalUsages()) {
                    if (usage.getAccessType() == AccessType.WRITE) {
                        if (update != null && usage.ancestors(ASTForUpdate.class).first() == update) {
                            continue;
                        }
                        asCtx(data).addViolation(usage, loopVar.getName());
                    }
                }
            }
        } else {
            Set<String> loopVarNames = loopVars.collect(Collectors.mapping(ASTVariableId::getName, Collectors.toSet()));
            Set<String> labels = JavaAstUtils.getStatementLabels(loopStmt);
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
                            checkForViolations(child);
                        }
                    }

                    mayExit |= copy(true, true, true).roamStatementsForExit(body);

                } else if (stmt instanceof ASTSwitchStatement) {

                    ASTSwitchStatement switchStmt = (ASTSwitchStatement) stmt;
                    checkForViolations(switchStmt.getTestedExpression());

                    mayExit |= copy(true, true, false).roamStatementsForExit(switchStmt.getBranches());

                } else if (stmt instanceof ASTIfStatement) {

                    ASTIfStatement ifStmt = (ASTIfStatement) stmt;
                    checkForViolations(ifStmt.getCondition());
                    mayExit |= withGuard(true).roamStatementsForExit(ifStmt.getThenBranch());
                    mayExit |= withGuard(this.guarded).roamStatementsForExit(ifStmt.getElseBranch());
                } else if (stmt instanceof ASTExpression) { // these two catch-all clauses implement other statements & eg switch branches
                    checkForViolations(stmt);
                } else if (!(stmt instanceof ASTLocalClassStatement)) {
                    mayExit |= roamStatementsForExit(stmt.children());
                }
            }
            return mayExit;
        }

        private void checkForViolations(JavaNode node) {
            if (node == null) {
                return;
            }
            final boolean onlyConsiderWrite = guarded || mayExit;
            node.descendants(ASTNamedReferenceExpr.class)
                .filter(it -> loopVarNames.contains(it.getName()))
                .filter(it -> onlyConsiderWrite ? it.getAccessType() == AccessType.WRITE && !isSimpleSkipOperation(it)
                                                : JavaAstUtils.isVarAccessReadAndWrite(it))
                .forEach(it -> asCtx(ruleCtx).addViolation(it, it.getName()));
        }

        private boolean isSimpleSkipOperation(ASTNamedReferenceExpr expr) {
            if (expr.getAccessType() != AccessType.WRITE) {
                return false;
            }
            
            if (expr.getParent() instanceof ASTAssignmentExpression) {
                // Check for simple assignment operations: i += 1, i -= 1, i = i + 1, i = i - 1
                ASTAssignmentExpression assignment = (ASTAssignmentExpression) expr.getParent();
                if (expr.getIndexInParent() == 0) { // expr is the left side of assignment
                    return isSimpleSkipAssignment(assignment, expr.getName());
                }
            } else {
                // Check for unary increment/decrement: i++, ++i, i--, --i
                return JavaAstUtils.isVarAccessReadAndWrite(expr);
            }
            
            return false;
        }

        private boolean isSimpleSkipAssignment(ASTAssignmentExpression assignment, String varName) {
            if (assignment.getOperator().isCompound()) {
                // Only += 1 and -= 1 are allowed as compound assignments
                AssignmentOp op = assignment.getOperator();
                ASTExpression rhs = assignment.getRightOperand();
                return (op == AssignmentOp.ADD_ASSIGN || op == AssignmentOp.SUB_ASSIGN) && isLiteralOne(rhs);
            }
            
            if (assignment.getOperator() == AssignmentOp.ASSIGN) {
                // Check for i = i + 1 or i = i - 1
                ASTExpression rhs = assignment.getRightOperand();
                return isSimpleIncrementExpression(rhs, varName);
            }
            
            return false;
        }

        private boolean isLiteralOne(ASTExpression expr) {
            // Check if expression is the literal "1"
            return expr.isCompileTimeConstant()
                    && expr.getConstValue() instanceof Number
                    && ((Number) expr.getConstValue()).intValue() == 1;
        }

        private boolean isSimpleIncrementExpression(ASTExpression expr, String varName) {
            // Check for patterns: i + 1, i - 1, 1 + i
            if (expr instanceof ASTInfixExpression) {
                ASTInfixExpression infixExpr = (ASTInfixExpression) expr;
                BinaryOp operator = infixExpr.getOperator();
                
                if (operator == BinaryOp.ADD || operator == BinaryOp.SUB) {
                    ASTExpression left = infixExpr.getLeftOperand();
                    ASTExpression right = infixExpr.getRightOperand();
                    
                    // Check i + 1 or i - 1
                    if (isVariableReference(left, varName) && isLiteralOne(right)) {
                        return true;
                    }
                    
                    // Check 1 + i (only for addition)
                    if (operator == BinaryOp.ADD && isLiteralOne(left) && isVariableReference(right, varName)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean isVariableReference(ASTExpression expr, String varName) {
            return expr instanceof ASTNamedReferenceExpr && ((ASTNamedReferenceExpr) expr).getName().equals(varName);
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
        ALLOW
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
