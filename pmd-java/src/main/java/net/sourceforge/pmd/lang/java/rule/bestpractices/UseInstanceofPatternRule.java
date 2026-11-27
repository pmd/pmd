/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_AND;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_OPS;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.CONDITIONAL_OR;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.INSTANCEOF;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPattern;
import net.sourceforge.pmd.lang.java.ast.ASTPatternExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTTypePattern;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.internal.AbruptCompletionAnalysis;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Reports casts that can be replaced with a pattern variable declared by a
 * JEP 394 {@code instanceof} check (Java 16+).
 *
 * <p>Features that are not (yet) implemented:
 * <ul>
 *   <li>Reassignment analysis beyond the effectively-final check.</li>
 *   <li>Field and getter paths as the guard subject, e.g. {@code obj.field}
 *       or {@code obj.getFoo()}.</li>
 *   <li>Subtype and widening casts, where the cast type is a supertype of the
 *       tested type. Only exact type equality is supported, because a pattern
 *       variable with a narrower static type could change overload resolution
 *       or member selection.</li>
 *   <li>Loop conditions as additional guard contexts.</li>
 *   <li>{@code assert} statements as guards.</li>
 *   <li>Splitting {@code &&}/{@code ||} conditions of ternary expressions into
 *       individual guards.</li>
 * </ul>
 *
 * @since 7.31.0
 */
public final class UseInstanceofPatternRule extends AbstractJavaRulechainRule {

    private static final String EXISTING_BINDING_MESSAGE = "Use the existing pattern variable ''{0}'' instead of this cast";

    public UseInstanceofPatternRule() {
        super(ASTCastExpression.class);
    }

    @Override
    public Object visit(ASTCastExpression cast, Object data) {
        RuleContext ctx = (RuleContext) data;

        JTypeMirror castType = cast.getCastType().getTypeMirror();
        if (TypeOps.isUnresolvedOrNull(castType)) {
            return null;
        }

        GuardInfo guard = findDominatingGuard(cast, castType);
        if (guard == null) {
            return null;
        }

        if (guard.patternVariableName != null) {
            ctx.addViolationWithMessage(cast, EXISTING_BINDING_MESSAGE, guard.patternVariableName);
        } else {
            ctx.addViolation(cast);
        }
        return null;
    }

    private static @Nullable GuardInfo findDominatingGuard(
            ASTCastExpression cast,
            JTypeMirror castType
    ) {
        GuardInfo guard = findInEnclosingIfs(cast, castType);
        if (guard == null) {
            guard = findInEnclosingBlocks(cast, castType);
        }
        if (guard == null) {
            guard = findInEnclosingTernaries(cast, castType);
        }
        if (guard == null) {
            guard = findInLogicalChains(cast, castType);
        }
        return guard;
    }

    /**
     * Finds a guard in an enclosing {@code if} statement whose branch contains
     * the cast, e.g.
     * <pre>{@code
     * if (x instanceof Foo) {
     *     bar((Foo) x);
     * }
     * if (!(x instanceof Foo)) {
     *     bar("not a Foo");
     * } else {
     *     bar((Foo) x);
     * }
     * }</pre>
     */
    private static @Nullable GuardInfo findInEnclosingIfs(
            ASTCastExpression cast,
            JTypeMirror castType
    ) {
        for (ASTIfStatement ifStmt : cast.ancestors(ASTIfStatement.class)) {
            if (crossesFindBoundary(cast, ifStmt)) {
                break;
            }

            if (isDescendant(ifStmt.getThenBranch(), cast)) {
                GuardInfo guard = findMatchingGuard(ifStmt.getCondition(), true, cast, castType);
                if (guard != null) {
                    return guard;
                }
            } else if (ifStmt.getElseBranch() != null && isDescendant(ifStmt.getElseBranch(), cast)) {
                GuardInfo guard = findMatchingGuard(ifStmt.getCondition(), false, cast, castType);
                if (guard != null) {
                    return guard;
                }
            }
        }
        return null;
    }

