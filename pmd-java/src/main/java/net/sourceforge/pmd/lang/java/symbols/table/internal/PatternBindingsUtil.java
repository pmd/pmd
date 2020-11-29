/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.Collection;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTBreakStatement;
import net.sourceforge.pmd.lang.java.ast.ASTContinueStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLabeledStatement;
import net.sourceforge.pmd.lang.java.ast.ASTPattern;
import net.sourceforge.pmd.lang.java.ast.ASTPatternExpression;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeTestPattern;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.util.OptionalBool;

final class PatternBindingsUtil {

    private PatternBindingsUtil() {
        // util class
    }

    static boolean canCompleteNormally(ASTStatement stmt) {
        return completesNormally(stmt) != OptionalBool.NO;
    }

    static OptionalBool completesNormally(ASTStatement stmt) {
        State endState = completesNormallyImpl(stmt, HashTreePSet.empty());

        return OptionalBool.max(endState.exitsLocally, endState.exitsOutside).complement();
    }

    private static final class State {

        private static final State NORMAL_COMPLETION = new State(OptionalBool.NO, OptionalBool.NO);
        // just for throws & return:
        private static final State ABRUPT_COMPLETION = new State(OptionalBool.NO, OptionalBool.YES);
        private static final State UNNAMED_BREAK = new State(OptionalBool.YES, OptionalBool.NO, HashTreePSet.empty(), HashTreePSet.singleton(NO_LABEL));
        private static final State UNNAMED_CONTINUE = new State(OptionalBool.YES, OptionalBool.NO, HashTreePSet.singleton(NO_LABEL), HashTreePSet.empty());

        final PSet<String> continueTargets;
        final PSet<String> breakTargets;
        final OptionalBool exitsLocally;
        final OptionalBool exitsOutside;

        State(OptionalBool exitsLocally, OptionalBool exitsOutside) {
            this(exitsLocally, exitsOutside, HashTreePSet.empty(), HashTreePSet.empty());
        }

        State(OptionalBool exitsLocally, OptionalBool exitsOutside, PSet<String> continues, PSet<String> breaks) {
            this.exitsLocally = exitsLocally;
            this.exitsOutside = exitsOutside;
            this.continueTargets = continues;
            this.breakTargets = breaks;
        }

        State temper() {
            return new State(OptionalBool.min(OptionalBool.UNKNOWN, this.exitsLocally),
                             OptionalBool.min(OptionalBool.UNKNOWN, this.exitsOutside),
                             this.continueTargets, this.breakTargets);
        }

        State replaceExits(OptionalBool bool, OptionalBool outside) {
            if (exitsLocally != bool && exitsOutside != outside) {
                return new State(bool, outside, this.continueTargets, this.breakTargets);
            }
            return this;
        }

        static OptionalBool mix(OptionalBool b1, OptionalBool b2) {
            if (b1 != b2) {
                return OptionalBool.UNKNOWN;
            }
            return b1;
        }

        static OptionalBool max(OptionalBool b1, OptionalBool b2) {
            return OptionalBool.max(b1, b2);
        }

        State join(State other) {
            return new State(mix(this.exitsLocally, other.exitsLocally),
                             mix(this.exitsOutside, other.exitsOutside),
                             this.continueTargets.plusAll(other.continueTargets),
                             this.breakTargets.plusAll(other.breakTargets));
        }

        public State chain(State other) {
            return new State(max(this.exitsLocally, other.exitsLocally),
                             max(this.exitsOutside, other.exitsOutside),
                             this.continueTargets.plusAll(other.continueTargets),
                             this.breakTargets.plusAll(other.breakTargets));
        }

        State removeLabel(String label) {
            return new State(
                this.exitsLocally,
                this.exitsOutside,
                this.continueTargets.minus(label),
                this.breakTargets.minus(label)
            );
        }

        State removeContinue(Set<String> labels) {
            PSet<String> continues = this.continueTargets.minusAll(labels);
            if (continues.size() != this.continueTargets.size()) {
                // we removed stuff
                OptionalBool localExit = this.exitsLocally;
                if (!this.breakTargets.isEmpty() || !continues.isEmpty()) {
                    localExit = OptionalBool.UNKNOWN;
                }

                return new State(localExit,
                                 this.exitsOutside,
                                 continues,
                                 this.breakTargets);

            }
            return this;
        }

