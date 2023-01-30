/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;


public class AddEmptyStringRule extends AbstractJavaRulechainRule {

    public AddEmptyStringRule() {
        super(ASTStringLiteral.class);
    }

    @Override
    public Object visit(ASTStringLiteral node, Object data) {
        if (!node.isEmpty()) {
            return null;
        }
        JavaNode parent = node.getParent();
        checkExpr(data, parent);
        if (parent instanceof ASTVariableDeclarator) {
            ASTVariableDeclaratorId varId = ((ASTVariableDeclarator) parent).getVarId();
            if (varId.hasModifiers(JModifier.FINAL)) {
                for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
                    checkExpr(data, usage.getParent());
                }
            }
        }
        return null;
    }

    private void checkExpr(Object data, JavaNode parent) {
        if (JavaAstUtils.isInfixExprWithOperator(parent, BinaryOp.ADD)
            && parent.ancestors(ASTAnnotation.class).isEmpty()) {
            addViolation(data, parent);
        }
    }
}
