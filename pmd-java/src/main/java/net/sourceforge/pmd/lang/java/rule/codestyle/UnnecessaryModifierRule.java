/*
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
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTRecordDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;


public class UnnecessaryModifierRule extends AbstractJavaRulechainRule {


    public UnnecessaryModifierRule() {
        super(ASTTypeDeclaration.class,
              ASTMethodDeclaration.class,
              ASTResource.class,
              ASTFieldDeclaration.class,
              ASTConstructorDeclaration.class);
    }


    private void reportUnnecessaryModifiers(RuleContext ctx, JavaNode node,
                                            JModifier unnecessaryModifier, String explanation) {
        reportUnnecessaryModifiers(ctx, node, EnumSet.of(unnecessaryModifier), explanation);
    }


    private void reportUnnecessaryModifiers(RuleContext ctx, JavaNode node,
                                            Set<JModifier> unnecessaryModifiers, String explanation) {
        if (unnecessaryModifiers.isEmpty()) {
            return;
        }
        ctx.addViolation(node, formatUnnecessaryModifiers(unnecessaryModifiers),
                         PrettyPrintingUtil.getPrintableNodeKind(node),
                         PrettyPrintingUtil.getNodeName(node),
                         explanation.isEmpty() ? "" : ": " + explanation);
    }


    private String formatUnnecessaryModifiers(Set<JModifier> set) {
        // prints in the standard modifier order (sorted by enum constant ordinal),
        // regardless of the actual order in which we checked
        return (set.size() > 1 ? "s" : "") + " '" + StringUtils.join(set, " ") + "'";
    }


    @Override
    public Object visit(ASTEnumDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        
        if (node.hasExplicitModifiers(PUBLIC)) {
            checkDeclarationInInterfaceType(ctx, node, EnumSet.of(PUBLIC));
        }

        if (node.hasExplicitModifiers(STATIC)) {
            // a static enum
            reportUnnecessaryModifiers(ctx, node, STATIC, "nested enums are implicitly static");
        }

        return null;
    }


    @Override
    public Object visit(ASTAnnotationTypeDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (node.hasExplicitModifiers(ABSTRACT)) {
            // may have several violations, with different explanations
            reportUnnecessaryModifiers(ctx, node, ABSTRACT, "annotations types are implicitly abstract");

        }

        if (!node.isNested()) {
            return null;
        }

        checkDeclarationInInterfaceType(ctx, node, EnumSet.of(PUBLIC));

        if (node.hasExplicitModifiers(STATIC)) {
            // a static annotation
            reportUnnecessaryModifiers(ctx, node, STATIC, "nested annotation types are implicitly static");
        }

        return null;
    }

    // also considers annotations, as should ASTTypeDeclaration do
    private boolean isParentInterfaceType(ModifierOwner node) {
        ASTTypeDeclaration enclosing = node.getEnclosingType();
        return enclosing != null && enclosing.isInterface();
    }


    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        
        if (node.isInterface() && node.hasExplicitModifiers(ABSTRACT)) {
            // an abstract interface
            reportUnnecessaryModifiers(ctx, node, ABSTRACT, "interface types are implicitly abstract");
        }

        if (!node.isNested()) {
            return null;
        }

        checkDeclarationInInterfaceType(ctx, node, EnumSet.of(PUBLIC, STATIC));

        if (node.hasExplicitModifiers(STATIC) && node.isInterface() && !isParentInterfaceType(node)) {
            // a static interface
            reportUnnecessaryModifiers(ctx, node, STATIC, "member interfaces are implicitly static");
        }

        return null;
    }

    @Override
    public Object visit(final ASTMethodDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        
        checkDeclarationInInterfaceType(ctx, node, EnumSet.of(PUBLIC, ABSTRACT));

        if (node.hasExplicitModifiers(FINAL)) {
            // If the method is annotated by @SafeVarargs then it's ok
            if (!isSafeVarargs(node)) {
                if (node.hasModifiers(PRIVATE)) {
                    reportUnnecessaryModifiers(ctx, node, FINAL, "private methods cannot be overridden");
                } else {
                    final ASTTypeDeclaration n = node.getEnclosingType();
                    // A final method of an anonymous class / enum constant. Neither can be extended / overridden
                    if (n.isAnonymous()) {
                        reportUnnecessaryModifiers(ctx, node, FINAL, "an anonymous class cannot be extended");
                    } else if (n.isFinal()) {
                        // notice: enum types are implicitly final if no enum constant declares a body
                        reportUnnecessaryModifiers(ctx, node, FINAL, "the method is already in a final class");
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Object visit(final ASTResource node, final Object data) {
        RuleContext ctx = (RuleContext) data;
        if (!node.isConciseResource() && node.asLocalVariableDeclaration().hasExplicitModifiers(FINAL)) {
            reportUnnecessaryModifiers(ctx, node, FINAL, "resource specifications are implicitly final");
        }
        return null;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        checkDeclarationInInterfaceType(ctx, node, EnumSet.of(PUBLIC, STATIC, FINAL));
        return null;
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (node.getEnclosingType().isEnum() && node.hasExplicitModifiers(PRIVATE)) {
            reportUnnecessaryModifiers(ctx, node, PRIVATE, "enum constructors are implicitly private");
        }
        return null;
    }

    @Override
    public Object visit(ASTRecordDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (node.hasExplicitModifiers(STATIC)) {
            reportUnnecessaryModifiers(ctx, node, STATIC, "records are implicitly static");
        }
        if (node.hasExplicitModifiers(FINAL)) {
            reportUnnecessaryModifiers(ctx, node, FINAL, "records are implicitly final");
        }
        return null;
    }


    private boolean isSafeVarargs(final ASTMethodDeclaration node) {
        return node.isAnnotationPresent(SafeVarargs.class.getName());
    }


    private void checkDeclarationInInterfaceType(RuleContext ctx, ModifierOwner member, Set<JModifier> unnecessary) {
        // third ancestor could be an AllocationExpression
        // if this is a method in an anonymous inner class
        ASTTypeDeclaration parent = member.getEnclosingType();
        if (isParentInterfaceType(member)) {
            unnecessary.removeIf(mod -> !member.hasExplicitModifiers(mod));

            String explanation = "the " + PrettyPrintingUtil.getPrintableNodeKind(member)
                + " is declared in an " + PrettyPrintingUtil.getPrintableNodeKind(parent) + " type";
            reportUnnecessaryModifiers(ctx, member, unnecessary, explanation);
        }
    }

}
