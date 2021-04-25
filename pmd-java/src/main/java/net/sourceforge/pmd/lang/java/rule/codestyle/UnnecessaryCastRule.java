/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodReference;
import net.sourceforge.pmd.lang.java.ast.ExprContext;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
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

        @Nullable ExprContext context = castExpr.getConversionContext();        // Object
        JTypeMirror coercionType = castExpr.getCastType().getTypeMirror();      // Integer
        JTypeMirror operandType = operand.getTypeMirror();                      // int

        if (TypeOps.isUnresolvedOrNull(operandType)
            || TypeOps.isUnresolvedOrNull(coercionType)
            || context.isMissing()) {
            return null;
        }

        // Note that we assume that coercionType is convertible to
        // contextType because the code must compile

        if (operand instanceof ASTLambdaExpression || operand instanceof ASTMethodReference) {
            // Then the cast provides a target type for the expression (always).
            // We need to check the enclosing context, as if it's invocation we give up for now
            if (castExpr.getConversionContext().isInvocationContext()) {
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
            return null;
        }

        boolean isInTernary = castExpr.getParent() instanceof ASTConditionalExpression;

        if (castIsUnnecessary(context, coercionType, operandType, isInTernary)) {
            reportCast(castExpr, data);
        }
        return null;
    }

    private void reportCast(ASTCastExpression castExpr, Object data) {
        addViolation(data, castExpr, PrettyPrintingUtil.prettyPrintTypeWithTargs(castExpr.getCastType()));
    }

    private boolean castIsUnnecessary(@NonNull ExprContext context, JTypeMirror coercionType, JTypeMirror operandType, boolean isInTernary) {
        if (isInTernary) {
            return castIsUnnecessaryInTernary(operandType, coercionType);
        } else {
            return castIsUnnecessary(context, operandType, coercionType);
        }
    }

    /**
     *
     */
    private static boolean castIsUnnecessary(ExprContext context,
                                             JTypeMirror operandType,
                                             JTypeMirror coercionType) {
        if (context.isInvocationContext()) {
            // todo unsupported for now, the cast may be disambiguating overloads
            return false;
        }
        JTypeMirror contextType = context.getTargetType();

        {
            boolean isNarrowing = !TypeConversion.isConvertibleUsingBoxing(operandType, coercionType);
            if (isNarrowing) {
                return false;
            }
        }

        // tests that actually deleting the cast would not give uncompilable code
        boolean canOperandSuitContext;
        if (context.isCastContext()) {
            // then boxing/unboxing are restricted
            canOperandSuitContext = TypeConversion.isConvertibleInCastContext(operandType, contextType);
        } else {
            canOperandSuitContext = TypeConversion.isConvertibleUsingBoxing(operandType, contextType);
        }
        boolean isBoxingFollowingCast = contextType.isPrimitive() != coercionType.isPrimitive();
        boolean boxingBehaviorIsSame = !isBoxingFollowingCast || operandType.unbox().isSubtypeOf(contextType.unbox());

        return canOperandSuitContext && boxingBehaviorIsSame;
    }

    /**
     * A cast in a ternary branch may be necessary in more cases
     * as both branches influence the target type.
     */
    private static boolean castIsUnnecessaryInTernary(JTypeMirror operandType, JTypeMirror coercionType) {
        return operandType.equals(coercionType);
    }

}
