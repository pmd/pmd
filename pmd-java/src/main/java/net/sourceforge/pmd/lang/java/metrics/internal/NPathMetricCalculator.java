/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.internal;

import java.util.Set;
import java.util.function.BiFunction;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnonymousClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLoopStatement;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchArrowBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLike;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.ASTYieldStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaVisitorBase;
import net.sourceforge.pmd.lang.java.ast.ReturnScopeNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;

public final class NPathMetricCalculator {

    public static long computeNpath(ReturnScopeNode node) {
        ASTBlock body = node.getBody();
        if (body == null) {
            return 1;
        }
        return computeNpath(body);
    }

    public static long computeNpath(ASTBlock node) {
        CfVisitState state = new CfVisitState(1);
        state = node.acceptVisitor(CfVisitor.INSTANCE, state);
        return state.getNumPathsToExit();
    }

    private static DecisionPoint getControlFlowInCondition(ASTExpression e, CfPoint point) {
        return e.acceptVisitor(CfExpressionVisitor.INSTANCE, point);
    }

    static final class CfExpressionVisitor extends JavaVisitorBase<CfPoint, DecisionPoint> {

        static final NPathMetricCalculator.CfExpressionVisitor INSTANCE = new CfExpressionVisitor();

        @Override
        public DecisionPoint visitJavaNode(JavaNode node, CfPoint point) {
            for (JavaNode child : node.children()) {
                point = child.acceptVisitor(this, point).endPaths();
            }
            return point;
        }

        @Override
        public DecisionPoint visit(ASTAnonymousClassDeclaration node, CfPoint point) {
            return point; // stop recursion.
        }

        @Override
        public DecisionPoint visit(ASTLambdaExpression node, CfPoint point) {
            return point; // stop recursion.
        }

        @Override
        public DecisionPoint visit(ASTConditionalExpression node, CfPoint point) {
            DecisionPoint condition = node.getCondition().acceptVisitor(this, point);
            DecisionPoint thenState = node.getThenBranch().acceptVisitor(this, condition.truePoint());
            DecisionPoint elseState = node.getElseBranch().acceptVisitor(this, condition.falsePoint());
            return new BooleanDecisionPoint(
                    thenState.endPaths().connectTo(new CfPoint(elseState.endPaths())),
                    thenState.truePoint().connectTo(new CfPoint(elseState.truePoint())),
                    thenState.falsePoint().connectTo(new CfPoint(elseState.falsePoint()))
            );
        }


        @Override
        public DecisionPoint visit(ASTUnaryExpression node, CfPoint point) {
            if (JavaAstUtils.isBooleanNegation(node)) {
                DecisionPoint condition = node.getOperand().acceptVisitor(this, point);
                return condition.negate();
            }
            return super.visit(node, point);
        }

        @Override
        public DecisionPoint visit(ASTInfixExpression node, CfPoint point) {
            if (node.getOperator() == BinaryOp.CONDITIONAL_AND) {
                // a && b
                // b is only visited if a is true
                DecisionPoint leftState = node.getLeftOperand().acceptVisitor(this, point);
                DecisionPoint rightState = node.getRightOperand().acceptVisitor(this, new CfPoint(leftState.truePoint()));

                return new BooleanDecisionPoint(
                        leftState.endPaths().connectTo(new CfPoint(rightState.endPaths())),
                        rightState.truePoint(),
                        rightState.falsePoint().connectTo(leftState.falsePoint())
                );
            } else if (node.getOperator() == BinaryOp.CONDITIONAL_OR) {
                // a || b
                // b is only visited if a is false
                DecisionPoint leftState = node.getLeftOperand().acceptVisitor(this, point);
                DecisionPoint rightState = node.getRightOperand().acceptVisitor(this, new CfPoint(leftState.falsePoint()));

                return new BooleanDecisionPoint(
                        leftState.endPaths().connectTo(new CfPoint(rightState.endPaths())),
                        rightState.truePoint().connectTo(leftState.truePoint()),
                        rightState.falsePoint()
                );
            } else {
                // other ops have only a linear path from left to right
                return super.visit(node, point);
            }
        }
    }

