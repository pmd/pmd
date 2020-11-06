/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Arrays;
import java.util.Collection;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.java.rule.design.UseUtilityClassRule;

/**
 * This rule detects non-static classes with no constructors;
 * requiring even the default constructor to be explicit.
 * It ignores classes with solely static methods,
 * use {@link UseUtilityClassRule} to flag those.
 */
public class AtLeastOneConstructorRule extends AbstractIgnoredAnnotationRule {

    public AtLeastOneConstructorRule() {
        addRuleChainVisit(ASTClassOrInterfaceDeclaration.class);
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
    public Object visit(final ASTClassOrInterfaceDeclaration node, final Object data) {
        // Ignore interfaces / static classes / classes that have a constructor / classes ignored through annotations
        if (node.isInterface() || node.isStatic() || node.hasDescendantOfType(ASTConstructorDeclaration.class)
                || hasIgnoredAnnotation(node)) {
            return data;
        }

        boolean atLeastOneMember = false;

        // Do we have any non-static methods?
        for (final ASTMethodDeclaration m : node.findDescendantsOfType(ASTMethodDeclaration.class)) {
            if (!m.isStatic()) {
                addViolation(data, node);
                return data;
            }
            atLeastOneMember = true;
        }

        // .. or fields?
        for (final ASTFieldDeclaration f : node.findDescendantsOfType(ASTFieldDeclaration.class)) {
            if (!f.isStatic()) {
                addViolation(data, node);
                return data;
            }
            atLeastOneMember = true;
        }

        // Class has no declared members
        if (!atLeastOneMember) {
            addViolation(data, node);
        }

        return data;
    }
}
