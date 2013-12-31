/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.controversial;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

// TODO - should check that this is not the first assignment.  e.g., this is OK:
// Object x;
// x = null;
public class NullAssignmentRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTNullLiteral node, Object data) {

        if (node.getNthParent(5) instanceof ASTStatementExpression) {
            ASTStatementExpression n = (ASTStatementExpression) node.getNthParent(5);

            if (isAssignmentToFinalField(n)) {
                return data;
            }

            if (n.jjtGetNumChildren() > 2 && n.jjtGetChild(1) instanceof ASTAssignmentOperator) {
                addViolation(data, node);
            }
        } else if (node.getNthParent(4) instanceof ASTConditionalExpression) {
            // "false" expression of ternary
            if (isBadTernary((ASTConditionalExpression)node.getNthParent(4))) {
                addViolation(data, node);
            }
        } else if (node.getNthParent(5) instanceof ASTConditionalExpression && node.getNthParent(4) instanceof ASTExpression) {
            // "true" expression of ternary
            if (isBadTernary((ASTConditionalExpression)node.getNthParent(5))) {
                addViolation(data, node);
            }
        }

        return data;
    }

    private boolean isAssignmentToFinalField(ASTStatementExpression n) {
        ASTName name = n.getFirstDescendantOfType(ASTName.class);
        return name != null
                && name.getNameDeclaration() instanceof VariableNameDeclaration
                && ((AccessNode) ((VariableNameDeclaration) name.getNameDeclaration()).getAccessNodeParent()).isFinal();
    }

    private boolean isBadTernary(ASTConditionalExpression n) {
        return n.isTernary() && !(n.jjtGetChild(0) instanceof ASTEqualityExpression);
    }
}
