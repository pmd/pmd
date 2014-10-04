/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.naming;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidDollarSignsRule extends AbstractJavaRule {

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
