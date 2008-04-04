/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.lang.java.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;

public class AvoidFieldNameMatchingTypeName extends AbstractJavaRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTClassOrInterfaceDeclaration cl = node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (cl != null && node.getVariableName().equalsIgnoreCase(cl.getImage())) {
            addViolation(data, node);
        }
        return data;
    }
}
