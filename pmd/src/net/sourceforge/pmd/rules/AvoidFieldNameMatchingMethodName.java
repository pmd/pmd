/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AvoidFieldNameMatchingMethodName extends AbstractJavaRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        String varName = node.getVariableName();
        String fieldDeclaringType = getDeclaringType(node);
        if (varName != null) {
            varName = varName.toLowerCase();
            ASTClassOrInterfaceDeclaration cl = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
            if (cl != null) {
                List<ASTMethodDeclaration> methods = cl.findChildrenOfType(ASTMethodDeclaration.class);
                for (ASTMethodDeclaration m: methods) {
                    //Make sure we are comparing fields and methods inside same type
                    if (fieldDeclaringType.equals(getDeclaringType(m))) {
                        String n = m.getMethodName();
                        if (varName.equals(n.toLowerCase())) {
                            addViolation(data, node);
                        }
                    }
                }
            }
        }
        return data;
    }
}
