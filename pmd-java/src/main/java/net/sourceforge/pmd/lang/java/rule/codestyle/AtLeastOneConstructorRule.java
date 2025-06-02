/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Arrays;
import java.util.Collection;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.rule.design.UseUtilityClassRule;
import net.sourceforge.pmd.lang.java.rule.internal.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * This rule detects non-static classes with no constructors;
 * requiring even the default constructor to be explicit.
 * It ignores classes with solely static methods,
 * use {@link UseUtilityClassRule} to flag those.
 */
public class AtLeastOneConstructorRule extends AbstractIgnoredAnnotationRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTClassDeclaration.class);
    }

    @Override
    protected Collection<String> defaultSuppressionAnnotations() {
        return Arrays.asList("lombok.Data",
                "lombok.Value",
                "lombok.Builder",
                "lombok.NoArgsConstructor",
                "lombok.RequiredArgsConstructor",
                "lombok.AllArgsConstructor");
    }

    @Override
    public Object visit(final ASTClassDeclaration node, final Object data) {
        // Ignore interfaces / static classes / classes that have a constructor / classes ignored through annotations
        if (!node.isRegularClass()
            || node.isStatic()
            || node.getDeclarations().any(it -> it instanceof ASTConstructorDeclaration)
            || hasIgnoredAnnotation(node)) {
            return data;
        }

        NodeStream<ModifierOwner> members = node.getDeclarations()
                                             .filterIs(ModifierOwner.class)
                                             .filterNot(it -> it instanceof ASTTypeDeclaration);
        if (members.isEmpty() || members.any(it -> !it.hasModifiers(JModifier.STATIC))) {
            // Do we have any non-static members?
            asCtx(data).addViolation(node);
        }

        return data;
    }
}
