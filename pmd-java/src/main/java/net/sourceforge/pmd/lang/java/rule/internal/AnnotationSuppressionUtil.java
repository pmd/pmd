/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Helper methods to suppress violations based on annotations.
 *
 * An annotation suppresses a rule if the annotation is a {@link SuppressWarnings},
 * and if the set of suppressed warnings ({@link SuppressWarnings#value()})
 * contains at least one of those:
 * <ul>
 * <li>"PMD" (suppresses all rules);
 * <li>"PMD.rulename", where rulename is the name of the given rule;
 * <li>"all" (conventional value to suppress all warnings).
 * </ul>
 *
 * <p>Additionally, the following values suppress a specific set of rules:
 * <ul>
 * <li>{@code "unused"}: suppresses rules like UnusedLocalVariable or UnusedPrivateField;
 * <li>{@code "serial"}: suppresses BeanMembersShouldSerialize and MissingSerialVersionUID;
 * <li>TODO "fallthrough" #1899
 * </ul>
 */
final class AnnotationSuppressionUtil {

    private static final Set<String> UNUSED_RULES
        = new HashSet<>(Arrays.asList("UnusedPrivateField", "UnusedLocalVariable", "UnusedPrivateMethod",
                "UnusedFormalParameter", "UnusedAssignment"));
    private static final Set<String> SERIAL_RULES =
        new HashSet<>(Arrays.asList("BeanMembersShouldSerialize", "MissingSerialVersionUID"));

    private AnnotationSuppressionUtil() {

    }

    static boolean contextSuppresses(Node node, Rule rule) {
        boolean result = suppresses(node, rule);

        if (!result && node instanceof ASTCompilationUnit) {
            for (int i = 0; !result && i < node.getNumChildren(); i++) {
                result = AnnotationSuppressionUtil.suppresses(node.getChild(i), rule);
            }
        }
        if (!result) {
            Node parent = node.getParent();
            while (!result && parent != null) {
                result = AnnotationSuppressionUtil.suppresses(parent, rule);
                parent = parent.getParent();
            }
        }
        return result;
    }


    /**
     * Returns true if the node has an annotation that suppresses the
     * given rule.
     */
    private static boolean suppresses(final Node node, Rule rule) {
        Annotatable suppressor = getSuppressor(node);
        if (suppressor == null) {
            return false;
        }

        return hasSuppressWarningsAnnotationFor(suppressor, rule);
    }

    @Nullable
    private static Annotatable getSuppressor(Node node) {
        if (node instanceof ASTAnyTypeDeclaration
            || node instanceof ASTMethodOrConstructorDeclaration
            // also works for ASTResource when Resource uses LocalVariableDeclaration
            || node instanceof ASTLocalVariableDeclaration
            || node instanceof ASTFieldDeclaration
            || node instanceof ASTFormalParameter) {
            return (Annotatable) node;
        } else {
            return null;
        }
    }

    private static boolean hasSuppressWarningsAnnotationFor(final Annotatable node, Rule rule) {
        return node.getDeclaredAnnotations().any(it -> annotationSuppresses(it, rule));
    }


    // @formatter:on
    private static boolean annotationSuppresses(ASTAnnotation annotation, Rule rule) {
        if (TypeTestUtil.isA(SuppressWarnings.class, annotation)) {
            for (ASTStringLiteral element : annotation.findDescendantsOfType(ASTStringLiteral.class)) {
                if (element.hasImageEqualTo("\"PMD\"") || element.hasImageEqualTo(
                    "\"PMD." + rule.getName() + "\"")
                    // Check for standard annotations values
                    || element.hasImageEqualTo("\"all\"")
                    || element.hasImageEqualTo("\"serial\"") && SERIAL_RULES.contains(rule.getName())
                    || element.hasImageEqualTo("\"unused\"") && UNUSED_RULES.contains(rule.getName())
                    || element.hasImageEqualTo("\"all\"")) {
                    return true;
                }
            }
        }

        return false;
    }
}
