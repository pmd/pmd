/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class IdempotentOperationsRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTStatementExpression node, Object data) {
        if (node.getNumChildren() != 3 || !(node.getChild(0) instanceof ASTPrimaryExpression)
                || !(node.getChild(1) instanceof ASTAssignmentOperator)
                || ((ASTAssignmentOperator) node.getChild(1)).isCompound()
                || !(node.getChild(2) instanceof ASTExpression)
                || node.getChild(0).getChild(0).getNumChildren() == 0
                || node.getChild(2).getChild(0).getChild(0).getNumChildren() == 0) {
            return super.visit(node, data);
        }

        Node lhs = node.getChild(0).getChild(0).getChild(0);
        if (!(lhs instanceof ASTName)) {
            return super.visit(node, data);
        }

        Node rhs = node.getChild(2).getChild(0).getChild(0).getChild(0);
        if (!(rhs instanceof ASTName)) {
            return super.visit(node, data);
        }

        if (!lhs.hasImageEqualTo(rhs.getImage())) {
            return super.visit(node, data);
        }

        if (lhs.getParent().getParent().getNumChildren() > 1) {
            Node n = lhs.getParent().getParent().getChild(1);
            if (n instanceof ASTPrimarySuffix && ((ASTPrimarySuffix) n).isArrayDereference()) {
                return super.visit(node, data);
            }
        }

        if (rhs.getParent().getParent().getNumChildren() > 1) {
            Node n = rhs.getParent().getParent().getChild(1);
            if (n instanceof ASTPrimarySuffix && ((ASTPrimarySuffix) n).isArguments()
                    || ((ASTPrimarySuffix) n).isArrayDereference()) {
                return super.visit(node, data);
            }
        }

        if (lhs.findDescendantsOfType(ASTPrimarySuffix.class).size() != rhs
                .findDescendantsOfType(ASTPrimarySuffix.class).size()) {
            return super.visit(node, data);
        }

        List<ASTPrimarySuffix> lhsSuffixes = lhs.getParent().getParent()
                .findDescendantsOfType(ASTPrimarySuffix.class);
        List<ASTPrimarySuffix> rhsSuffixes = rhs.getParent().getParent()
                .findDescendantsOfType(ASTPrimarySuffix.class);
        if (lhsSuffixes.size() != rhsSuffixes.size()) {
            return super.visit(node, data);
        }

        for (int i = 0; i < lhsSuffixes.size(); i++) {
            ASTPrimarySuffix l = lhsSuffixes.get(i);
            ASTPrimarySuffix r = rhsSuffixes.get(i);

            if (!l.hasImageEqualTo(r.getImage())) {
                return super.visit(node, data);
            }
        }

        addViolation(data, node);
        return super.visit(node, data);
    }
}
