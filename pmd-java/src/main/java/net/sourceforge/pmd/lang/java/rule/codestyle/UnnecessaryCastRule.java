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
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isInfixExprWithOperator;

import java.util.EnumSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeOps.Convertibility;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind;

/**
 * Detects casts where the operand is already a subtype of the context
 * type, or may be converted to it implicitly.
 */
public class UnnecessaryCastRule extends AbstractJavaRulechainRule {

    private static final Set<BinaryOp> BINARY_PROMOTED_OPS =
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
            if (context.isMissing() || context.hasKind(ExprContextKind.INVOCATION)) {
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
        } else if (castExpr.getParent() instanceof ASTMethodCall
                    && castExpr.getIndexInParent() == 0) {
            JMethodSig methodType = ((ASTMethodCall) castExpr.getParent()).getMethodType();
            handleMethodCall(castExpr, methodType, operandType, data);
        }
        return null;
    }

    private void handleMethodCall(ASTCastExpression castExpr, JMethodSig methodType,
            JTypeMirror operandType, Object data) {
        boolean generic = methodType.getSymbol().getFormalParameters().stream()
            .anyMatch(fp -> isTypeExpression(fp.getTypeMirror(Substitution.EMPTY)));
        if (!generic) {
            JTypeMirror declaringType = methodType.getDeclaringType();
            if (!isTypeExpression(methodType.getSymbol().getReturnType(Substitution.EMPTY))) {
                // declaring type of List<T>::size is List<T>, but since the return type
                // is not generic, it's enough to check that operand is a List
                declaringType = declaringType.getErasure();
            }
            if (TypeTestUtil.isA(declaringType, operandType)) {
                reportCast(castExpr, data);
            }
        }
    }

    private boolean isTypeExpression(JTypeMirror type) {
        return type.isGeneric() || type instanceof JTypeVar;
    }

    private boolean isCastUnnecessary(ASTCastExpression castExpr, @NonNull ExprContext context, JTypeMirror coercionType, JTypeMirror operandType) {
        if (operandType.equals(coercionType)) {
            return true;
        } else if (context.isMissing()) {
            // then we have fewer violation conditions

            return !operandType.isBottom() // casts on a null literal are necessary
                   && operandType.isSubtypeOf(coercionType)
                   && !isCastToRawType(coercionType, operandType)
                   // If the context is missing when the parent is a lambda,
                   // that means the body of the lambda is determining the return
                   // type of the lambda
                   && getLambdaParent(castExpr) == null;
        }

        return !isCastDeterminingContext(castExpr, context, coercionType, operandType)
            && castIsUnnecessaryToMatchContext(context, coercionType, operandType);
    }

    /**
     * Whether this cast is casting a non-raw type to a raw type.
     * This is part of the {@link Convertibility#bySubtyping()} relation,
     * and needs to be singled out as operations on the raw type
     * behave differently than on the non-raw type. In that case the
     * cast may be necessary to avoid compile-errors, even though it
     * will be noop at runtime (an _unchecked_ cast).
     */
    private boolean isCastToRawType(JTypeMirror coercionType, JTypeMirror operandType) {
        return coercionType.isRaw() && !operandType.isRaw();
    }

    private void reportCast(ASTCastExpression castExpr, Object data) {
        asCtx(data).addViolation(castExpr, PrettyPrintingUtil.prettyPrintType(castExpr.getCastType()));
    }

    private static boolean castIsUnnecessaryToMatchContext(ExprContext context,
                                                           JTypeMirror coercionType,
                                                           JTypeMirror operandType) {
        if (context.hasKind(ExprContextKind.INVOCATION)) {
            // todo unsupported for now, the cast may be disambiguating overloads
            return false;
        }

        JTypeMirror contextType = context.getTargetType();
        if (contextType == null) {
            return false; // should not occur in valid code
        } else if (!TypeConversion.isConvertibleUsingBoxing(operandType, coercionType)) {
            // narrowing cast
            return false;
        } else if (!context.acceptsType(operandType)) {
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
    private static boolean isCastDeterminingContext(ASTCastExpression castExpr, ExprContext context, @NonNull JTypeMirror coercionType, JTypeMirror operandType) {

        if (castExpr.getParent() instanceof ASTConditionalExpression && castExpr.getIndexInParent() != 0) {
            // a branch of a ternary
            return true;

        } else if (context.hasKind(ExprContextKind.STRING) && isInfixExprWithOperator(castExpr.getParent(), ADD)) {

            // inside string concatenation
            return !TypeTestUtil.isA(String.class, JavaAstUtils.getOtherOperandIfInInfixExpr(castExpr))
                && !TypeTestUtil.isA(String.class, operandType);

        } else if (context.hasKind(ExprContextKind.NUMERIC) && castExpr.getParent() instanceof ASTInfixExpression) {
            // numeric expr
            ASTInfixExpression parent = (ASTInfixExpression) castExpr.getParent();

            if (isInfixExprWithOperator(parent, SHIFT_OPS)) {
                // if so, then the cast is determining the width of expr
                // the right operand is always int
                return castExpr == parent.getLeftOperand()
                        && !TypeOps.isStrictSubtype(operandType.unbox(), operandType.getTypeSystem().INT);
            } else if (isInfixExprWithOperator(parent, BINARY_PROMOTED_OPS)) {
                ASTExpression otherOperand = JavaAstUtils.getOtherOperandIfInInfixExpr(castExpr);
                JTypeMirror otherType = otherOperand.getTypeMirror();

                // Ie, the type that is taken by the binary promotion
                // is the type of the cast, not the type of the operand.
                // Eg in
                //     int i; ((double) i) * i
                // the only reason the mult expr has type double is because of the cast
                JTypeMirror promotedTypeWithoutCast = TypeConversion.binaryNumericPromotion(operandType, otherType);
                JTypeMirror promotedTypeWithCast = TypeConversion.binaryNumericPromotion(coercionType, otherType);
                return !promotedTypeWithoutCast.equals(promotedTypeWithCast);
            }

        }
        return false;
    }

    private static @Nullable ASTLambdaExpression getLambdaParent(ASTCastExpression castExpr) {
        if (castExpr.getParent() instanceof ASTLambdaExpression) {
            return (ASTLambdaExpression) castExpr.getParent();
        }
        if (castExpr.getParent() instanceof ASTReturnStatement) {
            JavaNode returnTarget = JavaAstUtils.getReturnTarget((ASTReturnStatement) castExpr.getParent());

            if (returnTarget instanceof ASTLambdaExpression) {
                return (ASTLambdaExpression) returnTarget;
            }
        }
        return null;
    }

}
