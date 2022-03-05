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

import net.sourceforge.pmd.lang.java.ast.ASTAnnotationTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;


public class UnnecessaryModifierRule extends AbstractJavaRulechainRule {


    public UnnecessaryModifierRule() {
        super(ASTAnyTypeDeclaration.class,
              ASTMethodDeclaration.class,
              ASTResource.class,
              ASTFieldDeclaration.class,
              ASTConstructorDeclaration.class);
        addRuleChainVisit(ASTRecordDeclaration.class);
    }


    private void reportUnnecessaryModifiers(Object data, JavaNode node,
                                            JModifier unnecessaryModifier, String explanation) {
        reportUnnecessaryModifiers(data, node, EnumSet.of(unnecessaryModifier), explanation);
    }


    private void reportUnnecessaryModifiers(Object data, JavaNode node,
                                            Set<JModifier> unnecessaryModifiers, String explanation) {
        if (unnecessaryModifiers.isEmpty()) {
            return;
        }
        super.addViolation(data, node, new String[]{
                formatUnnecessaryModifiers(unnecessaryModifiers),
                PrettyPrintingUtil.getPrintableNodeKind(node),
                PrettyPrintingUtil.getNodeName(node),
                explanation.isEmpty() ? "" : ": " + explanation,
        });
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
        if (node.hasExplicitModifiers(ABSTRACT)) {
            // may have several violations, with different explanations
            reportUnnecessaryModifiers(data, node, ABSTRACT, "annotations types are implicitly abstract");

        }


        if (!node.isNested()) {
            return data;
        }

        checkDeclarationInInterfaceType(data, node, EnumSet.of(PUBLIC));

        if (node.hasExplicitModifiers(STATIC)) {
            // a static annotation
            reportUnnecessaryModifiers(data, node, STATIC, "nested annotation types are implicitly static");
        }

        return data;
    }

    // also considers annotations, as should ASTAnyTypeDeclaration do
    private boolean isParentInterfaceType(AccessNode node) {
        ASTAnyTypeDeclaration enclosing = node.getEnclosingType();
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

        checkDeclarationInInterfaceType(data, node, EnumSet.of(PUBLIC, STATIC));

        if (node.hasExplicitModifiers(STATIC) && node.isInterface() && !isParentInterfaceType(node)) {
            // a static interface
            reportUnnecessaryModifiers(data, node, STATIC, "member interfaces are implicitly static");
        }

        return data;
    }

    @Override
    public Object visit(final ASTMethodDeclaration node, Object data) {

        checkDeclarationInInterfaceType(data, node, EnumSet.of(PUBLIC, ABSTRACT));

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
        checkDeclarationInInterfaceType(data, node, EnumSet.of(PUBLIC, STATIC, FINAL));
        return data;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        if (node.getEnclosingType().isEnum() && node.hasExplicitModifiers(PRIVATE)) {
            reportUnnecessaryModifiers(data, node, PRIVATE, "enum constructors are implicitly private");
        }
        return data;
    }

    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        if (node.hasExplicitModifiers(STATIC)) {
            reportUnnecessaryModifiers(data, node, STATIC, "records are implicitly static");
        }
        if (node.hasExplicitModifiers(FINAL)) {
            reportUnnecessaryModifiers(data, node, FINAL, "records are implicitly final");
        }
        return data;
    }


    private boolean isSafeVarargs(final ASTMethodDeclaration node) {
        return node.isAnnotationPresent(SafeVarargs.class.getName());
    }


    private void checkDeclarationInInterfaceType(Object data, AccessNode member, Set<JModifier> unnecessary) {
        // third ancestor could be an AllocationExpression
        // if this is a method in an anonymous inner class
        ASTAnyTypeDeclaration parent = member.getEnclosingType();
        if (isParentInterfaceType(member)) {
            unnecessary.removeIf(mod -> !member.hasExplicitModifiers(mod));

            String explanation = "the " + PrettyPrintingUtil.getPrintableNodeKind(member)
                + " is declared in an " + PrettyPrintingUtil.getPrintableNodeKind(parent) + " type";
            reportUnnecessaryModifiers(data, member, unnecessary, explanation);
        }
    }

}
