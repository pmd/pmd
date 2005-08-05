/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTConditionalExpression;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNullLiteral;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.Iterator;
import java.util.Map;

// Would this be simplified by using DFA somehow?
public class NullAssignmentRule extends AbstractRule {

    public Object visit(ASTNullLiteral node, Object data) {
        if (get5thParent(node) instanceof ASTStatementExpression) {
            ASTStatementExpression n = (ASTStatementExpression)get5thParent(node);

            if (isAssignmentToFinalField(n)) {
                return data;
            }

            if (n.jjtGetNumChildren() > 2 && n.jjtGetChild(1) instanceof ASTAssignmentOperator) {
                addViolation(data, node);
            }
        } else if (get4thParent(node) instanceof ASTConditionalExpression) {
            checkTernary((ASTConditionalExpression)get4thParent(node), data, node);
        } else if (get5thParent(node) instanceof ASTConditionalExpression) {
            checkTernary((ASTConditionalExpression)get5thParent(node), data, node);
        }

        return data;
    }

    private boolean isAssignmentToFinalField(ASTStatementExpression n) {
        ASTName name = (ASTName)n.getFirstChildOfType(ASTName.class);
        if (name != null) {
            Map vars = name.getScope().getEnclosingClassScope().getVariableDeclarations();
            for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
                VariableNameDeclaration vnd = (VariableNameDeclaration)i.next();
                if (vnd.getImage().equals(name.getImage()) && vnd.getAccessNodeParent().isFinal()) {
                    return true;
                }
            }
        }
        return false;
    }

    private Node get4thParent(ASTNullLiteral node) {
        return node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent();
    }

    private Node get5thParent(ASTNullLiteral node) {
        return get4thParent(node).jjtGetParent();
    }

    private void checkTernary(ASTConditionalExpression n, Object data, ASTNullLiteral node) {
        if (n.isTernary() && !(n.jjtGetChild(0) instanceof ASTEqualityExpression)) {
            addViolation(data, node);
        }
    }
}
