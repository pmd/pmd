/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.unusedcode;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnusedModifierRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        if (node.isStatic()) {
            // a static enum
            addViolation(data, node, getMessage());
        }

        return super.visit(node, data);
    }

    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        if (node.isAbstract()) {
            // an abstract annotation
            addViolation(data, node, getMessage());
        }

        if (!node.isNested()) {
            return super.visit(node, data);
        }

        Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
        boolean isParentInterfaceOrAnnotation = parent instanceof ASTAnnotationTypeDeclaration
                || parent instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) parent).isInterface();

        // a public annotation within an interface or annotation
        if (node.isPublic() && isParentInterfaceOrAnnotation) {
            addViolation(data, node, getMessage());
        }

        if (node.isStatic()) {
            // a static annotation
            addViolation(data, node, getMessage());
        }

        return super.visit(node, data);
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface() && node.isAbstract()) {
            // an abstract interface
            addViolation(data, node, getMessage());
        }

        if (!node.isNested()) {
            return super.visit(node, data);
        }

        Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
        boolean isParentInterfaceOrAnnotation = parent instanceof ASTAnnotationTypeDeclaration
                || parent instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) parent).isInterface();

        // a public interface within an interface or annotation
        if (node.isInterface() && node.isPublic() && isParentInterfaceOrAnnotation) {
            addViolation(data, node, getMessage());
        }

        if (node.isInterface() && node.isStatic()) {
            // a static interface
            addViolation(data, node, getMessage());
        }

        // a public and/or static class within an interface or annotation
        if (!node.isInterface() && (node.isPublic() || node.isStatic()) && isParentInterfaceOrAnnotation) {
            addViolation(data, node, getMessage());
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

    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        if (node.isPublic() || node.isAbstract()) {
            check(node, data);
        }
        return super.visit(node, data);
    }

    private void check(Node fieldOrMethod, Object data) {
        // third ancestor could be an AllocationExpression
        // if this is a method in an anonymous inner class
        Node parent = fieldOrMethod.jjtGetParent().jjtGetParent().jjtGetParent();
        if (parent instanceof ASTAnnotationTypeDeclaration
                || parent instanceof ASTClassOrInterfaceDeclaration
                && ((ASTClassOrInterfaceDeclaration) parent).isInterface()) {
            addViolation(data, fieldOrMethod);
        }
    }
}
