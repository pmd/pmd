package net.sourceforge.pmd.rules.naming;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

public class AvoidDollarSigns extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.getImage().indexOf('$') != -1) {
            addViolation(data, node);
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.getImage().indexOf('$') != -1) {
            addViolation(data, node);
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclarator node, Object data) {
        if (node.getImage().indexOf('$') != -1) {
            addViolation(data, node);
            return data;
        }
        return super.visit(node, data);
    }

}
