/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.DIV;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.GE;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.GT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.LE;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.LT;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.MOD;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.MUL;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.SHIFT_OPS;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.SUB;
import static net.sourceforge.pmd.lang.java.ast.BinaryOp.isInfixExprWithOperator;

import java.util.EnumSet;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.ExprContext;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;

/**
 * Detects casts where the operand is already a subtype of the context
 * type, or may be converted to it implicitly.
 */
public class UnnecessaryCastRule extends AbstractJavaRulechainRule {

    private static final EnumSet<BinaryOp> BINARY_PROMOTED_OPS =
        EnumSet.of(LE, GE, GT, LT, ADD, SUB, MUL, DIV, MOD);

    public UnnecessaryCastRule() {
        super(ASTCastExpression.class);
    }

    @Override
    public Object visit(ASTCastExpression castExpr, Object data) {
        ASTExpression operand = castExpr.getOperand();

        // eg in
        // Object o = (Integer) 1;

        @Nullable ExprContext context = castExpr.getConversionContext();        // Object
        JTypeMirror coercionType = castExpr.getCastType().getTypeMirror();      // Integer
        JTypeMirror operandType = operand.getTypeMirror();                      // int

        if (TypeOps.isUnresolvedOrNull(operandType)
            || TypeOps.isUnresolvedOrNull(coercionType)) {
            return null;
        }

        // Note that we assume that coercionType is convertible to
        // contextType because the code must compile

        if (operand instanceof ASTLambdaExpression || operand instanceof ASTMethodReference) {
            // Then the cast provides a target type for the expression (always).
            // We need to check the enclosing context, as if it's invocation we give up for now
            if (context.isMissing() || context.isInvocationContext()) {
                // Then the cast may be used to determine the overload.
                // We need to treat the casted lambda as a whole unit.
                // todo see below
                return null;
            }

            // Since the code is assumed to compile we'll just assume that coercionType
            // is a functional interface.
            if (coercionType.equals(context.getTargetType())) {
                // then we also know that the context is functional
                reportCast(castExpr, data);
            }
            // otherwise the cast is narrowing, and removing it would
            // change the runtime class of the produced lambda.
            // Eg `SuperItf obj = (SubItf) ()-> {};`
            // If we remove the cast, even if it might compile,
            // the object will not implement SubItf anymore.
        } else if (isCastUnnecessary(castExpr, context, coercionType, operandType)) {
            reportCast(castExpr, data);
        }
        return null;
    }

    private boolean isCastUnnecessary(ASTCastExpression castExpr, @NonNull ExprContext context, JTypeMirror coercionType, JTypeMirror operandType) {
        if (operandType.equals(coercionType)) {
            // with the exception of the lambda thing above, casts to
            // the same type are always unnecessary
            return true;
        } else if (context.isMissing()) {
            // then we have fewer violation conditions

            return !operandType.isBottom() // casts on a null literal are necessary
                && operandType.isSubtypeOf(coercionType);
        }

        return !isCastDeterminingContext(castExpr, context, coercionType)
            && castIsUnnecessaryToMatchContext(context, coercionType, operandType);
    }

    private void reportCast(ASTCastExpression castExpr, Object data) {
        addViolation(data, castExpr, PrettyPrintingUtil.prettyPrintType(castExpr.getCastType()));
    }

    private static boolean castIsUnnecessaryToMatchContext(ExprContext context,
                                                           JTypeMirror coercionType,
                                                           JTypeMirror operandType) {
        if (context.isInvocationContext()) {
            // todo unsupported for now, the cast may be disambiguating overloads
            return false;
        }

        JTypeMirror contextType = context.getTargetType();
        if (contextType == null) {
            return false; // should not occur in valid code
        }

        if (!TypeConversion.isConvertibleUsingBoxing(operandType, coercionType)) {
            // narrowing cast
            return false;
        }

        if (!context.acceptsType(operandType)) {
            // then removing the cast would produce uncompilable code
            return false;
        }

        boolean isBoxingFollowingCast = contextType.isPrimitive() != coercionType.isPrimitive();
        // means boxing behavior is equivalent
        return !isBoxingFollowingCast || operandType.unbox().isSubtypeOf(contextType.unbox());
    }

    /**
     * Returns whether the context type actually depends on the cast.
     * This means our analysis as written above won't work, and usually
     * that the cast is necessary, because there's some primitive conversions
     * happening, or some other corner case.
     */
    private static boolean isCastDeterminingContext(ASTCastExpression castExpr, ExprContext context, @NonNull JTypeMirror coercionType) {

        if (castExpr.getParent() instanceof ASTConditionalExpression && castExpr.getIndexInParent() != 0) {
            // a branch of a ternary
            return true;
        }

        if (context.isNumeric() && castExpr.getParent() instanceof ASTInfixExpression) {
            ASTInfixExpression parent = (ASTInfixExpression) castExpr.getParent();

            if (isInfixExprWithOperator(parent, SHIFT_OPS)) {
                // then the cast is determining the width of expr
                assert castExpr == parent.getLeftOperand(); // second operand doesn't have a numeric context
                return true;
            } else if (isInfixExprWithOperator(parent, BINARY_PROMOTED_OPS)) {
                ASTExpression otherOperand = JavaRuleUtil.getOtherOperandIfInInfixExpr(castExpr);
                if (otherOperand instanceof ASTCastExpression) {
                    return true; // remove FPs
                }
                JTypeMirror otherType = otherOperand.getTypeMirror();

                // Ie, the type that is taken by the binary promotion
                // is the type of the cast, not the type of the operand.
                // Eg in
                //     int i; ((double) i) * i
                // the only reason the mult expr has type double is because of the cast
                return TypeOps.isStrictSubtype(otherType, coercionType)
                    // but not for integers strictly smaller than int
                    && !otherType.unbox().isSubtypeOf(otherType.getTypeSystem().SHORT);
            }

        }
        return false;
    }

}
