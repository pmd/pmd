/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
import net.sourceforge.pmd.lang.java.ast.MethodLikeNode;
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
        reportUnnecessaryModifiers(data, node, Collections.singletonList(unnecessaryModifier), explanation);
    }


    private void reportUnnecessaryModifiers(Object data, Node node,
                                            List<Modifier> unnecessaryModifiers, String explanation) {
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
        return node instanceof ASTMethodDeclaration
               ? ((ASTMethodDeclaration) node).getMethodName()
               : node instanceof ASTMethodOrConstructorDeclaration
                 // constructors are differentiated by their parameters, while we only use method name for methods
                 ? ((ASTConstructorDeclaration) node).getQualifiedName().getOperation()
                 : node instanceof ASTFieldDeclaration
                   ? ((ASTFieldDeclaration) node).getVariableName()
                   : node instanceof ASTResource
                     ? ((ASTResource) node).getVariableDeclaratorId().getImage()
                     : node.getImage();
    }


    // TODO same here
    private String getPrintableNodeKind(Node node) {
        if (node instanceof ASTAnyTypeDeclaration) {
            return ((ASTAnyTypeDeclaration) node).getTypeKind().getPrintableName();
        } else if (node instanceof MethodLikeNode) {
            return ((MethodLikeNode) node).getKind().getPrintableName();
        } else if (node instanceof ASTFieldDeclaration) {
            return "field";
        } else if (node instanceof ASTResource) {
            return "resource specification";
        }
        throw new UnsupportedOperationException("Node " + node + " is unaccounted for");
    }


    private String formatUnnecessaryModifiers(List<Modifier> lst) {
        // prints in the standard modifier order, regardless of the actual order in which we checked
        Collections.sort(lst, new Comparator<Modifier>() {
            @Override
            public int compare(Modifier o1, Modifier o2) {
                return Integer.compare(o1.ordinal(), o2.ordinal());
            }
        });
        return (lst.size() > 1 ? "s" : "") + " '" + StringUtils.join(lst, " ") + "'";
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {

        if (node.isPublic()) {
            checkDeclarationInInterfaceType(data, node, Collections.singletonList(Modifier.PUBLIC));
        }

        if (node.isStatic()) {
            // a static enum
            reportUnnecessaryModifiers(data, node, Modifier.STATIC, "nested enums are implicitly static");
        }

        return data;
    }


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

        if ((node.isInterface() || isParentInterfaceOrAnnotation) && node.isStatic()) {
            // a static interface or class nested within an interface
            reportUnnecessaryModifiers(data, node, Modifier.PUBLIC, "types nested within an interface type are implicitly static");
        }

        return data;
    }
    
    public Object visit(final ASTMethodDeclaration node, Object data) {
        List<Modifier> unnecessary = new ArrayList<>();

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
    
    public Object visit(final ASTResource node, final Object data) {
        if (node.isFinal()) {
            reportUnnecessaryModifiers(data, node, Modifier.FINAL, "resources specifications are implicitly final");
        }
        
        return data;
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        List<Modifier> unnecessary = new ArrayList<>();
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

    public Object visit(ASTAnnotationMethodDeclaration node, Object data) {
        List<Modifier> unnecessary = new ArrayList<>();
        if (node.isPublic()) {
            unnecessary.add(Modifier.PUBLIC);
        }
        if (node.isAbstract()) {
            unnecessary.add(Modifier.ABSTRACT);
        }
        checkDeclarationInInterfaceType(data, node, unnecessary);
        return data;
    }
    
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


    private void checkDeclarationInInterfaceType(Object data, Node fieldOrMethod, List<Modifier> unnecessary) {
        // third ancestor could be an AllocationExpression
        // if this is a method in an anonymous inner class
        Node parent = fieldOrMethod.jjtGetParent().jjtGetParent().jjtGetParent();
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
