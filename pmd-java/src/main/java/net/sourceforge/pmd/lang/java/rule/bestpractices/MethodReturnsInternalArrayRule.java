/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

/**
 * Implementation note: this rule currently ignores return types of y.x.z,
 * currently it handles only local type fields. Created on Jan 17, 2005
 */
public class MethodReturnsInternalArrayRule extends AbstractJavaRulechainRule {

    public MethodReturnsInternalArrayRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (!method.getResultTypeNode().getTypeMirror().isArray()
            || method.getVisibility() == Visibility.V_PRIVATE) {
            return data;
        }

        for (ASTReturnStatement returnStmt : method.descendants(ASTReturnStatement.class)) {
            ASTExpression expr = returnStmt.getExpr();
            if (expr instanceof ASTNamedReferenceExpr) {
                ASTNamedReferenceExpr reference = (ASTNamedReferenceExpr) expr;

                if (JavaRuleUtil.isRefToFieldOfThisInstance(reference)) {
                    addViolation(data, returnStmt, reference.getName());
                } else {
                    // considers static, non-final fields
                    JVariableSymbol symbol = reference.getReferencedSym();
                    if (symbol instanceof JFieldSymbol) {
                        JFieldSymbol field = (JFieldSymbol) symbol;
                        if (field.isStatic() && !field.isFinal()) {
                            addViolation(data, returnStmt, reference.getName());
                        }
                    }
                }
            }
        }
        return data;
    }
}
