/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimExpr;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTArrayTypeDim;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
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
                        if (field.isStatic() && (!field.isFinal() || !hasZeroLengthArrayInitializer(field))) {
                            addViolation(data, returnStmt, reference.getName());
                        }
                    }
                }
            }
        }
        return data;
    }

    private static boolean hasZeroLengthArrayInitializer(JFieldSymbol sym) {
        return NodeStream.of(sym.tryGetNode())
                         .map(ASTVariableDeclaratorId::getInitializer)
                         .filter(MethodReturnsInternalArrayRule::isZeroLengthArrayExpr)
                         .nonEmpty();
    }

    private static boolean isZeroLengthArrayExpr(ASTExpression expr) {
        if (expr instanceof ASTArrayInitializer) {
            // {}
            return ((ASTArrayInitializer) expr).length() == 0;
        } else if (expr instanceof ASTArrayAllocation) {
            ASTArrayInitializer init = ((ASTArrayAllocation) expr).getArrayInitializer();
            if (init != null) {
                // new int[] {}
                return init.length() == 0;
            } else {
                // new int[0]
                ASTArrayTypeDim lastChild = ((ASTArrayAllocation) expr).getTypeNode().getDimensions().getLastChild();
                if (lastChild instanceof ASTArrayDimExpr) {
                    return JavaRuleUtil.isIntLit(((ASTArrayDimExpr) lastChild).getLengthExpression(), 0);
                }
            }
        }
        return false;
    }
}
