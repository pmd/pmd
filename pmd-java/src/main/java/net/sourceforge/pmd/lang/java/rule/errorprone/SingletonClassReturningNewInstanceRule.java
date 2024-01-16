/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

public class SingletonClassReturningNewInstanceRule extends AbstractJavaRulechainRule {

    public SingletonClassReturningNewInstanceRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isVoid() || !"getInstance".equals(node.getName())) {
            return data;
        }

        DescendantNodeStream<ASTReturnStatement> rsl = node.descendants(ASTReturnStatement.class);
        if (returnsNewInstances(rsl) || returnsLocalVariables(rsl)) {
            addViolation(data, node);
        }
        return data;
    }

    private boolean returnsNewInstances(NodeStream<ASTReturnStatement> returns) {
        return returns.descendants(ASTConstructorCall.class).nonEmpty();
    }

    private boolean returnsLocalVariables(NodeStream<ASTReturnStatement> returns) {
        return returns.children(ASTVariableAccess.class)
                .filter(JavaAstUtils::isReferenceToLocal)
                .filterNot(this::isDoubleAssignment)
                .nonEmpty();
    }

    private boolean isDoubleAssignment(ASTVariableAccess variableAccess) {
        // search in the whole method
        return variableAccess.ancestors(ASTMethodDeclaration.class)
                .descendants(ASTVariableAccess.class)
                // for any writes
                .filter(v -> v.getAccessType() == ASTAssignableExpr.AccessType.WRITE)
                // to the same variable
                .filter(v -> JavaAstUtils.isReferenceToSameVar(variableAccess, v))
                // assignment from a constructor call (next sibling = right hand side)
                .filter(v -> v.getNextSibling() instanceof ASTConstructorCall)
                // and double assignment
                .filter(v -> v.getParent() instanceof ASTAssignmentExpression && v.getParent().getParent() instanceof ASTAssignmentExpression)
                .filter(v -> {
                    ASTAssignmentExpression rightAssignment = (ASTAssignmentExpression) v.getParent();
                    ASTAssignmentExpression leftAssignment = (ASTAssignmentExpression) v.getParent().getParent();

                    boolean fromConstructor = rightAssignment.getRightOperand() instanceof ASTConstructorCall;
                    boolean fromRightToLeft = leftAssignment.getRightOperand() == rightAssignment;
                    boolean leftIsField = false;
                    if (leftAssignment.getLeftOperand() instanceof ASTAssignableExpr.ASTNamedReferenceExpr) {
                        JVariableSymbol symbol = ((ASTAssignableExpr.ASTNamedReferenceExpr) leftAssignment.getLeftOperand()).getReferencedSym();
                        leftIsField = symbol.isField();
                    }
                    return fromConstructor && fromRightToLeft && leftIsField;
                })
                .nonEmpty();
    }

}
