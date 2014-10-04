/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.naming;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidFieldNameMatchingMethodNameRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        int n = node.jjtGetNumChildren();
        List<ASTFieldDeclaration> fields = new ArrayList<ASTFieldDeclaration>();
        Set<String> methodNames = new HashSet<String>();
        for (int i = 0; i < n; i++) {
            Node child = node.jjtGetChild(i);
            if (child.jjtGetNumChildren() == 0) {
                continue;
            }
            child = child.jjtGetChild(child.jjtGetNumChildren()-1);
            if (child instanceof ASTFieldDeclaration) {
                fields.add((ASTFieldDeclaration) child);
            } else if (child instanceof ASTMethodDeclaration) {
                methodNames.add(((ASTMethodDeclaration) child).getMethodName().toLowerCase());
            }
        }
        for (ASTFieldDeclaration field : fields) {
            String varName = field.getVariableName().toLowerCase();
            if (methodNames.contains(varName)) {
                addViolation(data, field, field.getVariableName());
            }
        }
        return super.visit(node, data);
    }

}