    interface DecisionPoint {
        /**
         * This is the total number of paths that lead out of the decision.
         * It is not the same as the sum of the true paths and false paths
         * as that sum would be double-counting some paths.
         */
        CfPoint endPaths();

        /**
         * The point that counts the number of CF-paths that lead to a
         * "true" result. For instance in {@code a || b}, there are two
         * paths that lead to a true result.
         */
        CfPoint truePoint();

        /**
         * The point that counts the number of CF-paths that lead to a
         * "false" result. For instance in {@code a && b}, there are two
         * paths that lead to a false result.
         */
        CfPoint falsePoint();


        DecisionPoint negate();
    }

    static final class BooleanDecisionPoint implements DecisionPoint {
        CfPoint pointLeadingToThisDecision;
        final CfPoint truePoint;
        final CfPoint falsePoint;

        BooleanDecisionPoint(CfPoint pred, CfPoint truePoint, CfPoint falsePoint) {
            this.pointLeadingToThisDecision = pred;
            this.truePoint = truePoint;
            this.falsePoint = falsePoint;
        }

        @Override
        public CfPoint truePoint() {
            return truePoint;
        }

        @Override
        public CfPoint falsePoint() {
            return falsePoint;
        }

        @Override
        public CfPoint endPaths() {
            return pointLeadingToThisDecision;
        }

        @Override
        public DecisionPoint negate() {
            return new BooleanDecisionPoint(pointLeadingToThisDecision, falsePoint, truePoint);
        }
    }

    static final class CfVisitor extends JavaVisitorBase<CfVisitState, CfVisitState> {

        static final CfVisitor INSTANCE = new CfVisitor();

        @Override
        protected CfVisitState visitChildren(Node node, CfVisitState state) {
            for (int i = 0, numChildren = node.getNumChildren(); i < numChildren; i++) {
                state = node.getChild(i).acceptVisitor(this, state);
            }
            return state;
        }

        @Override
        public CfVisitState visit(ASTBlock node, CfVisitState state) {
            return visitChildren(node, state);
        }

        @Override
        public CfVisitState visit(ASTReturnStatement node, CfVisitState state) {
            if (node.getExpr() != null) {
                state = node.getExpr().acceptVisitor(this, state);
            }
            return state.abruptCompletion(state.returnPoint);
        }

        @Override
        public CfVisitState visit(ASTThrowStatement node, CfVisitState state) {
            state = node.getExpr().acceptVisitor(this, state);
            return state.abruptCompletion(state.throwPoint);
        }

        @Override
        public CfVisitState visit(ASTBreakStatement node, CfVisitState state) {
            return state.abruptCompletion(state.getBreakPoint(node.getLabel()));
        }

        @Override
        public CfVisitState visit(ASTYieldStatement node, CfVisitState state) {
            state = node.getExpr().acceptVisitor(this, state);
            return state.abruptCompletion(state.yieldPoint);
        }

        @Override
        public CfVisitState visit(ASTContinueStatement node, CfVisitState state) {
            return state.abruptCompletion(state.getContinuePoint(node.getLabel()));
        }

        @Override
        public CfVisitState visit(ASTIfStatement node, CfVisitState data) {
            return handleLabelsForRegularStmt(node, data, (stmt, state) -> {
                DecisionPoint condition = getControlFlowInCondition(stmt.getCondition(), state.currentProgramPoint);
                CfVisitState thenState = stmt.getThenBranch().acceptVisitor(this, state.fork(condition.truePoint()));
                if (stmt.getElseBranch() != null) {
                    CfVisitState elseState = stmt.getElseBranch().acceptVisitor(this, state.fork(condition.falsePoint()));
                    return thenState.absorb(elseState.currentProgramPoint);
                } else {
                    return thenState.absorb(condition.falsePoint());
                }
            });
        }