    /**
     * Finds a guard in an early-exit {@code if} that precedes the cast in an
     * enclosing block, e.g.
     * <pre>{@code
     * if (!(x instanceof Foo)) {
     *     return;
     * }
     * bar((Foo) x);
     * }</pre>
     */
    private static @Nullable GuardInfo findInEnclosingBlocks(
            ASTCastExpression cast,
            JTypeMirror castType
    ) {
        for (ASTBlock block : cast.ancestors(ASTBlock.class)) {
            if (crossesFindBoundary(cast, block)) {
                break;
            }

            List<ASTStatement> statements = block.children(ASTStatement.class).toList();
            int castIndex = indexOfContaining(statements, cast);
            for (int i = 0; i < castIndex; i++) {
                if (!(statements.get(i) instanceof ASTIfStatement)) {
                    continue;
                }
                ASTIfStatement ifStmt = (ASTIfStatement) statements.get(i);

                if (isAbrupt(ifStmt.getThenBranch())) {
                    GuardInfo guard = findMatchingGuard(ifStmt.getCondition(), false, cast, castType);
                    if (guard != null) {
                        return guard;
                    }
                }

                if (ifStmt.getElseBranch() != null && isAbrupt(ifStmt.getElseBranch())) {
                    GuardInfo guard = findMatchingGuard(ifStmt.getCondition(), true, cast, castType);
                    if (guard != null) {
                        return guard;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds a guard in the condition of an enclosing conditional expression,
     * e.g.
     * <pre>{@code
     * x instanceof Foo ? ((Foo) x).m() : other
     * }</pre>
     */
    private static @Nullable GuardInfo findInEnclosingTernaries(
            ASTCastExpression cast,
            JTypeMirror castType
    ) {
        for (ASTConditionalExpression conditional : cast.ancestors(ASTConditionalExpression.class)) {
            if (crossesFindBoundary(cast, conditional)) {
                break;
            }

            boolean inElse = isDescendant(conditional.getElseBranch(), cast);

            GuardInfo guard = analyzeInstanceofCondition(conditional.getCondition());
            if (guard != null
                    && guard.negated == inElse
                    && guard.testedType.equals(castType)
                    && isSameSubject(guard.subject, cast.getOperand())
            ) {
                return guard;
            }
        }
        return null;
    }

    /**
     * Finds a guard in an enclosing {@code &&}/{@code ||} chain, e.g.
     * <pre>{@code
     * x instanceof Foo && ((Foo) x).m()
     * }</pre>
     */
    private static @Nullable GuardInfo findInLogicalChains(
            ASTCastExpression cast,
            JTypeMirror castType
    ) {
        for (ASTInfixExpression chain
                : cast.ancestors(ASTInfixExpression.class)
                        .filter(e -> CONDITIONAL_OPS.contains(e.getOperator()))
        ) {
            if (crossesFindBoundary(cast, chain)) {
                break;
            }

            BinaryOp op = chain.getOperator();
            List<ASTExpression> leaves = new ArrayList<>();
            collectLeaves(chain, op, leaves);

            int castIndex = indexOfContaining(leaves, cast);
            for (int i = 0; i < castIndex; i++) {
                GuardInfo guard = analyzeInstanceofCondition(leaves.get(i));
                if (guard != null
                        && ((op == CONDITIONAL_AND && !guard.negated)
                                || (op == CONDITIONAL_OR && guard.negated))
                        && guard.testedType.equals(castType)
                        && isSameSubject(guard.subject, cast.getOperand())
                ) {
                    return guard;
                }
            }
        }
        return null;
    }

    private static @Nullable GuardInfo findMatchingGuard(
            ASTExpression condition,
            boolean thenBranch,
            ASTCastExpression cast,
            JTypeMirror castType
    ) {
        for (GuardInfo guard : guardsFromCondition(condition, thenBranch)) {
            if (guard.testedType.equals(castType) && isSameSubject(guard.subject, cast.getOperand())) {
                return guard;
            }
        }
        return null;
    }

    private static List<GuardInfo> guardsFromCondition(ASTExpression condition, boolean thenBranch) {
        BinaryOp op = thenBranch ? CONDITIONAL_AND : CONDITIONAL_OR;
        List<ASTExpression> leaves = new ArrayList<>();
        collectLeaves(condition, op, leaves);

        List<GuardInfo> result = new ArrayList<>();
        for (ASTExpression leaf : leaves) {
            GuardInfo guard = analyzeInstanceofCondition(leaf);
            if (guard != null && guard.negated == !thenBranch) {
                result.add(guard);
            }
        }
        return result;
    }

    private static @Nullable GuardInfo analyzeInstanceofCondition(ASTExpression expr) {
        boolean negated = false;
        ASTExpression current = expr;
        while (JavaAstUtils.isBooleanNegation(current)) {
            negated = !negated;
            current = JavaAstUtils.unaryOperand(current);
        }

        if (!JavaAstUtils.isInfixExprWithOperator(current, INSTANCEOF)) {
            return null;
        }
        ASTInfixExpression infix = (ASTInfixExpression) current;

        JTypeMirror type = testedType(infix.getRightOperand());
        if (type == null) {
            return null;
        }

        String patternVariableName = null;
        if (infix.getRightOperand() instanceof ASTPatternExpression) {
            ASTPattern pattern = ((ASTPatternExpression) infix.getRightOperand()).getPattern();
            if (pattern instanceof ASTTypePattern) {
                patternVariableName = ((ASTTypePattern) pattern).getVarId().getName();
            }
        }

        return new GuardInfo(infix.getLeftOperand(), type, negated, patternVariableName);
    }

    private static @Nullable JTypeMirror testedType(ASTExpression right) {
        ASTType type = null;
        if (right instanceof ASTTypeExpression) {
            type = ((ASTTypeExpression) right).getTypeNode();
        } else if (right instanceof ASTPatternExpression && ((ASTPatternExpression) right).getPattern() instanceof ASTTypePattern) {
            type = ((ASTTypePattern) ((ASTPatternExpression) right).getPattern()).getTypeNode();
        }
        if (type == null) {
            return null;
        }
        JTypeMirror mirror = type.getTypeMirror();
        if (TypeOps.isUnresolvedOrNull(mirror) || mirror.isPrimitive()) {
            return null;
        }
        return mirror;
    }

    private static boolean isSameSubject(ASTExpression guardSubject, ASTExpression castOperand) {
        if (JavaAstUtils.isUnqualifiedThis(guardSubject) && JavaAstUtils.isUnqualifiedThis(castOperand)) {
            return true;
        }
        if (JavaAstUtils.isUnqualifiedSuper(guardSubject) && JavaAstUtils.isUnqualifiedSuper(castOperand)) {
            return true;
        }
        if (guardSubject instanceof ASTVariableAccess && castOperand instanceof ASTVariableAccess) {
            JVariableSymbol s1 = ((ASTVariableAccess) guardSubject).getReferencedSym();
            JVariableSymbol s2 = ((ASTVariableAccess) castOperand).getReferencedSym();
            if (!(s1 instanceof JLocalVariableSymbol) || !s1.equals(s2)) {
                return false;
            }
            ASTVariableId varId = s1.tryGetNode();
            return varId != null && JavaAstUtils.isEffectivelyFinal(varId);
        }
        return false;
    }

    private static boolean isDescendant(JavaNode ancestor, JavaNode node) {
        for (Node n : node.ancestorsOrSelf()) {
            if (n == ancestor) {
                return true;
            }
            if (n.isFindBoundary()) {
                return false;
            }
        }
        return false;
    }

    private static int indexOfContaining(List<? extends JavaNode> nodes, JavaNode node) {
        for (int i = 0; i < nodes.size(); i++) {
            if (isDescendant(nodes.get(i), node)) {
                return i;
            }
        }
        throw new IllegalArgumentException(node + " is not contained in any of the given nodes");
    }

    private static boolean isAbrupt(ASTStatement stmt) {
        return !AbruptCompletionAnalysis.canCompleteNormally(stmt, expr -> Boolean.TRUE.equals(expr.getConstValue()));
    }

    private static boolean crossesFindBoundary(JavaNode node, JavaNode ancestor) {
        for (Node n : node.ancestors()) {
            if (n == ancestor) {
                return false;
            }
            if (n.isFindBoundary()) {
                return true;
            }
        }
        throw new IllegalArgumentException(ancestor + " is not an ancestor of " + node);
    }

    private static void collectLeaves(ASTExpression expr, BinaryOp op, List<ASTExpression> leaves) {
        if (JavaAstUtils.isInfixExprWithOperator(expr, op)) {
            ASTInfixExpression infix = (ASTInfixExpression) expr;
            collectLeaves(infix.getLeftOperand(), op, leaves);
            collectLeaves(infix.getRightOperand(), op, leaves);
            return;
        }
        leaves.add(expr);
    }

    private static final class GuardInfo {
        final ASTExpression subject;
        final JTypeMirror testedType;
        final boolean negated;
        final @Nullable String patternVariableName;

        GuardInfo(
                ASTExpression subject,
                JTypeMirror testedType,
                boolean negated,
                @Nullable String patternVariableName
        ) {
            this.subject = subject;
            this.testedType = testedType;
            this.negated = negated;
            this.patternVariableName = patternVariableName;
        }
    }
}
