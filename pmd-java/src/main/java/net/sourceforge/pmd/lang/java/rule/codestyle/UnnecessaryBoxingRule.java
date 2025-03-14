/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.QualifiableExpression;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext.ExprContextKind;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 *
 */
public class UnnecessaryBoxingRule extends AbstractJavaRulechainRule {

    private static final Set<String> INTERESTING_NAMES = setOf(
        "valueOf",
        "booleanValue",
        "charValue",
        "byteValue",
        "shortValue",
        "intValue",
        "longValue",
        "floatValue",
        "doubleValue"
    );

    public UnnecessaryBoxingRule() {
        super(ASTMethodCall.class, ASTConstructorCall.class);
    }

    @Override
    public Object visit(ASTConstructorCall node, Object data) {
        if (node.getTypeMirror().isBoxedPrimitive()) {
            ASTExpression arg = ASTList.singleOrNull(node.getArguments());
            if (arg == null) {
                return null;
            }
            JTypeMirror argT = arg.getTypeMirror();
            if (argT.isPrimitive()) {
                checkBox((RuleContext) data, node, arg);
            }
        }
        return null;
    }


    @Override
    public Object visit(ASTMethodCall node, Object data) {
        if (INTERESTING_NAMES.contains(node.getMethodName())) {
            OverloadSelectionResult overload = node.getOverloadSelectionInfo();
            if (overload.isFailed()) {
                return null;
            }
            JMethodSig m = overload.getMethodType();
            boolean isValueOf = "valueOf".equals(node.getMethodName());
            ASTExpression qualifier = node.getQualifier();

            if (isValueOf && isWrapperValueOf(m)) {
                checkBox((RuleContext) data, node, node.getArguments().get(0));
            } else if (isValueOf && isBoxValueOfString(m)) {
                checkUnboxing((RuleContext) data, node, m.getDeclaringType());
            } else if (!isValueOf && qualifier != null && isUnboxingCall(m)) {
                checkBox((RuleContext) data, node, qualifier);
            }
        }
        return null;
    }

    private boolean isUnboxingCall(JMethodSig m) {
        return !m.isStatic() && m.getDeclaringType().isBoxedPrimitive() && m.getArity() == 0
            && m.getReturnType().isPrimitive();
    }

    private boolean isWrapperValueOf(JMethodSig m) {
        return m.isStatic()
            && m.getArity() == 1
            && m.getDeclaringType().isBoxedPrimitive()
            && m.getFormalParameters().get(0).isPrimitive();
    }

    private boolean isBoxValueOfString(JMethodSig m) {
        // eg Integer.valueOf("2")
        return m.isStatic()
                && (m.getArity() == 1 || m.getArity() == 2)
                && m.getDeclaringType().isBoxedPrimitive()
                && TypeTestUtil.isA(String.class, m.getFormalParameters().get(0));
    }

    private void checkBox(
        RuleContext rctx,
        ASTExpression conversionExpr,
        ASTExpression convertedExpr
    ) {
        // the conversion looks like
        //  CTX _ = conversion(sourceExpr)

        // we have the following data flow:
        //      sourceExpr -> convInput -> convOutput -> ctx
        //                 1            2             3
        // where 1 and 3 are implicit conversions which we assume are
        // valid because the code should compile.

        // we want to report a violation if this is equivalent to
        //      sourceExpr -> ctx

        // which means testing that
        // 1. the result of the implicit conversion of sourceExpr
        // with context type ctx is the same type as the result of conversion 3
        // 2. conversion 2 does not truncate the value

        // We cannot just test compatibility of the source to the ctx,
        // because of situations like
        //   int i = integer.byteValue()
        // where the conversion actually truncates the input value
        // (here we do sourceExpr=Integer (-> convInput=Integer) -> convOutput=byte -> ctx=int).

        JTypeMirror sourceType = convertedExpr.getTypeMirror();
        JTypeMirror conversionOutput = conversionExpr.getTypeMirror();
        ExprContext ctx = conversionExpr.getConversionContext();
        JTypeMirror ctxType = ctx.getTargetType();

        if (sourceType.isPrimitive()
            && !conversionOutput.isPrimitive()
            && ctxType == null
            && isObjectConversionNecessary(conversionExpr)) {
            // eg Integer.valueOf(2).equals(otherInteger)
            return;
        }

        String reason = null;
        if (sourceType.equals(conversionOutput)) {
            reason = "boxing of boxed value";
        } else if (isImplicitlyTypedLambdaReturnExpr(conversionExpr)
            || ctxType != null && conversionIsImplicitlyRealisable(sourceType, ctxType, ctx, conversionOutput)) {
            if (sourceType.unbox().equals(conversionOutput)) {
                reason = "explicit unboxing";
            } else if (sourceType.box().equals(conversionOutput)) {
                reason = "explicit boxing";
            } else if (ctxType != null) {
                reason = "explicit conversion from " + TypePrettyPrint.prettyPrintWithSimpleNames(sourceType)
                    + " to " + TypePrettyPrint.prettyPrintWithSimpleNames(ctxType);
                if (!conversionOutput.equals(ctxType)) {
                    reason += " through " + TypePrettyPrint.prettyPrintWithSimpleNames(conversionOutput);
                }
            }

        }
        if (reason != null) {
            rctx.addViolation(conversionExpr, reason);
        }
    }