        private CfVisitState visitSwitch(ASTSwitchLike switchLike, CfVisitState state) {
            CfVisitState startState = switchLike.getTestedExpression().acceptVisitor(this, state);

            final long numPathsToStart = startState.currentProgramPoint.numPathsUntilThisPoint;
            CfVisitState currentFallthroughState = startState.fork(new CfPoint(0));
            for (ASTSwitchBranch n : switchLike) {
                CfPoint thisBranch = new CfPoint(numPathsToStart * JavaAstUtils.numAlternatives(n));
                if (n instanceof ASTSwitchFallthroughBranch) {
                    currentFallthroughState.absorb(thisBranch);

                    NodeStream<ASTStatement> statements = ((ASTSwitchFallthroughBranch) n).getStatements();
                    currentFallthroughState = statements.reduce(currentFallthroughState, (point, stmt) -> stmt.acceptVisitor(this, point));

                } else if (n instanceof ASTSwitchArrowBranch) {
                    CfVisitState branchState = startState.fork(thisBranch);
                    branchState = ((ASTSwitchArrowBranch) n).getRightHandSide().acceptVisitor(this, branchState);
                    CfPoint exitPoint = switchLike instanceof ASTSwitchExpression ? branchState.yieldPoint : branchState.breakPoint;
                    branchState.abruptCompletion(exitPoint);
                }
            }

            if (!JavaAstUtils.isTotalSwitch(switchLike)) {
                // add a path for the implicit default case
                currentFallthroughState.absorb(startState.currentProgramPoint);
            }

            return currentFallthroughState;
        }

        @Override
        public CfVisitState visit(ASTSwitchStatement node, CfVisitState data) {
            return handleLabels(node, data, true, false, this::visitSwitch);
        }


        @Override
        public CfVisitState visit(ASTSwitchExpression node, CfVisitState state) {
            final CfPoint prevYield = state.yieldPoint;
            state.yieldPoint = new CfPoint(0);
            CfVisitState endState = visitSwitch(node, state);
            endState.absorb(state.yieldPoint);
            endState.yieldPoint = prevYield;
            return endState;
        }

        @Override
        public CfVisitState visit(ASTForeachStatement node, CfVisitState state) {
            return visitLoopExceptDoWhile(node, state, node.getIterableExpr(), null, node.getIterableExpr());
        }


        @Override
        public CfVisitState visit(ASTWhileStatement node, CfVisitState state) {
            return visitLoopExceptDoWhile(node, state, null, null, node.getCondition());
        }

        @Override
        public CfVisitState visit(ASTForStatement node, CfVisitState state) {
            return visitLoopExceptDoWhile(node, state, node.getInit(), node.getUpdate(), node.getCondition());
        }

        private CfVisitState visitLoopExceptDoWhile(ASTLoopStatement node, CfVisitState state, @Nullable JavaNode init, @Nullable JavaNode update, @Nullable ASTExpression conditionNode) {
            state = acceptOpt(init, state);

            DecisionPoint decision = getLoopCondition(conditionNode, state.currentProgramPoint);

            CfVisitState endState = handleLabels(node, state.fork(decision.truePoint()), true, true,
                    (loop, state2) -> {
                        state2 = loop.getBody().acceptVisitor(this, state2);
                        return acceptOpt(update, state2);
                    },
                    (afterBody, breakPoint, contPoint) -> {
                        assert contPoint != null;
                        return afterBody.absorb(breakPoint).absorb(contPoint);
                    }
            );
            if (JavaAstUtils.isUnconditionalLoop(node)) {
                return endState;
            }
            return endState.absorb(decision.falsePoint());
        }


