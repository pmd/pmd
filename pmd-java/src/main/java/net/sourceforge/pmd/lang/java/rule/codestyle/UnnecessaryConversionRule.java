/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
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
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;

/**
 *
 */
public class UnnecessaryConversionRule extends AbstractJavaRulechainRule {

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

    public UnnecessaryConversionRule() {
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
                checkBox((RuleContext) data, "boxing", node, arg, node.getMethodType().getFormalParameters().get(0));
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
                checkBox((RuleContext) data, "boxing", node, node.getArguments().get(0), m.getFormalParameters().get(0));
            } else if (!isValueOf && isUnboxingCall(m) && qualifier != null) {
                checkBox((RuleContext) data, "unboxing", node, qualifier, qualifier.getTypeMirror());
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

    private void checkBox(
        RuleContext rctx,
        String opKind,
        ASTExpression conversionExpr,
        ASTExpression convertedExpr,
        JTypeMirror conversionInput
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

        // which basically means testing that convInput -> convOutput
        // may be performed implicitly.

        // We cannot just test compatibility of the source to the ctx,
        // because of situations like
        //   int i = integer.byteValue()
        // where the conversion actually truncates the input value.

        JTypeMirror sourceType = convertedExpr.getTypeMirror();
        JTypeMirror conversionOutput = conversionExpr.getTypeMirror();
        ExprContext ctx = conversionExpr.getConversionContext();
        JTypeMirror ctxType = ctx.getTargetType();
        if (ctxType == null && conversionExpr instanceof InvocationNode) {
            ctxType = conversionOutput;
        }

        if (ctxType != null) {

            if (isImplicitlyConvertible(conversionInput, conversionOutput)) {

                boolean simpleConv = isReferenceSubtype(sourceType, conversionInput);

                final String reason;
                if (simpleConv && conversionInput.unbox().equals(conversionOutput)) {
                    reason = "explicit unboxing";
                } else if (simpleConv && conversionInput.box().equals(conversionOutput)) {
                    reason = "explicit boxing";
                } else if (sourceType.equals(conversionOutput)) {
                    reason = "boxing of boxed value";
                } else {
                    if (sourceType.equals(ctxType)) {
                        reason = opKind;
                    } else {
                        reason = "explicit conversion from " + TypePrettyPrint.prettyPrintWithSimpleNames(sourceType) + " to " + TypePrettyPrint.prettyPrintWithSimpleNames(ctxType);
                    }
                }

                addViolation(rctx, conversionExpr, reason);
            }
        }
    }

    private boolean isImplicitlyConvertible(JTypeMirror i, JTypeMirror o) {
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
