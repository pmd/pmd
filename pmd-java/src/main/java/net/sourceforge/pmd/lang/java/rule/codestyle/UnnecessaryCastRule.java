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
import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static net.sourceforge.pmd.util.OptionalBool.definitely;

import java.util.EnumSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeOps.Convertibility;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind;
import net.sourceforge.pmd.util.OptionalBool;

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
        }
        return null;
    }

    private boolean isCastUnnecessary(ASTCastExpression castExpr, @NonNull ExprContext context, JTypeMirror coercionType, JTypeMirror operandType) {
        if (isCastDeterminingReturnOfLambda(castExpr) != NO) {
            return false;
        }

        if (operandType.equals(coercionType)) {
            // with the exception of the lambda thing above, casts to
            // the same type are always unnecessary
            return true;
        } else if (context.isMissing()) {
            // then we have fewer violation conditions

            return !operandType.isBottom() // casts on a null literal are necessary
                && operandType.isSubtypeOf(coercionType)
                && !isCastToRawType(coercionType, operandType);
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

    private static OptionalBool isCastDeterminingReturnOfLambda(ASTCastExpression castExpr) {
        ASTLambdaExpression lambda = getLambdaParent(castExpr);
        if (lambda == null) {
            return NO;
        }

        // The necessary conditions are:
        // - The lambda return type mentions type vars that are missing from its arguments
        // (lambda is context dependent)
        // - The lambda type is inferred:
        //   1. its context is missing, or
        //   2. it is invocation and the parent method call
        //      a. is inferred (generic + no explicit arguments)
        //      b. mentions some of its type params in the argument type corresponding to the lambda

        JExecutableSymbol symbol = lambda.getFunctionalMethod().getSymbol();
        if (symbol.isUnresolved()) {
            return UNKNOWN;
        }

        // Note we don't test the functional method directly, because it has been instantiated
        // We test its generic signature (the symbol).
        boolean contextDependent = TypeOps.isContextDependent(symbol);
        if (!contextDependent) {
            return NO;
        }

        ExprContext lambdaCtx = lambda.getConversionContext();
        if (lambdaCtx.isMissing()) {
            return YES;
        } else if (lambdaCtx.hasKind(ExprContextKind.CAST)) {
            return NO;
        } else if (lambdaCtx.hasKind(ExprContextKind.INVOCATION)) {
            InvocationNode parentCall = (InvocationNode) lambda.getParent().getParent();
            if (parentCall.getExplicitTypeArguments() != null) {
                return NO;
            }
            OverloadSelectionResult overload = parentCall.getOverloadSelectionInfo();
            if (overload.isFailed()) {
                return UNKNOWN;
            }
            JMethodSig parentMethod = overload.getMethodType();
            if (!parentMethod.getSymbol().isGeneric()) {
                return NO;
            }

            int argIdx = lambda.getIndexInParent();
            JMethodSig genericSig = parentMethod.getSymbol().getGenericSignature();
            // this is the generic lambda ty as mentioned in the formal parameters
            JTypeMirror genericLambdaTy = genericSig.ithFormalParam(argIdx, overload.isVarargsCall());
            if (!(genericLambdaTy instanceof JClassType)) {
                return NO;
            }
            // Note that we don't capture this type, which may make the method type malformed (eg mentioning a wildcard
            // as return type). We need these bare wildcards for "mentionsAny" to work properly.
            // The "correct" approach here to remove wildcards would be to infer the ground non-wildcard parameterization
            // of the lambda but this is pretty deep inside the inference code and not readily usable.
            JClassType lambdaTyCapture = (JClassType) genericLambdaTy;

            // This is the method signature of the lambda, given the formal parameter type of the parent call.
            // The formal type is not instantiated, it may contain type variables of the parent method...
            JMethodSig expectedLambdaMethod = genericLambdaTy.getTypeSystem().sigOf(
                lambda.getFunctionalMethod().getSymbol(),
                lambdaTyCapture.getTypeParamSubst()
            );
            // but if the return type does not contain such tvars, then the parent method type does
            // not depend on the lambda type :)
            return definitely(
                TypeOps.mentionsAny(
                    expectedLambdaMethod.getReturnType(),
                    parentMethod.getTypeParameters()
                )
            );
        }
        return UNKNOWN;
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
