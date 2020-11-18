/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;

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
                    if (JavaRuleUtil.isDefaultValue(varId.getTypeMirror(), init) && !isOkExpr(init)) {
                        addViolation(data, varId);
                    }
                }
            }
        }
        return data;
    }

    private static boolean isOkExpr(ASTExpression e) {
        return e.descendantsOrSelf().none(it -> it instanceof ASTVariableAccess || it instanceof ASTFieldAccess);
    }
}
