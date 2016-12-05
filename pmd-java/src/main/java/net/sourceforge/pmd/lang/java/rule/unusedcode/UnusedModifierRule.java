/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.unusedcode;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnusedModifierRule extends AbstractJavaRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (!node.isNested()) {
            return super.visit(node, data);
        }

        ASTClassOrInterfaceDeclaration parentClassOrInterface = node
                .getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
        ASTEnumDeclaration parentEnum = node.getFirstParentOfType(ASTEnumDeclaration.class);

        if (node.isInterface() && node.isPublic()) {
            // a public interface
            if (parentClassOrInterface != null && parentClassOrInterface.isInterface()) {
                // within a interface
                addViolation(data, node, getMessage());
            }
        }

        if (node.isInterface() && node.isStatic()) {
            // a static interface
            if (parentClassOrInterface != null || parentEnum != null) {
                // within a interface, class or enum
                addViolation(data, node, getMessage());
            }
        }

        if (!node.isInterface() && (node.isPublic() || node.isStatic())) {
            // a public and/or static class
            if (parentClassOrInterface != null && parentClassOrInterface.isInterface()) {
                // within a interface
                addViolation(data, node, getMessage());
            }
        }

        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isSyntacticallyPublic() || node.isSyntacticallyAbstract()) {
            check(node, data);
        }
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        if (node.isSyntacticallyPublic() || node.isSyntacticallyStatic() || node.isSyntacticallyFinal()) {
            check(node, data);
        }
        return super.visit(node, data);
    }

    private void check(Node fieldOrMethod, Object data) {
        // third ancestor could be an AllocationExpression
        // if this is a method in an anonymous inner class
        Node parent = fieldOrMethod.jjtGetParent().jjtGetParent().jjtGetParent();
        if (parent instanceof ASTClassOrInterfaceDeclaration
                && ((ASTClassOrInterfaceDeclaration) parent).isInterface()) {
            addViolation(data, fieldOrMethod);
        }
    }
}