        static State breakCompletion(@Nullable String label) {
            if (label == null) {
                return UNNAMED_BREAK;
            }
            return new State(OptionalBool.YES, OptionalBool.NO, HashTreePSet.empty(), HashTreePSet.singleton(label));
        }

        static State continueCompletion(@Nullable String label) {
            if (label == null) {
                return UNNAMED_CONTINUE;
            }
            return new State(OptionalBool.YES, OptionalBool.NO, HashTreePSet.singleton(label), HashTreePSet.empty());
        }
    }

    static final String NO_LABEL = ":not a label:";


    /**
     * @param stmt Statement
     */
    private static State completesNormallyImpl(ASTStatement stmt, PSet<String> curLabels) {

        if (stmt instanceof ASTThrowStatement || stmt instanceof ASTReturnStatement) {

            return State.ABRUPT_COMPLETION;

        } else if (stmt instanceof ASTBreakStatement) {

            String label = ((ASTBreakStatement) stmt).getLabel();
            return State.breakCompletion(label);

        } else if (stmt instanceof ASTContinueStatement) {

            String label = ((ASTContinueStatement) stmt).getLabel();
            return State.continueCompletion(label);

        } else if (stmt instanceof ASTBlock) {

            ASTBlock block = (ASTBlock) stmt;
            State result = State.NORMAL_COMPLETION;
            for (ASTStatement child : block) {
                State childResult = completesNormallyImpl(child, HashTreePSet.empty());

                result = result.chain(childResult);
            }
            return result;

        } else if (stmt instanceof ASTIfStatement) {
            ASTIfStatement ifStmt = (ASTIfStatement) stmt;

            ASTStatement thenBranch = ifStmt.getThenBranch();
            ASTStatement elseBranch = ifStmt.getElseBranch();

            State thenResult = completesNormallyImpl(thenBranch, HashTreePSet.empty());

            if (elseBranch != null) {
                return thenResult.join(completesNormallyImpl(elseBranch, HashTreePSet.empty()));
            } else {
                return thenResult.temper();
            }

        } else if (stmt instanceof ASTLabeledStatement) {
            ASTLabeledStatement labeledStmt = (ASTLabeledStatement) stmt;

            State result = completesNormallyImpl(labeledStmt.getStatement(), curLabels.plus(labeledStmt.getLabel()));

            return result.removeLabel(labeledStmt.getLabel());

        } else if (stmt instanceof ASTSynchronizedStatement) {

            return completesNormallyImpl(((ASTSynchronizedStatement) stmt).getBody(), HashTreePSet.empty());

        } else if (stmt instanceof ASTWhileStatement) {

            PSet<String> allLabels = curLabels.plus(NO_LABEL);
            ASTWhileStatement loop = (ASTWhileStatement) stmt;

            State bodyResult = completesNormallyImpl(((ASTWhileStatement) stmt).getBody(), curLabels);

            bodyResult = bodyResult.removeContinue(allLabels);

            boolean isWhileTrue = JavaRuleUtil.isBooleanLit(loop.getCondition(), true);

            boolean hasLocalBreak = containsAny(bodyResult.breakTargets, allLabels);
            boolean hasOuterBreak = bodyResult.exitsOutside != OptionalBool.NO
                // or there is a break/continue to some label that is not within this loop
                || !isSubset(bodyResult.breakTargets, allLabels)
                || !isSubset(bodyResult.continueTargets, allLabels);

            State result;
            if (hasLocalBreak && !hasOuterBreak) {
                result = bodyResult.temper();
            } else if (hasLocalBreak && hasOuterBreak) {
                // both local & non-local breaks
                result = bodyResult.temper();
            } else if (hasOuterBreak && !hasLocalBreak) {
                result = isWhileTrue ? bodyResult//.replaceExits(bodyResult.exits, true)
                                     : bodyResult.temper();
            } else {
                // no break at all
                result = isWhileTrue ? bodyResult.replaceExits(OptionalBool.NO, OptionalBool.YES)
                                     : bodyResult.replaceExits(OptionalBool.UNKNOWN, OptionalBool.YES);
            }
            return result.removeLabel(NO_LABEL);
        } else {
            return State.NORMAL_COMPLETION;
        }
    }