        @Override
        public CfVisitState visit(ASTDoStatement node, CfVisitState state) {
            return handleLabels(node, state, true, true, (loop, state2) -> loop.getBody().acceptVisitor(this, state2),
                    (afterBody, breakPoint, contPoint) -> {
                        assert contPoint != null;
                        CfPoint beforeCond = afterBody.currentProgramPoint.connectTo(contPoint);
                        DecisionPoint condition = getLoopCondition(node.getCondition(), beforeCond);
                        return afterBody.absorb(condition.falsePoint()).absorb(breakPoint);
                    });
        }

        private DecisionPoint getLoopCondition(@Nullable ASTExpression condition, CfPoint point) {
            if (condition != null) {
                return getControlFlowInCondition(condition, point);
            }
            return point;
        }

        @Override
        public CfVisitState visitExpression(ASTExpression node, CfVisitState state) {
            CfPoint endPoint = getControlFlowInCondition(node, state.currentProgramPoint).endPaths();
            return state.withPoint(endPoint);
        }

        private CfVisitState acceptOpt(@Nullable JavaNode node, CfVisitState state) {
            if (node == null) {
                return state;
            }
            return node.acceptVisitor(this, state);
        }

        private static <N extends ASTStatement> CfVisitState handleLabelsForRegularStmt(N stmt, CfVisitState state,
                                                                                        BiFunction<N, CfVisitState, CfVisitState> action) {
            return handleLabels(stmt, state, false, false, action);
        }

        private static <N extends ASTStatement> CfVisitState handleLabels(N stmt, CfVisitState state,
                                                                          boolean canBreakWithoutLabel,
                                                                          boolean canContinue,
                                                                          BiFunction<N, CfVisitState, CfVisitState> action) {
            return handleLabels(stmt, state, canBreakWithoutLabel, canContinue, action,
                    (endState, breakPoint, contPoint) -> endState.absorb(breakPoint));
        }

        interface BreakAndContinueHandler {
            CfVisitState handleBreakAndContinue(CfVisitState state, CfPoint breakPoint, @Nullable CfPoint continuePoint);
        }

        private static <N extends ASTStatement> CfVisitState handleLabels(N stmt, CfVisitState state,
                                                                          boolean canBreakWithoutLabel,
                                                                          boolean canContinue,
                                                                          BiFunction<N, CfVisitState, CfVisitState> action,
                                                                          BreakAndContinueHandler callback) {
            Set<String> labels = JavaAstUtils.getStatementLabels(stmt);
            if (labels.isEmpty() && !canBreakWithoutLabel && !canContinue) {
                return action.apply(stmt, state);
            }
            final PMap<String, CfPoint> prevLabeledBreaks = state.labeledBreakPoints;
            final PMap<String, CfPoint> prevLabeledContinues = state.labeledContinuePoints;
            final CfPoint prevBreak = state.breakPoint;
            final CfPoint prevContinue = state.continuePoint;

            CfPoint breakPoint = new CfPoint(0);

            for (String label : labels) {
                state.labeledBreakPoints = state.labeledBreakPoints.plus(label, breakPoint);
            }
            if (canBreakWithoutLabel) {
                state.breakPoint = breakPoint;
            }

            CfPoint continuePoint = null;
            if (canContinue) {
                continuePoint = new CfPoint(0);
                state.continuePoint = continuePoint;
                for (String label : labels) {
                    state.labeledContinuePoints = state.labeledContinuePoints.plus(label, continuePoint);
                }
            }

            CfVisitState endState = action.apply(stmt, state);
            endState.continuePoint = prevContinue;
            endState.breakPoint = prevBreak;
            endState.labeledBreakPoints = prevLabeledBreaks;
            endState.labeledContinuePoints = prevLabeledContinues;

            return callback.handleBreakAndContinue(endState, breakPoint, continuePoint);
        }
    }


    /** A control flow point (eg, exit point of a method). */
    static final class CfPoint implements DecisionPoint {
        private long numPathsUntilThisPoint;

        CfPoint(long start) {
            this.numPathsUntilThisPoint = start;
        }

        CfPoint(CfPoint clone) {
            this.numPathsUntilThisPoint = clone.numPathsUntilThisPoint;
        }

