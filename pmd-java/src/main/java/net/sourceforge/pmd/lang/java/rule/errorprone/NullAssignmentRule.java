/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLambdaExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

public class NullAssignmentRule extends AbstractJavaRule {

    public NullAssignmentRule() {
        addRuleChainVisit(ASTNullLiteral.class);
    }

    @Override
    public Object visit(ASTNullLiteral node, Object data) {

        if (node.getNthParent(5) instanceof ASTStatementExpression) {
            ASTStatementExpression n = (ASTStatementExpression) node.getNthParent(5);

            if (isAssignmentToFinalField(n)) {
                return data;
            }

            if (n.getNumChildren() > 2 && n.getChild(1) instanceof ASTAssignmentOperator) {
                addViolation(data, node);
            }
        } else if (node.getNthParent(4) instanceof ASTConditionalExpression) {
            // "false" expression of ternary
            if (isBadTernary((ASTConditionalExpression) node.getNthParent(4))) {
                addViolation(data, node);
            }
        } else if (node.getNthParent(5) instanceof ASTConditionalExpression
                && node.getNthParent(4) instanceof ASTExpression) {
            // "true" expression of ternary
            if (isBadTernary((ASTConditionalExpression) node.getNthParent(5))) {
                addViolation(data, node);
            }
        }

        return data;
    }

    private boolean isAssignmentToFinalField(ASTStatementExpression n) {
        ASTName name = n.getFirstDescendantOfType(ASTName.class);
        return name != null && name.getNameDeclaration() instanceof VariableNameDeclaration
                && ((AccessNode) ((VariableNameDeclaration) name.getNameDeclaration()).getAccessNodeParent()).isFinal();
    }

    private boolean isBadTernary(ASTConditionalExpression ternary) {
        boolean isInitializer = false;

        ASTVariableInitializer variableInitializer = ternary.getFirstParentOfType(ASTVariableInitializer.class);
        if (variableInitializer != null) {
            ASTBlockStatement statement = ternary.getFirstParentOfType(ASTBlockStatement.class);
            isInitializer = statement == variableInitializer.getFirstParentOfType(ASTBlockStatement.class);
        }

        return !(ternary.getChild(0) instanceof ASTEqualityExpression)
                && !isInitializer
                && !(ternary.getNthParent(2) instanceof ASTReturnStatement)
                && !(ternary.getNthParent(2) instanceof ASTLambdaExpression);
    }
}
