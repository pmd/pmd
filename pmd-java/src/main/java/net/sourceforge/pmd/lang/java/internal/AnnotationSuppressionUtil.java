/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.rule.errorprone.ImplicitSwitchFallThroughRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;
import net.sourceforge.pmd.reporting.Reportable;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
import net.sourceforge.pmd.reporting.ViolationSuppressor.UnusedSuppressorNode;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

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
 * <li>{@code "fallthrough"}: suppresses ImplicitSwitchFallthrough #1899
 * </ul>
 */
final class AnnotationSuppressionUtil {

    private static final Set<String> UNUSED_RULES
        = new HashSet<>(Arrays.asList("UnusedPrivateField", "UnusedLocalVariable", "UnusedPrivateMethod",
                                      "UnusedFormalParameter", "UnusedAssignment", "SingularField"));
    private static final Set<String> SERIAL_RULES =
        new HashSet<>(Arrays.asList("BeanMembersShouldSerialize", "NonSerializableClass", "MissingSerialVersionUID"));

    /** Key to store the set of rule violations that were effectively suppressed by an annotation. */
    private static final SimpleDataKey<Boolean> KEY_SUPPRESSED_RULES =
        DataMap.simpleDataKey("pmd.java.suppressed.rules");

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

        @Override
        public Set<UnusedSuppressorNode> getUnusedSuppressors(RootNode tree) {
            return tree.descendants(ASTAnnotation.class)
                       .crossFindBoundaries()
                       .toStream()
                       .map(AnnotationSuppressionUtil::getUnusedSuppressorNodes)
                       .flatMap(Set::stream)
                       .collect(Collectors.toSet());
        }
    };

    static final List<ViolationSuppressor> ALL_JAVA_SUPPRESSORS = listOf(JAVA_ANNOT_SUPPRESSOR);

    private AnnotationSuppressionUtil() {

    }

    static boolean contextSuppresses(Node node, Rule rule) {
        if (suppresses(node, rule)) {
            return true;
        }

        if (node instanceof ASTCompilationUnit) {
            for (int i = 0; i < node.getNumChildren(); i++) {
                if (suppresses(node.getChild(i), rule)) {
                    return true;
                }
            }
        }

        Node parent = node.getParent();
        while (parent != null) {
            if (suppresses(parent, rule)) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }


    /**
     * Returns true if the node has an annotation that suppresses the
     * given rule.
     */
    private static boolean suppresses(final Node node, Rule rule) {
        Annotatable suppressor = getSuppressor(node);
        return suppressor != null && hasSuppressWarningsAnnotationFor(suppressor, rule);
    }

    private static @Nullable Annotatable getSuppressor(Node node) {
        if (node instanceof Annotatable) {
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
                        value.getUserMap().compute(KEY_SUPPRESSED_RULES, a -> Boolean.TRUE);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Return the set of rule names for which the given annotation has suppressed at least one violation.
     *
     * @param annotation An annotation
     */
    private static Set<UnusedSuppressorNode> getUnusedSuppressorNodes(ASTAnnotation annotation) {
        if (TypeTestUtil.isA(SuppressWarnings.class, annotation)) {
            boolean entireAnnotationIsUnused = true;
            Set<ASTMemberValue> unusedParts = new HashSet<>();
            for (ASTMemberValue value : annotation.getFlatValue(ASTMemberValuePair.VALUE_ATTR)) {
                boolean suppressedAny = annotation.getUserMap().getOrDefault(KEY_SUPPRESSED_RULES, Boolean.FALSE);
                if (suppressedAny) {
                    entireAnnotationIsUnused = false;
                } else {
                    Object constVal = value.getConstValue();
                    if (constVal instanceof String) {
                        String stringVal = (String) constVal;
                        if (stringVal.startsWith("PMD")) {
                            // we don't report other kinds of warnings, although maybe we should
                            unusedParts.add(value);
                        } else {
                            entireAnnotationIsUnused = false;
                        }
                    }
                }
            }

            if (entireAnnotationIsUnused) {
                return Collections.singleton(
                    new UnusedSuppressorNode() {
                        @Override
                        public Reportable getLocation() {
                            return annotation;
                        }

                        @Override
                        public String unusedReason() {
                            return "Unnecessary PMD suppression annotation";
                        }
                    }
                );
            } else {
                return toUnusedSuppressors(unusedParts);
            }
        }
        return Collections.emptySet();
    }

    private static Set<UnusedSuppressorNode> toUnusedSuppressors(Set<ASTMemberValue> unusedParts) {
        Set<UnusedSuppressorNode> unused = new HashSet<>();
        for (ASTMemberValue value : unusedParts) {
            unused.add(new UnusedSuppressorNode() {

                @Override
                public Reportable getLocation() {
                    return value;
                }

                @Override
                public String unusedReason() {
                    return "Unnecessary PMD suppression";
                }
            });
        }
        return unused;
    }
}
