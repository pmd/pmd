/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ViolationSuppressor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.rule.errorprone.ImplicitSwitchFallThroughRule;
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
 * <li>{@code "serial"}: suppresses BeanMembersShouldSerialize, NonSerializableClass and MissingSerialVersionUID;
 * <li>TODO "fallthrough" #1899
 * </ul>
 */
final class AnnotationSuppressionUtil {

    private static final Set<String> UNUSED_RULES
        = new HashSet<>(Arrays.asList("UnusedPrivateField", "UnusedLocalVariable", "UnusedPrivateMethod",
                                      "UnusedFormalParameter", "UnusedAssignment", "SingularField"));
    private static final Set<String> SERIAL_RULES =
        new HashSet<>(Arrays.asList("BeanMembersShouldSerialize", "NonSerializableClass", "MissingSerialVersionUID"));

    static final ViolationSuppressor JAVA_ANNOT_SUPPRESSOR = new ViolationSuppressor() {
        @Override
        public String getId() {
            return "@SuppressWarnings";
        }

        @Override
        public Report.SuppressedViolation suppressOrNull(RuleViolation rv, @NonNull Node node) {
            if (contextSuppresses(node, rv.getRule())) {
                return new SuppressedViolation(rv, this, null);
            }
            return null;
        }
    };

    static final List<ViolationSuppressor> ALL_JAVA_SUPPRESSORS = listOf(JAVA_ANNOT_SUPPRESSOR);

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
        return suppressor != null && hasSuppressWarningsAnnotationFor(suppressor, rule);
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
            for (ASTMemberValue value : annotation.getFlatValue(ASTMemberValuePair.VALUE_ATTR)) {
                Object constVal = value.getConstValue();
                if (constVal instanceof String) {
                    String stringVal = (String) constVal;
                    if ("PMD".equals(stringVal)
                        || ("PMD." + rule.getName()).equals(stringVal) // NOPMD uselessparentheses false positive
                        // Check for standard annotations values
                        || "all".equals(stringVal)
                        || "serial".equals(stringVal) && SERIAL_RULES.contains(rule.getName())
                        || "unused".equals(stringVal) && UNUSED_RULES.contains(rule.getName())
                        || "fallthrough".equals(stringVal) && rule instanceof ImplicitSwitchFallThroughRule
                    ) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
