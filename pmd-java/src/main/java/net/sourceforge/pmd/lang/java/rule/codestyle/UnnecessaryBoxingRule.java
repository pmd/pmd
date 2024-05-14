/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
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
                checkBox((RuleContext) data, "boxing", node, arg);
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
                checkBox((RuleContext) data, "boxing", node, node.getArguments().get(0));
            } else if (isValueOf && isStringValueOf(m) && qualifier != null) {
                checkUnboxing((RuleContext) data, node, qualifier.getTypeMirror());
            } else if (!isValueOf && isUnboxingCall(m) && qualifier != null) {
                checkBox((RuleContext) data, "unboxing", node, qualifier);
            }
        }
        return null;
    }

    private boolean isUnboxingCall(JMethodSig m) {
        return !m.isStatic() && m.getDeclaringType().isBoxedPrimitive() && m.getArity() == 0;
    }

    private boolean isWrapperValueOf(JMethodSig m) {
        return m.isStatic()
            && m.getArity() == 1
            && m.getDeclaringType().isBoxedPrimitive()
            && m.getFormalParameters().get(0).isPrimitive();
    }

    private boolean isStringValueOf(JMethodSig m) {
        return m.isStatic()
                && (m.getArity() == 1 || m.getArity() == 2)
                && m.getDeclaringType().isBoxedPrimitive()
                && TypeTestUtil.isA(String.class, m.getFormalParameters().get(0));
    }

    private void checkBox(
        RuleContext rctx,
        String opKind,
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

        // which basically means testing that sourceExpr -> convOutput
        // may be performed implicitly.

        // We cannot just test compatibility of the source to the ctx,
        // because of situations like
        //   int i = integer.byteValue()
        // where the conversion actually truncates the input value
        // (here we do sourceExpr=Integer (-> convInput=Integer) -> convOutput=byte -> ctx=int).

        JTypeMirror sourceType = convertedExpr.getTypeMirror();
        JTypeMirror conversionOutput = conversionExpr.getTypeMirror();
        ExprContext ctx = conversionExpr.getConversionContext();
        JTypeMirror ctxType = ctx.getTargetType();
        if (ctxType == null && conversionExpr instanceof InvocationNode) {
            ctxType = conversionOutput;
        }

        if (ctxType != null) {

            if (isImplicitlyConvertible(sourceType, conversionOutput)) {

                final String reason;
                if (sourceType.equals(conversionOutput)) {
                    reason = "boxing of boxed value";
                } else if (sourceType.unbox().equals(conversionOutput)) {
                    reason = "explicit unboxing";
                } else if (sourceType.box().equals(conversionOutput)) {
                    reason = "explicit boxing";
                } else if (sourceType.equals(ctxType)) {
                    reason = opKind;
                } else {
                    reason = "explicit conversion from "
                        + TypePrettyPrint.prettyPrintWithSimpleNames(sourceType)
                        + " to " + TypePrettyPrint.prettyPrintWithSimpleNames(ctxType);
                }

                rctx.addViolation(conversionExpr, reason);
            }
        }
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
        }
        return i.box().isSubtypeOf(o.box())
            || i.unbox().isSubtypeOf(o.unbox());
    }

    /**
     * Whether {@code S <: T}, but ignoring primitive widening.
     * {@code isReferenceSubtype(int, double) == false} even though
     * {@code int.isSubtypeOf(double)}.
     */
    private static boolean isReferenceSubtype(JTypeMirror s, JTypeMirror t) {
        return s.isPrimitive() ? t.equals(s)
                               : s.isSubtypeOf(t);
    }

}
