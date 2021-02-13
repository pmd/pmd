/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;

/**
 * Detects redundant field initializers, i.e. the field initializer expressions
 * the JVM would assign by default.
 *
 * @author lucian.ciufudean@gmail.com
 * @since Apr 10, 2009
 */
public class RedundantFieldInitializerRule extends AbstractJavaRulechainRule {

    public RedundantFieldInitializerRule() {
        super(ASTFieldDeclaration.class);
    }

    @Override
    public Object visit(ASTFieldDeclaration fieldDeclaration, Object data) {
        if (!fieldDeclaration.hasModifiers(JModifier.FINAL)) {
            for (ASTVariableDeclaratorId varId : fieldDeclaration.getVarIds()) {
                ASTExpression init = varId.getInitializer();
                if (init != null) {
                    if (isDefaultValue(varId.getTypeMirror(), init)) {
                        addViolation(data, varId);
                    }
                }
            }
        }
        return data;
    }

    private boolean isDefaultValue(JTypeMirror type, ASTExpression expr) {
        if (type.isPrimitive()) {
            if (type.isPrimitive(PrimitiveTypeKind.BOOLEAN)) {
                return expr instanceof ASTBooleanLiteral && !((ASTBooleanLiteral) expr).isTrue();
            } else {
                if (!isOkExpr(expr)) {
                    // whitelist named constants or calculations involving them
                    return false;
                }
                Object constValue = expr.getConstValue();
                return constValue instanceof Number && ((Number) constValue).doubleValue() == 0d
                    || constValue instanceof Character && constValue.equals('\u0000');
            }
        } else {
            return expr instanceof ASTNullLiteral;
        }
    }

    private static boolean isOkExpr(ASTExpression e) {
        return e.descendantsOrSelf().none(it -> it instanceof ASTVariableAccess || it instanceof ASTFieldAccess);
    }
}
