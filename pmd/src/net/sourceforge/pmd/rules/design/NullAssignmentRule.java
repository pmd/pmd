/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTConditionalExpression;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNullLiteral;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

// TODO - should check that this is not the first assignment.  e.g., this is OK:
// Object x;
// x = null;
public class NullAssignmentRule extends AbstractRule {

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
        ASTName name = n.getFirstChildOfType(ASTName.class);
        return name != null
                && name.getNameDeclaration() instanceof VariableNameDeclaration
                && ((VariableNameDeclaration) name.getNameDeclaration()).getAccessNodeParent().isFinal();
    }

    private boolean isBadTernary(ASTConditionalExpression n) {
        return n.isTernary() && !(n.jjtGetChild(0) instanceof ASTEqualityExpression);
    }
}
