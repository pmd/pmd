/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.java.ast.JModifier.ABSTRACT;
import static net.sourceforge.pmd.lang.java.ast.JModifier.FINAL;
import static net.sourceforge.pmd.lang.java.ast.JModifier.PRIVATE;
import static net.sourceforge.pmd.lang.java.ast.JModifier.PUBLIC;
import static net.sourceforge.pmd.lang.java.ast.JModifier.STATIC;

import java.util.EnumSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
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
        addRuleChainVisit(ASTConstructorDeclaration.class);
    }


    private void reportUnnecessaryModifiers(Object data, Node node,
                                            JModifier unnecessaryModifier, String explanation) {
        reportUnnecessaryModifiers(data, node, EnumSet.of(unnecessaryModifier), explanation);
    }


    private void reportUnnecessaryModifiers(Object data, Node node,
                                            Set<JModifier> unnecessaryModifiers, String explanation) {
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
            return ((ASTResource) node).getStableName();
        } else {
            return node.getImage();
        }
    }


    // TODO same here
    private String getPrintableNodeKind(Node node) {
        if (node instanceof ASTAnyTypeDeclaration) {
            return PrettyPrintingUtil.kindName((ASTAnyTypeDeclaration) node);
        } else if (node instanceof ASTMethodDeclaration) {
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


    private String formatUnnecessaryModifiers(Set<JModifier> set) {
        // prints in the standard modifier order (sorted by enum constant ordinal),
        // regardless of the actual order in which we checked
        return (set.size() > 1 ? "s" : "") + " '" + StringUtils.join(set, " ") + "'";
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {

        if (node.hasExplicitModifiers(PUBLIC)) {
            checkDeclarationInInterfaceType(data, node, EnumSet.of(PUBLIC));
        }

        if (node.hasExplicitModifiers(STATIC)) {
            // a static enum
            reportUnnecessaryModifiers(data, node, STATIC, "nested enums are implicitly static");
        }

        return data;
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        if (node.isAbstract()) {
            // may have several violations, with different explanations
            reportUnnecessaryModifiers(data, node, ABSTRACT, "annotations types are implicitly abstract");

        }


        if (!node.isNested()) {
            return data;
        }

        // a public annotation within an interface or annotation
        if (node.hasExplicitModifiers(PUBLIC) && isParentInterfaceType(node.getEnclosingType())) {
            reportUnnecessaryModifiers(data, node, PUBLIC,
                                       "members of " + getPrintableNodeKind(node.getEnclosingType())
                                           + " types are implicitly public");
        }

        if (node.hasExplicitModifiers(STATIC)) {
            // a static annotation
            reportUnnecessaryModifiers(data, node, STATIC, "nested annotation types are implicitly static");
        }

        return data;
    }

    // also considers annotations, as should ASTAnyTypeDeclaration do
    private boolean isParentInterfaceType(@Nullable ASTAnyTypeDeclaration enclosing) {
        return enclosing != null && enclosing.isInterface();
    }


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

        if (node.isInterface() && node.hasExplicitModifiers(ABSTRACT)) {
            // an abstract interface
            reportUnnecessaryModifiers(data, node, ABSTRACT, "interface types are implicitly abstract");
        }

        if (!node.isNested()) {
            return data;
        }

        boolean isParentInterfaceOrAnnotation = isParentInterfaceType(node.getEnclosingType());

        // a public class or interface within an interface or annotation
        if (node.hasExplicitModifiers(PUBLIC) && isParentInterfaceOrAnnotation) {
            reportUnnecessaryModifiers(data, node, PUBLIC,
                                       "members of " + getPrintableNodeKind(node.getEnclosingType())
                                           + " types are implicitly public");
        }

        if (node.hasExplicitModifiers(STATIC)) {
            if (node.isInterface()) {
                // a static interface
                reportUnnecessaryModifiers(data, node, STATIC, "member interfaces are implicitly static");
            } else if (isParentInterfaceOrAnnotation) {
                // a type nested within an interface
                reportUnnecessaryModifiers(data, node, STATIC, "types nested within an interface type are implicitly static");
            }
        }

        return data;
    }

    @Override
    public Object visit(final ASTMethodDeclaration node, Object data) {
        Set<JModifier> unnecessary = EnumSet.noneOf(JModifier.class);

        if (node.hasExplicitModifiers(PUBLIC)) {
            unnecessary.add(PUBLIC);
        }
        if (node.hasExplicitModifiers(ABSTRACT)) {
            unnecessary.add(ABSTRACT);
        }

        checkDeclarationInInterfaceType(data, node, unnecessary);

        if (node.hasExplicitModifiers(FINAL)) {
            // If the method is annotated by @SafeVarargs then it's ok
            if (!isSafeVarargs(node)) {
                if (node.hasModifiers(PRIVATE)) {
                    reportUnnecessaryModifiers(data, node, FINAL, "private methods cannot be overridden");
                } else {
                    final ASTAnyTypeDeclaration n = node.getEnclosingType();
                    // A final method of an anonymous class / enum constant. Neither can be extended / overridden
                    if (n.isAnonymous()) {
                        reportUnnecessaryModifiers(data, node, FINAL, "an anonymous class cannot be extended");
                    } else if (n.isFinal()) {
                        // notice: enum types are implicitly final if no enum constant declares a body
                        reportUnnecessaryModifiers(data, node, FINAL, "the method is already in a final class");
                    }
                }
            }
        }

        return data;
    }

    @Override
    public Object visit(final ASTResource node, final Object data) {
        if (!node.isConciseResource() && node.asLocalVariableDeclaration().hasExplicitModifiers(FINAL)) {
            reportUnnecessaryModifiers(data, node, FINAL, "resource specifications are implicitly final");
        }

        return data;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        Set<JModifier> unnecessary = EnumSet.noneOf(JModifier.class);
        if (node.hasExplicitModifiers(PUBLIC)) {
            unnecessary.add(PUBLIC);
        }
        if (node.hasExplicitModifiers(STATIC)) {
            unnecessary.add(STATIC);
        }
        if (node.hasExplicitModifiers(FINAL)) {
            unnecessary.add(FINAL);
        }

        checkDeclarationInInterfaceType(data, node, unnecessary);
        return data;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.getEnclosingType().isEnum()) {
            if (node.hasExplicitModifiers(PRIVATE)) {
                reportUnnecessaryModifiers(data, node, PRIVATE, "enum constructors are implicitly private");
            }
        }
        return data;
    }


    private boolean isSafeVarargs(final ASTMethodDeclaration node) {
        return node.isAnnotationPresent(SafeVarargs.class.getName());
    }


    private void checkDeclarationInInterfaceType(Object data, JavaNode fieldOrMethod, Set<JModifier> unnecessary) {
        // third ancestor could be an AllocationExpression
        // if this is a method in an anonymous inner class
        ASTAnyTypeDeclaration parent = fieldOrMethod.getEnclosingType();
        if (parent.isInterface()) {
            reportUnnecessaryModifiers(data, fieldOrMethod, unnecessary,
                                       "the " + getPrintableNodeKind(fieldOrMethod) + " is declared in an "
                                           + getPrintableNodeKind(parent) + " type");
        }
    }


}