        CfPoint connectTo(CfPoint point) {
            point.numPathsUntilThisPoint = saturatingAdd(point.numPathsUntilThisPoint, this.numPathsUntilThisPoint);
            return point;
        }

        CfPoint average(CfPoint point) {
            long halfThis = this.numPathsUntilThisPoint / 2;
            long halfThat = point.numPathsUntilThisPoint / 2;
            point.numPathsUntilThisPoint = saturatingAdd(halfThis, halfThat);
            return point;
        }

        @Override
        public String toString() {
            return numPathsUntilThisPoint + "";
        }

        @Override
        public CfPoint truePoint() {
            return this;
        }

        @Override
        public CfPoint falsePoint() {
            return this;
        }

        @Override
        public CfPoint endPaths() {
            return this;
        }

        @Override
        public DecisionPoint negate() {
            return this;
        }
    }

    static final class CfVisitState {

        // these are overridable and accumulate paths that end the current statement.

        private PMap<String, CfPoint> labeledBreakPoints;
        private PMap<String, CfPoint> labeledContinuePoints;

        private @Nullable CfPoint breakPoint;
        private @Nullable CfPoint continuePoint;
        private @Nullable CfPoint yieldPoint;

        /** Accumulate paths that end in a return. */
        private final CfPoint returnPoint;
        /** Accumulate paths that end in a throw. */
        private final CfPoint throwPoint;

        /** Stores the number of paths until the current program point we are exploring. */
        private CfPoint currentProgramPoint;

        CfVisitState(long numPathsUntilThisPoint) {
            this.labeledBreakPoints = HashTreePMap.empty();
            this.labeledContinuePoints = HashTreePMap.empty();
            this.returnPoint = new CfPoint(0);
            this.throwPoint = new CfPoint(0);
            this.currentProgramPoint = new CfPoint(numPathsUntilThisPoint);
        }

        CfVisitState(CfVisitState state, CfPoint currentProgramPoint) {
            this.labeledBreakPoints = state.labeledBreakPoints;
            this.labeledContinuePoints = state.labeledContinuePoints;
            this.breakPoint = state.breakPoint;
            this.continuePoint = state.continuePoint;
            this.yieldPoint = state.yieldPoint;
            this.returnPoint = state.returnPoint;
            this.throwPoint = state.throwPoint;
            this.currentProgramPoint = currentProgramPoint;
        }

        CfVisitState fork() {
            return fork(this.currentProgramPoint);
        }

        CfVisitState fork(CfPoint currentPoint) {
            CfPoint newPoint = new CfPoint(currentPoint.numPathsUntilThisPoint);
            return new CfVisitState(this, newPoint);
        }

        CfVisitState absorb(CfPoint point) {
            point.connectTo(currentProgramPoint);
            return this;
        }

        CfVisitState withPoint(CfPoint point) {
            this.currentProgramPoint = point;
            return this;
        }

        @Nullable
        CfPoint getBreakPoint(@Nullable String label) {
            if (label == null) {
                return breakPoint;
            }
            return labeledBreakPoints.get(label);
        }

        @Nullable
        CfPoint getContinuePoint(@Nullable String label) {
            if (label == null) {
                return continuePoint;
            }
            return labeledContinuePoints.get(label);
        }

        CfVisitState abruptCompletion(CfPoint exitPoint) {
            if (exitPoint != null) {
                // if it is null, it is because of invalid source (break to nonexistent label)
                this.currentProgramPoint.connectTo(exitPoint);
            }
            this.currentProgramPoint.numPathsUntilThisPoint = 0;
            return this;
        }

        long getNumPathsToExit() {
            return currentProgramPoint
                    .connectTo(returnPoint)
                    .connectTo(throwPoint).numPathsUntilThisPoint;
        }
    }


    static long saturatingAdd(long a, long b) {
        try {
            return Math.addExact(a, b);
        } catch (ArithmeticException e) {
            return Long.MAX_VALUE;
        }
    }

}
