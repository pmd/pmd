/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMarkerAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnnecessaryModifierRule extends AbstractJavaRule {

    public UnnecessaryModifierRule() {
        addRuleChainVisit(ASTEnumDeclaration.class);
        addRuleChainVisit(ASTAnnotationTypeDeclaration.class);
        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
        addRuleChainVisit(ASTMethodDeclaration.class);
        addRuleChainVisit(ASTResource.class);
        addRuleChainVisit(ASTFieldDeclaration.class);
        addRuleChainVisit(ASTAnnotationMethodDeclaration.class);
        addRuleChainVisit(ASTConstructorDeclaration.class);
    }
    
    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        if (node.isStatic()) {
            // a static enum
            addViolation(data, node, getMessage());
        }

        return data;
    }

    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        if (node.isAbstract()) {
            // an abstract annotation
            addViolation(data, node, getMessage());
        }

        if (!node.isNested()) {
            return data;
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

        return data;
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface() && node.isAbstract()) {
            // an abstract interface
            addViolation(data, node, getMessage());
        }

        if (!node.isNested()) {
            return data;
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

        return data;
    }
    
    public Object visit(final ASTMethodDeclaration node, Object data) {
        if (node.isSyntacticallyPublic() || node.isSyntacticallyAbstract()) {
            check(node, data);
        }
        
        if (node.isFinal()) {
            // If the method is annotated by @SafeVarargs then it's ok
            if (!isSafeVarargs(node)) {
                if (node.isPrivate()) {
                    addViolation(data, node);
                } else {
                    final Node n = node.getNthParent(3);
                    // A final method of an anonymous class / enum constant. Neither can be extended / overridden
                    if (n instanceof ASTAllocationExpression || n instanceof ASTEnumConstant) {
                        addViolation(data, node);
                    } else if (n instanceof ASTClassOrInterfaceDeclaration
                            && ((ASTClassOrInterfaceDeclaration) n).isFinal()) {
                        addViolation(data, node);
                    }
                }
            }
        }
        
        return data;
    }
    
    public Object visit(final ASTResource node, final Object data) {
        if (node.isFinal()) {
            addViolation(data, node);
        }
        
        return data;
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        if (node.isSyntacticallyPublic() || node.isSyntacticallyStatic() || node.isSyntacticallyFinal()) {
            check(node, data);
        }
        return data;
    }

    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        if (node.isPublic() || node.isAbstract()) {
            check(node, data);
        }
        return data;
    }
    
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.getNthParent(2) instanceof ASTEnumBody) {
            if (node.isPrivate()) {
                addViolation(data, node);
            }
        }
        return data;
    }
    
    private boolean isSafeVarargs(final ASTMethodDeclaration node) {
        for (final ASTAnnotation annotation : node.jjtGetParent().findChildrenOfType(ASTAnnotation.class)) {
            final Node childAnnotation = annotation.jjtGetChild(0);
            if (childAnnotation instanceof ASTMarkerAnnotation) {
                final ASTMarkerAnnotation marker = (ASTMarkerAnnotation) childAnnotation;
                if (marker.getType() != null && SafeVarargs.class.isAssignableFrom(marker.getType())) {
                    return true;
                }
            }
        }
        
        return false;
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
