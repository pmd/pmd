/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
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
    public Object visit(ASTCastExpression node, Object data) {
        @Nullable ExprContext context = node.getConversionContextType();
        JTypeMirror operandType = node.getOperand().getTypeMirror();
        JTypeMirror coercionType = node.getCastType().getTypeMirror();

        if (TypeOps.isUnresolvedOrNull(operandType)
            || TypeOps.isUnresolvedOrNull(coercionType)
            || context == null
            || context.getTargetType() == null) {
            return null;
        }

        if (castIsUnnecessary(context, operandType, coercionType)) {
            addViolation(data, node);
        }
        return null;
    }

    private static boolean castIsUnnecessary(ExprContext context, JTypeMirror operandType, JTypeMirror coercionType) {
        if (context.isInvocationContext()) {
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
}