    // as subset of bs?
    private static <T> boolean isSubset(Collection<? extends T> as, Collection<? super T> bs) {
        for (T label : as) {
            if (!bs.contains(label)) {
                return false;
            }
        }
        return true;
    }

    private static <T> boolean containsAny(Collection<? super T> subject,
                                           Collection<? extends T> searchedValues) {
        for (T t : searchedValues) {
            if (subject.contains(t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the binding set declared by the expression. Note that
     * only some expressions contribute bindings, and every other form
     * of expression does not contribute anything (meaning, all subexpressions
     * are not processed).
     */
    static BindSet bindersOfExpr(ASTExpression e) {
        if (e instanceof ASTUnaryExpression) {
            ASTUnaryExpression unary = (ASTUnaryExpression) e;
            return unary.getOperator() == UnaryOp.NEGATION ? bindersOfExpr(unary.getOperand()).negate()
                                                           : BindSet.EMPTY;
        } else if (e instanceof ASTInfixExpression) {
            BinaryOp op = ((ASTInfixExpression) e).getOperator();
            ASTExpression left = ((ASTInfixExpression) e).getLeftOperand();
            ASTExpression right = ((ASTInfixExpression) e).getRightOperand();

            if (op == BinaryOp.INSTANCEOF && right instanceof ASTPatternExpression) {
                return collectBindings(((ASTPatternExpression) right).getPattern());
            } else if (op == BinaryOp.CONDITIONAL_AND) { // &&
                return BindSet.union(bindersOfExpr(left), bindersOfExpr(right));
            } else if (op == BinaryOp.CONDITIONAL_OR) { // ||
                // actually compute !( !left && !right )
                return BindSet.union(bindersOfExpr(left).negate(),
                                     bindersOfExpr(right).negate()).negate();
            } else {
                return BindSet.EMPTY;
            }
        }
        return BindSet.EMPTY;
    }

    static BindSet collectBindings(ASTPattern pattern) {
        if (pattern instanceof ASTTypeTestPattern) {
            return BindSet.EMPTY.addBinding(((ASTTypeTestPattern) pattern).getVarId());
        } else {
            throw AssertionUtil.shouldNotReachHere("no other instances of pattern should exist");
        }
    }

    static final class BindSet {

        static final BindSet EMPTY = new BindSet(HashTreePSet.empty(),
                                                 HashTreePSet.empty());

        private final PSet<ASTVariableDeclaratorId> trueBindings;
        private final PSet<ASTVariableDeclaratorId> falseBindings;

        static PSet<ASTVariableDeclaratorId> noBindings() {
            return HashTreePSet.empty();
        }

        BindSet(PSet<ASTVariableDeclaratorId> trueBindings,
                PSet<ASTVariableDeclaratorId> falseBindings) {
            this.trueBindings = trueBindings;
            this.falseBindings = falseBindings;
        }

        public PSet<ASTVariableDeclaratorId> getTrueBindings() {
            return trueBindings;
        }

        public PSet<ASTVariableDeclaratorId> getFalseBindings() {
            return falseBindings;
        }

        BindSet negate() {
            return isEmpty() ? this : new BindSet(falseBindings, trueBindings);
        }

        boolean isEmpty() {
            return this == EMPTY;
        }

        BindSet addBinding(ASTVariableDeclaratorId e) {
            return new BindSet(trueBindings.plus(e), falseBindings);
        }

        static BindSet union(BindSet first, BindSet other) {
            if (first.isEmpty()) {
                return other;
            } else if (other.isEmpty()) {
                return first;
            }
            return new BindSet(first.trueBindings.plusAll(other.trueBindings),
                               first.falseBindings.plusAll(other.falseBindings));
        }
    }

}