    private static boolean conversionIsImplicitlyRealisable(JTypeMirror sourceType, JTypeMirror ctxType, ExprContext ctx, JTypeMirror conversionOutput) {
        JTypeMirror conv = implicitConversionResult(sourceType, ctxType, ctx.getKind());
        return conv != null
            && conv.equals(implicitConversionResult(conversionOutput, ctxType, ctx.getKind()))
            && conversionDoesNotChangesValue(sourceType, conversionOutput);
    }


    private boolean isImplicitlyTypedLambdaReturnExpr(ASTExpression e) {
        JavaNode parent = e.getParent();
        if (isImplicitlyTypedLambda(parent)) {
            return true;
        } else if (parent instanceof ASTReturnStatement) {
            JavaNode target = JavaAstUtils.getReturnTarget((ASTReturnStatement) parent);
            return isImplicitlyTypedLambda(target);
        }
        return false;
    }


    private static boolean isImplicitlyTypedLambda(JavaNode e) {
        return e instanceof ASTLambdaExpression && !((ASTLambdaExpression) e).isExplicitlyTyped();
    }

    private boolean isObjectConversionNecessary(ASTExpression e) {
        JavaNode parent = e.getParent();
        return e.getIndexInParent() == 0 && parent instanceof QualifiableExpression;
    }


    private void checkUnboxing(
            RuleContext rctx,
            ASTMethodCall methodCall,
            JTypeMirror conversionOutput
    ) {
        // methodCall is e.g. Integer.valueOf("42")
        // this checks, whether the resulting type "Integer" is e.g. assigned to an "int"
        // which triggers implicit unboxing.
        ExprContext ctx = methodCall.getConversionContext();
        JTypeMirror ctxType = ctx.getTargetType();

        if (ctxType != null) {
            if (isImplicitlyConvertible(conversionOutput, ctxType)) {
                if (conversionOutput.unbox().equals(ctxType)) {
                    rctx.addViolation(methodCall, "implicit unboxing. Use "
                            + conversionOutput.getSymbol().getSimpleName() + ".parse"
                            + StringUtils.capitalize(ctxType.getSymbol().getSimpleName()) + "(...) instead");
                }
            }
        }
    }

    private boolean isImplicitlyConvertible(JTypeMirror i, JTypeMirror o) {
        if (i.isBoxedPrimitive() && o.isBoxedPrimitive()) {
            // There is no implicit conversions between box types,
            // only between primitives
            return i.equals(o);
        } else if (i.isPrimitive() && o.isPrimitive()) {
            return i.isSubtypeOf(o);
        } else {
            return i.unbox().equals(o.unbox());
        }
    }


    /**
     * Type of the converted i in context ctx. If no implicit
     * conversion is possible then return null.
     */
    private static @Nullable JTypeMirror implicitConversionResult(JTypeMirror i, JTypeMirror ctx, ExprContextKind kind) {
        if (kind == ExprContextKind.CAST) {
            // In cast contexts conversions are less restrictive.
            if (i.isPrimitive() != ctx.isPrimitive()) {
                // Whether an unboxing or boxing conversion may occur depends on whether
                // the expression has a primitive type or not (not on the cast type).
                // https://docs.oracle.com/javase/specs/jls/se22/html/jls-5.html#jls-5.5
                return i.isPrimitive() ? i.box() : i.unbox();
            } else if (i.isNumeric() && ctx.isNumeric()) {
                // then narrowing or widening conversions occur to transform i to ctx
                return ctx;
            }
            // otherwise no conversion occurs
            return i;
        }
        if (!ctx.isPrimitive()) {
            // boxing
            return i.box().isSubtypeOf(ctx) ? i.box() : null;
        } else if (i.isBoxedPrimitive()) {
            // unboxing then optional widening
            return i.unbox().isSubtypeOf(ctx) ? ctx : null;
        } else if (i.isPrimitive()) {
            // widening
            return i.isSubtypeOf(ctx) ? ctx : null;
        }
        return null;
    }


    /**
     * Whether the explicit conversion from i to o changes the value.
     * This is e.g. truncating an integer.
     */
    private static boolean conversionDoesNotChangesValue(JTypeMirror i, JTypeMirror o) {
        return i.box().isSubtypeOf(o.box()) || i.unbox().isSubtypeOf(o.unbox());
    }

}
