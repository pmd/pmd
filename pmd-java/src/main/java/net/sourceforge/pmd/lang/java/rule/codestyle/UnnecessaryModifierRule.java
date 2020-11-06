/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration.TypeKind;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
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


    private void reportUnnecessaryModifiers(Object data, Node node,
                                            Modifier unnecessaryModifier, String explanation) {
        reportUnnecessaryModifiers(data, node, EnumSet.of(unnecessaryModifier), explanation);
    }


    private void reportUnnecessaryModifiers(Object data, Node node,
                                            Set<Modifier> unnecessaryModifiers, String explanation) {
        if (unnecessaryModifiers.isEmpty()) {
            return;
        }
        super.addViolation(data, node, new String[]{
                formatUnnecessaryModifiers(unnecessaryModifiers),
                getPrintableNodeKind(node),
                getNodeName(node),
                explanation.isEmpty() ? "" : ": " + explanation,
        });
    }


    // TODO this should probably make it into a PrettyPrintingUtil or something.
    private String getNodeName(Node node) {
        // constructors are differentiated by their parameters, while we only use method name for methods
        if (node instanceof ASTMethodDeclaration) {
            return ((ASTMethodDeclaration) node).getName();
        } else if (node instanceof ASTMethodOrConstructorDeclaration) {
            // constructors are differentiated by their parameters, while we only use method name for methods
            return PrettyPrintingUtil.displaySignature((ASTConstructorDeclaration) node);
        } else if (node instanceof ASTFieldDeclaration) {
            return ((ASTFieldDeclaration) node).getVariableName();
        } else if (node instanceof ASTResource) {
            return ((ASTResource) node).getVariableDeclaratorId().getImage();
        } else {
            return node.getImage();
        }
    }


    // TODO same here
    private String getPrintableNodeKind(Node node) {
        if (node instanceof ASTAnyTypeDeclaration) {
            return PrettyPrintingUtil.kindName((ASTAnyTypeDeclaration) node);
        } else if (node instanceof ASTMethodDeclaration || node instanceof ASTAnnotationMethodDeclaration) {
            return "method";
        } else if (node instanceof ASTConstructorDeclaration) {
            return "constructor";
        } else if (node instanceof ASTFieldDeclaration) {
            return "field";
        } else if (node instanceof ASTResource) {
            return "resource specification";
        }
        throw new UnsupportedOperationException("Node " + node + " is unaccounted for");
    }


    private String formatUnnecessaryModifiers(Set<Modifier> set) {
        // prints in the standard modifier order (sorted by enum constant ordinal),
        // regardless of the actual order in which we checked
        return (set.size() > 1 ? "s" : "") + " '" + StringUtils.join(set, " ") + "'";
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {

        if (node.isPublic()) {
            checkDeclarationInInterfaceType(data, node, EnumSet.of(Modifier.PUBLIC));
        }

        if (node.isStatic()) {
            // a static enum
            reportUnnecessaryModifiers(data, node, Modifier.STATIC, "nested enums are implicitly static");
        }

        return data;
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        if (node.isAbstract()) {
            // may have several violations, with different explanations
            reportUnnecessaryModifiers(data, node, Modifier.ABSTRACT, "annotations types are implicitly abstract");

        }


        if (!node.isNested()) {
            return data;
        }

        // a public annotation within an interface or annotation
        if (node.isPublic() && node.enclosingTypeIsA(TypeKind.INTERFACE, TypeKind.ANNOTATION)) {
            reportUnnecessaryModifiers(data, node, Modifier.PUBLIC, "members of " + getPrintableNodeKind(node.getEnclosingTypeDeclaration()) + " types are implicitly public");
        }

        if (node.isStatic()) {
            // a static annotation
            reportUnnecessaryModifiers(data, node, Modifier.STATIC, "nested annotation types are implicitly static");
        }

        return data;
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

        if (node.isInterface() && node.isAbstract()) {
            // an abstract interface
            reportUnnecessaryModifiers(data, node, Modifier.ABSTRACT, "interface types are implicitly abstract");
        }

        if (!node.isNested()) {
            return data;
        }

        boolean isParentInterfaceOrAnnotation = node.enclosingTypeIsA(TypeKind.INTERFACE, TypeKind.ANNOTATION);

        // a public class or interface within an interface or annotation
        if (node.isPublic() && isParentInterfaceOrAnnotation) {
            reportUnnecessaryModifiers(data, node, Modifier.PUBLIC, "members of " + getPrintableNodeKind(node.getEnclosingTypeDeclaration()) + " types are implicitly public");
        }

        if (node.isStatic()) {
            if (node.isInterface()) {
                // a static interface
                reportUnnecessaryModifiers(data, node, Modifier.STATIC, "member interfaces are implicitly static");
            } else if (isParentInterfaceOrAnnotation) {
                // a type nested within an interface
                reportUnnecessaryModifiers(data, node, Modifier.STATIC, "types nested within an interface type are implicitly static");
            }
        }

        return data;
    }

    @Override
    public Object visit(final ASTMethodDeclaration node, Object data) {
        Set<Modifier> unnecessary = EnumSet.noneOf(Modifier.class);

        if (node.isSyntacticallyPublic()) {
            unnecessary.add(Modifier.PUBLIC);
        }
        if (node.isSyntacticallyAbstract()) {
            unnecessary.add(Modifier.ABSTRACT);
        }

        checkDeclarationInInterfaceType(data, node, unnecessary);

        if (node.isFinal()) {
            // If the method is annotated by @SafeVarargs then it's ok
            if (!isSafeVarargs(node)) {
                if (node.isPrivate()) {
                    reportUnnecessaryModifiers(data, node, Modifier.FINAL, "private methods cannot be overridden");
                } else {
                    final Node n = node.getNthParent(3);
                    // A final method of an anonymous class / enum constant. Neither can be extended / overridden
                    if (n instanceof ASTAllocationExpression || n instanceof ASTEnumConstant) {
                        reportUnnecessaryModifiers(data, node, Modifier.FINAL, "an anonymous class cannot be extended");
                    } else if (n instanceof ASTClassOrInterfaceDeclaration && ((AccessNode) n).isFinal()) {
                        reportUnnecessaryModifiers(data, node, Modifier.FINAL, "the method is already in a final class");
                    }
                }
            }
        }

        return data;
    }

    @Override
    public Object visit(final ASTResource node, final Object data) {
        if (node.isFinal()) {
            reportUnnecessaryModifiers(data, node, Modifier.FINAL, "resource specifications are implicitly final");
        }

        return data;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        Set<Modifier> unnecessary = EnumSet.noneOf(Modifier.class);
        if (node.isSyntacticallyPublic()) {
            unnecessary.add(Modifier.PUBLIC);
        }
        if (node.isSyntacticallyStatic()) {
            unnecessary.add(Modifier.STATIC);
        }
        if (node.isSyntacticallyFinal()) {
            unnecessary.add(Modifier.FINAL);
        }

        checkDeclarationInInterfaceType(data, node, unnecessary);
        return data;
    }

    @Override
    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        Set<Modifier> unnecessary = EnumSet.noneOf(Modifier.class);
        if (node.isPublic()) {
            unnecessary.add(Modifier.PUBLIC);
        }
        if (node.isAbstract()) {
            unnecessary.add(Modifier.ABSTRACT);
        }
        checkDeclarationInInterfaceType(data, node, unnecessary);
        return data;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.getNthParent(2) instanceof ASTEnumBody) {
            if (node.isPrivate()) {
                reportUnnecessaryModifiers(data, node, Modifier.PRIVATE, "enum constructors are implicitly private");
            }
        }
        return data;
    }


    private boolean isSafeVarargs(final ASTMethodDeclaration node) {
        return node.isAnnotationPresent(SafeVarargs.class.getName());
    }


    private void checkDeclarationInInterfaceType(Object data, Node fieldOrMethod, Set<Modifier> unnecessary) {
        // third ancestor could be an AllocationExpression
        // if this is a method in an anonymous inner class
        Node parent = fieldOrMethod.getParent().getParent().getParent();
        if (parent instanceof ASTAnnotationTypeDeclaration
                || parent instanceof ASTClassOrInterfaceDeclaration
                && ((ASTClassOrInterfaceDeclaration) parent).isInterface()) {
            reportUnnecessaryModifiers(data, fieldOrMethod, unnecessary, "the " + getPrintableNodeKind(fieldOrMethod) + " is declared in an " + getPrintableNodeKind(parent) + " type");
        }
    }


    private enum Modifier {
        PUBLIC, PRIVATE, PROTECTED, STATIC, FINAL, ABSTRACT;


        @Override
        public String toString() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

}
