/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
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
        JTypeMirror contextType = node.getConversionContextType();
        JTypeMirror operandType = node.getOperand().getTypeMirror();
        JTypeMirror coercionType = node.getCastType().getTypeMirror();

        if (TypeOps.isUnresolvedOrNull(operandType)
            || TypeOps.isUnresolvedOrNull(coercionType)
            || TypeOps.isUnresolvedOrNull(contextType)) {
            return null;
        }

        if (castIsUnnecessary(contextType, operandType, coercionType)) {
            addViolation(data, node);
        }
        return null;
    }

    private static boolean castIsUnnecessary(JTypeMirror contextType, JTypeMirror operandType, JTypeMirror coercionType) {
        // note: those depend on java5+ autoboxing rules
        boolean isNotNarrowing = TypeConversion.isConvertibleThroughBoxing(operandType, coercionType);
        // tests that actually deleting the cast would not give uncompilable code
        boolean canOperandSuitContext = TypeConversion.isConvertibleThroughBoxing(operandType, contextType);
        return isNotNarrowing && canOperandSuitContext;
    }
}
