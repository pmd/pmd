/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValue;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.ASTModifierList;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.errorprone.ImplicitSwitchFallThroughRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.AbstractAnnotationSuppressor;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
import net.sourceforge.pmd.util.OptionalBool;

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
final class JavaAnnotationSuppressor extends AbstractAnnotationSuppressor<ASTAnnotation> {

    private static final Set<String> UNUSED_RULES = new HashSet<>(Arrays.asList("UnusedPrivateField", "UnusedLocalVariable", "UnusedPrivateMethod", "UnusedFormalParameter", "UnusedAssignment", "SingularField"));
    private static final Set<String> SERIAL_RULES = new HashSet<>(Arrays.asList("BeanMembersShouldSerialize", "NonSerializableClass", "MissingSerialVersionUID"));


    static final List<ViolationSuppressor> ALL_JAVA_SUPPRESSORS = listOf(new JavaAnnotationSuppressor());

    private JavaAnnotationSuppressor() {
        super(ASTAnnotation.class);
    }


    @Override
    protected NodeStream<ASTAnnotation> getAnnotations(Node n) {
        if (n instanceof Annotatable) {
            return ((Annotatable) n).getDeclaredAnnotations();
        }
        return NodeStream.empty();
    }

    @Override
    protected boolean annotationParamSuppresses(String stringVal, Rule rule) {
        return super.annotationParamSuppresses(stringVal, rule)
            || "serial".equals(stringVal) && SERIAL_RULES.contains(rule.getName())
            || "unused".equals(stringVal) && UNUSED_RULES.contains(rule.getName())
            || "fallthrough".equals(stringVal) && rule instanceof ImplicitSwitchFallThroughRule;
    }

    @Override
    protected boolean walkAnnotation(ASTAnnotation annotation, AnnotationWalkCallbacks callbacks) {
        if (TypeTestUtil.isA(SuppressWarnings.class, annotation)) {
            for (ASTMemberValue value : annotation.getFlatValue(ASTMemberValuePair.VALUE_ATTR)) {
                Object constVal = value.getConstValue();
                if (constVal instanceof String) {
                    if (callbacks.processNode(value, (String) constVal)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    JavaNode getAnnotationScope(ASTAnnotation a) {
        if (a.getParent() instanceof ASTModifierList) return a.getParent().getParent();
        return null;
    }

    @Override
    protected OptionalBool isSuppressingNonPmdWarnings(String stringVal, ASTAnnotation annotation) {
        if ("unused".equals(stringVal)) {
            JavaNode scope = getAnnotationScope(annotation);
            if (scope != null && scope.descendants(ASTVariableId.class).crossFindBoundaries().none(it -> it.getLocalUsages().isEmpty())) {
                return OptionalBool.NO;
            }
        }
        return super.isSuppressingNonPmdWarnings(stringVal, annotation);
    }
}
