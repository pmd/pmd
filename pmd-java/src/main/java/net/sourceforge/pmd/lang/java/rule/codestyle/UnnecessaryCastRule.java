/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ExprContext;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;

/**
 * Detects casts where the operand is already a subtype of the context
 * type, or may be converted to it implicitly.
 */
public class UnnecessaryCastRule extends AbstractJavaRulechainRule {

    public UnnecessaryCastRule() {
        super(ASTCastExpression.class);
    }

    @Override
    public Object visit(ASTCastExpression castExpr, Object data) {
        ASTExpression operand = castExpr.getOperand();

        // eg in
        // Object o = (Integer) 1;

        @Nullable ExprContext context = castExpr.getConversionContextType();    // Object
        JTypeMirror operandType = operand.getTypeMirror();                  // int
        JTypeMirror coercionType = castExpr.getCastType().getTypeMirror();      // Integer

        if (TypeOps.isUnresolvedOrNull(operandType)
            || TypeOps.isUnresolvedOrNull(coercionType)
            || context == null
            || context.getTargetType() == null) {
            return null;
        }

        // Note that we assume that coercionType is convertible to
        // contextType because the code must compile

        if (operand instanceof ASTLambdaExpression || operand instanceof ASTMethodReference) {
            // Then the cast provides a target type for the expression (always).
            // We need to check the enclosing context, as if it's invocation we give up for now
            if (isInvocationContext(castExpr.getConversionContextType())) {
                // Then the cast may be used to determine the overload.
                // We need to treat the casted lambda as a whole unit.
                // todo see below
                return null;
            }

            // Since the code is assumed to compile we'll just assume that coercionType
            // is a functional interface.
            if (context.getTargetType().equals(coercionType)) {
                // then we also know that the context is functional
                addViolation(data, castExpr);
            }
            // otherwise the cast is narrowing, and removing it would
            // change the runtime class of the produced lambda.
            // Eg `SuperItf obj = (SubItf) ()-> {};`
            // If we remove the cast, even if it might compile,
            // the object will not implement SubItf anymore.
            return null;
        }

        if (castIsUnnecessary(context, operandType, coercionType)) {
            addViolation(data, castExpr);
        }
        return null;
    }

    private static boolean castIsUnnecessary(ExprContext context,
                                             JTypeMirror operandType,
                                             JTypeMirror coercionType) {
        if (isInvocationContext(context)) {
            // todo unsupported for now, the cast may be disambiguating overloads
            return false;
        }
        JTypeMirror contextType = context.getTargetType();
        // note: those depend on java5+ autoboxing rules
        boolean isNotNarrowing = TypeConversion.isConvertibleThroughBoxing(operandType, coercionType);
        // tests that actually deleting the cast would not give uncompilable code
        boolean canOperandSuitContext = TypeConversion.isConvertibleThroughBoxing(operandType, contextType);
        return isNotNarrowing && canOperandSuitContext;
    }

    private static boolean isInvocationContext(ExprContext context) {
        return context != null && context.isInvocationContext();
    }
}
