/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;

public class IdempotentOperations extends AbstractJavaRule {

    public Object visit(ASTStatementExpression node, Object data) {
        if (node.jjtGetNumChildren() != 3
                || !(node.jjtGetChild(0) instanceof ASTPrimaryExpression)
                || !(node.jjtGetChild(1) instanceof ASTAssignmentOperator)
                || (((ASTAssignmentOperator) (node.jjtGetChild(1))).isCompound())
                || !(node.jjtGetChild(2) instanceof ASTExpression)
                || node.jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() == 0
                || node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() == 0
        ) {
            return super.visit(node, data);
        }

        Node lhs = node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        if (!(lhs instanceof ASTName)) {
            return super.visit(node, data);
        }

        Node rhs = node.jjtGetChild(2).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
        if (!(rhs instanceof ASTName)) {
            return super.visit(node, data);
        }

        if (!lhs.hasImageEqualTo(rhs.getImage())) {
            return super.visit(node, data);
        }

        if (lhs.jjtGetParent().jjtGetParent().jjtGetNumChildren() > 1) {
            Node n = lhs.jjtGetParent().jjtGetParent().jjtGetChild(1);
            if (n instanceof ASTPrimarySuffix && ((ASTPrimarySuffix) n).isArrayDereference()) {
                return super.visit(node, data);
            }
        }

        if (rhs.jjtGetParent().jjtGetParent().jjtGetNumChildren() > 1) {
            Node n = rhs.jjtGetParent().jjtGetParent().jjtGetChild(1);
            if (n instanceof ASTPrimarySuffix && ((ASTPrimarySuffix) n).isArguments() || ((ASTPrimarySuffix) n).isArrayDereference()) {
                return super.visit(node, data);
            }
        }

        if (lhs.findChildrenOfType(ASTPrimarySuffix.class).size() != rhs.findChildrenOfType(ASTPrimarySuffix.class).size()) {
            return super.visit(node, data);
        }

        addViolation(data, node);
        return super.visit(node, data);
    }
}
