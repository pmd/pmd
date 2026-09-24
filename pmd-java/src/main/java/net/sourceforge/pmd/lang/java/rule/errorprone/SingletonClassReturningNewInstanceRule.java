/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
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
            asCtx(data).addViolation(node);
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
                .filter(this::mayHoldNewInstance)
                .nonEmpty();
    }

    /**
     * Whether a value assigned to the local variable is a constructor call. A local
     * that only ever receives the value of a field, e.g. {@code Foo temp = (Foo) INSTANCE},
     * returns the existing instance and is not a new one. See #5158.
     */
    private boolean mayHoldNewInstance(ASTVariableAccess variableAccess) {
        JVariableSymbol symbol = variableAccess.getReferencedSym();
        if (symbol == null || !(symbol.tryGetNode() instanceof ASTVariableId)) {
            // unresolved: keep reporting, as before
            return true;
        }
        ASTVariableId varId = (ASTVariableId) symbol.tryGetNode();
        if (isNewInstance(varId.getInitializer())) {
            return true;
        }
        for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
            if (usage.getAccessType() == ASTAssignableExpr.AccessType.WRITE
                    && usage.getParent() instanceof ASTAssignmentExpression
                    && isNewInstance(((ASTAssignmentExpression) usage.getParent()).getRightOperand())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isNewInstance(ASTExpression expr) {
        ASTExpression value = JavaAstUtils.peelCasts(expr);
        if (value instanceof ASTConstructorCall) {
            return true;
        }
        if (value instanceof ASTAssignmentExpression) {
            return isNewInstance(((ASTAssignmentExpression) value).getRightOperand());
        }
        if (value instanceof ASTConditionalExpression) {
            ASTConditionalExpression conditional = (ASTConditionalExpression) value;
            return isNewInstance(conditional.getThenBranch()) || isNewInstance(conditional.getElseBranch());
        }
        return false;
    }

    private boolean isDoubleAssignment(ASTVariableAccess variableAccess) {
        // search in the whole method
        return variableAccess.ancestors(ASTMethodDeclaration.class)
                .descendants(ASTVariableAccess.class)
                // for any writes
                .filter(v -> v.getAccessType() == ASTAssignableExpr.AccessType.WRITE)
                // to the same variable
                .filter(v -> JavaAstUtils.isReferenceToSameVar(variableAccess, v))
                // assignment from a constructor call (next sibling = right hand side) or another Assignment
                .filter(v -> v.getNextSibling() instanceof ASTConstructorCall || v.getNextSibling() instanceof ASTAssignmentExpression)
                // check for both variants
                .filter(v -> {
                    boolean variant1 = false;
                    boolean variant2 = false;

                    // check variant 1: field = localVar = new Singleton()
                    if (v.getNextSibling() instanceof ASTConstructorCall) {
                        if (v.getParent() instanceof ASTAssignmentExpression && v.getParent().getParent() instanceof ASTAssignmentExpression) {
                            ASTAssignmentExpression leftAssignment = (ASTAssignmentExpression) v.getParent().getParent();
                            ASTAssignmentExpression rightAssignment = (ASTAssignmentExpression) v.getParent();

                            boolean fromConstructor = rightAssignment.getRightOperand() instanceof ASTConstructorCall;
                            boolean fromRightToLeft = leftAssignment.getRightOperand() == rightAssignment;
                            boolean leftIsField = false;
                            if (leftAssignment.getLeftOperand() instanceof ASTAssignableExpr.ASTNamedReferenceExpr) {
                                JVariableSymbol symbol = ((ASTAssignableExpr.ASTNamedReferenceExpr) leftAssignment.getLeftOperand()).getReferencedSym();
                                leftIsField = symbol != null && symbol.isField();
                            }
                            variant1 = fromConstructor && fromRightToLeft && leftIsField;
                        }

                    // check variant 2: localVar = field = new Singleton()
                    } else if (v.getNextSibling() instanceof ASTAssignmentExpression) {
                        if (v.getParent() instanceof ASTAssignmentExpression) {
                            ASTAssignmentExpression leftAssignment = (ASTAssignmentExpression) v.getParent();
                            ASTAssignmentExpression rightAssignment = (ASTAssignmentExpression) v.getNextSibling();

                            boolean fromConstructor = rightAssignment.getRightOperand() instanceof ASTConstructorCall;
                            boolean fromRightToLeft = leftAssignment.getRightOperand() == rightAssignment;
                            boolean rightIsField = false;
                            if (rightAssignment.getLeftOperand() instanceof ASTAssignableExpr.ASTNamedReferenceExpr) {
                                JVariableSymbol symbol = ((ASTAssignableExpr.ASTNamedReferenceExpr) rightAssignment.getLeftOperand()).getReferencedSym();
                                rightIsField = symbol != null && symbol.isField();
                            }
                            variant2 = fromConstructor && fromRightToLeft && rightIsField;
                        }
                    }
                    return variant1 || variant2;
                })
                .nonEmpty();
    }

}
