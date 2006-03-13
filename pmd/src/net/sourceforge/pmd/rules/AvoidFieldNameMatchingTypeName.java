/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;

public class AvoidFieldNameMatchingTypeName extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTClassOrInterfaceDeclaration cl = (ASTClassOrInterfaceDeclaration) node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        if (cl != null && node.getVariableName().toLowerCase().equals(cl.getImage().toLowerCase())) {
            addViolation(data, node);
        }
        return data;
    }
}
