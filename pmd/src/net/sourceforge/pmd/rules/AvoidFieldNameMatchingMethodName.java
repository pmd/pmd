/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

import java.util.Iterator;
import java.util.List;

public class AvoidFieldNameMatchingMethodName extends AbstractRule {

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
            ASTClassOrInterfaceDeclaration cl = (ASTClassOrInterfaceDeclaration) node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
            if (cl != null) {
                List methods = cl.findChildrenOfType(ASTMethodDeclaration.class);
                for (Iterator it = methods.iterator(); it.hasNext();) {
                    ASTMethodDeclaration m = (ASTMethodDeclaration) it.next();
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
