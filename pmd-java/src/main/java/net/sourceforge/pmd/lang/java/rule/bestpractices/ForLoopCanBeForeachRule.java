/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.lang.ast.NodeStream.empty;
import static net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT;

import java.util.Iterator;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAccess;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpressionList;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil.InvocationMatcher;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class ForLoopCanBeForeachRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher ITERATOR_CALL = InvocationMatcher.parse("java.lang.Iterable#iterator()");
    private static final InvocationMatcher ITERATOR_NEXT = InvocationMatcher.parse("java.util.Iterator#next()");
    private static final InvocationMatcher ITERATOR_HAS_NEXT = InvocationMatcher.parse("java.util.Iterator#hasNext()");
    private static final InvocationMatcher COLLECTION_SIZE = InvocationMatcher.parse("java.util.Collection#size()");
    private static final InvocationMatcher LIST_GET = InvocationMatcher.parse("java.util.List#get(int)");

    public ForLoopCanBeForeachRule() {
        super(ASTForStatement.class);
    }

    @Override
    public Object visit(ASTForStatement forLoop, Object data) {

        final @Nullable ASTStatement init = forLoop.getInit();
        final @Nullable ASTStatementExpressionList update = forLoop.getUpdate();
        final ASTExpression guardCondition = forLoop.getCondition();

        if (init == null && update == null || guardCondition == null) {
            return data;
        }

        // checked to be either Iterator or int
        ASTVariableDeclaratorId index = getIndexVarDeclaration(init, update);

        if (index == null) {
            return data;
        }

        if (index.getTypeMirror().isPrimitive(INT)) {
            ASTNamedReferenceExpr iterable = findIterableFromCondition(guardCondition, index);
            if (iterable != null) {
                if (isReplaceableArrayLoop(forLoop, index, iterable)
                    || isReplaceableListLoop(forLoop, index, iterable)) {
                    addViolation(data, forLoop);
                }
            }
        } else if (TypeTestUtil.isA(Iterator.class, index.getTypeMirror())) {
            if (isReplaceableIteratorLoop(index, forLoop)) {
                addViolation(data, forLoop);
            }
            return data;
        }

        return data;
    }


    /** Finds the declaration of the index variable and its occurrences, null to abort */
    private @Nullable ASTVariableDeclaratorId getIndexVarDeclaration(@Nullable ASTStatement init, ASTStatementExpressionList update) {
        if (init == null) {
            return guessIndexVarFromUpdate(update);
        } else if (init instanceof ASTLocalVariableDeclaration) {
            NodeStream<ASTVariableDeclaratorId> varIds = ((ASTLocalVariableDeclaration) init).getVarIds();
            if (varIds.count() == 1) {
                ASTVariableDeclaratorId first = varIds.firstOrThrow();
                if (ITERATOR_CALL.matchesCall(first.getInitializer())
                    || JavaRuleUtil.isLiteralInt(first.getInitializer(), 0)) {
                    return first;
                }
            }
        }

        return null;
    }


    /**
     * @return the variable name if there's only one update statement of the form i++ or ++i.
     */
    private @Nullable ASTVariableDeclaratorId guessIndexVarFromUpdate(ASTStatementExpressionList update) {
        return NodeStream.of(update)
                         .filter(it -> it.getNumChildren() == 1)
                         .firstChild(ASTUnaryExpression.class)
                         .map(this::asIPlusPlus)
                         .first();
    }

    private @Nullable ASTVariableDeclaratorId asIPlusPlus(ASTExpression update) {
        return NodeStream.of(update)
                         .filterIs(ASTUnaryExpression.class)
                         .filter(it -> it.getOperator().isIncrement())
                         .map(ASTUnaryExpression::getOperand)
                         .filterIs(ASTVariableAccess.class)
                         .firstOpt()
                         .map(ASTNamedReferenceExpr::getReferencedSym)
                         .map(JVariableSymbol::tryGetNode)
                         .orElse(null);
    }


    /**
     * Gets the name of the iterable array or list. The condition has the form i < arr.length or i < coll.size()
     *
     * @param indexVar The index variable
     *
     * @return The name, or null if it couldn't be found or the guard condition is not safe to refactor (then abort)
     */
    private @Nullable ASTNamedReferenceExpr findIterableFromCondition(ASTExpression guardCondition, ASTVariableDeclaratorId indexVar) {
        if (!BinaryOp.isInfixExprWithOperator(guardCondition, BinaryOp.COMPARISON_OPS)) {
            return null;
        }

        ASTInfixExpression condition = (ASTInfixExpression) guardCondition;
        BinaryOp op = condition.getOperator();

        if (!JavaRuleUtil.isReferenceToVar(condition.getLeftOperand(), indexVar.getSymbol())) {
            return null;
        }

        NodeStream<ASTExpression> rhs = empty();
        if (op == BinaryOp.LT) {
            // i < rhs
            rhs = NodeStream.of(condition.getRightOperand());
        } else if (op == BinaryOp.LE) {
            // i <= rhs - 1
            rhs = NodeStream.of(condition.getRightOperand())
                            .filterIs(ASTInfixExpression.class)
                            .filter(it -> it.getOperator() == BinaryOp.SUB)
                            .filter(it -> JavaRuleUtil.isLiteralInt(it.getRightOperand(), 1))
                            .map(ASTInfixExpression::getLeftOperand);

        }

        if (rhs.isEmpty()) {
            return null;
        }

        ASTExpression sizeExpr = rhs.get(0);
        ASTExpression iterableExpr = null;
        if (sizeExpr instanceof ASTFieldAccess && "length".equals(((ASTFieldAccess) sizeExpr).getName())) {
            iterableExpr = ((ASTFieldAccess) sizeExpr).getQualifier();
        } else if (COLLECTION_SIZE.matchesCall(sizeExpr)) {
            iterableExpr = ((ASTMethodCall) sizeExpr).getQualifier();
        }

        if (!(iterableExpr instanceof ASTNamedReferenceExpr)
            || ((ASTNamedReferenceExpr) iterableExpr).getReferencedSym() == null) {
            return null;
        }

        return (ASTNamedReferenceExpr) iterableExpr;
    }


    private boolean isReplaceableArrayLoop(ASTForStatement loop,
                                           ASTVariableDeclaratorId index,
                                           ASTNamedReferenceExpr arrayDeclaration) {

        return arrayDeclaration.getTypeMirror().isArray()
            && occurrencesMatch(loop, index, arrayDeclaration, (i, iterable, expr) -> isArrayAccessIndex(expr, iterable));

    }


    private boolean isReplaceableListLoop(ASTForStatement loop,
                                          ASTVariableDeclaratorId index,
                                          ASTNamedReferenceExpr listDeclaration) {

        return TypeTestUtil.isA(List.class, listDeclaration.getTypeMirror())
            && occurrencesMatch(loop, index, listDeclaration, (i, iterable, expr) -> isListGetIndex(expr, iterable));
    }

    private boolean isArrayAccessIndex(ASTNamedReferenceExpr usage, ASTNamedReferenceExpr arrayVar) {
        if (!(usage.getParent() instanceof ASTArrayAccess)) {
            return false;
        }
        ASTArrayAccess arrayAccess = (ASTArrayAccess) usage.getParent();
        return arrayAccess.getAccessType() == AccessType.READ
            && JavaRuleUtil.isReferenceToSameVar(arrayAccess.getQualifier(), arrayVar);
    }


    private boolean isListGetIndex(ASTNamedReferenceExpr usage, ASTNamedReferenceExpr listVar) {
        return usage.getParent() instanceof ASTArgumentList
            && LIST_GET.matchesCall(usage.getParent().getParent())
            && JavaRuleUtil.isReferenceToSameVar(((ASTMethodCall) usage.getParent().getParent()).getQualifier(), listVar);
    }


    private interface OccurrenceMatcher {

        boolean matches(ASTVariableDeclaratorId index, ASTNamedReferenceExpr iterable, ASTNamedReferenceExpr indexOcc1);
    }

    private boolean occurrencesMatch(ASTForStatement loop,
                                     ASTVariableDeclaratorId index,
                                     ASTNamedReferenceExpr collection,
                                     OccurrenceMatcher getMatcher) {

        for (ASTNamedReferenceExpr usage : index.getLocalUsages()) {
            ASTExpression toplevel = JavaRuleUtil.getTopLevelExpr(usage);
            boolean isInUpdateOrCond =
                loop.getUpdate() == toplevel.getParent()
                    || loop.getCondition() == toplevel;

            if (!isInUpdateOrCond && !getMatcher.matches(index, collection, usage)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isReplaceableIteratorLoop(ASTVariableDeclaratorId var, ASTForStatement stmt) {
        List<ASTNamedReferenceExpr> usages = var.getLocalUsages();
        if (usages.size() != 2) {
            return false;
        }
        ASTNamedReferenceExpr u1 = usages.get(0);
        ASTNamedReferenceExpr u2 = usages.get(1);
        return isHasNextInCondition(u1, stmt) && isNextInLoop(u2, stmt)
            || isNextInLoop(u1, stmt) && isHasNextInCondition(u2, stmt);
    }

    private static boolean isNextInLoop(ASTNamedReferenceExpr u1, ASTForStatement stmt) {
        return ITERATOR_NEXT.matchesCall(u1.getParent()) && u1.ancestors().any(it -> it == stmt);
    }

    private static boolean isHasNextInCondition(ASTNamedReferenceExpr u1, ASTForStatement forStmt) {
        return forStmt.getCondition() == u1.getParent() && ITERATOR_HAS_NEXT.matchesCall(u1.getParent());
    }


}
