/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.impl.UnnecessaryPmdSuppressionRule;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;
import net.sourceforge.pmd.util.OptionalBool;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Base class for a {@link ViolationSuppressor} that uses annotations
 * of the source language to suppress some warnings.
 *
 * @param <A> Class of the node type that models annotations in the AST
 *           of the language
 * @since 7.14.0
 */
public abstract class AbstractAnnotationSuppressor<A extends Node> implements ViolationSuppressor {

    private final Class<A> annotationNodeType;

    protected AbstractAnnotationSuppressor(Class<A> annotationClass) {
        this.annotationNodeType = annotationClass;
    }

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
        return tree.descendants(annotationNodeType)
                .crossFindBoundaries()
                .toStream()
                .map(this::getUnusedSuppressorNodes)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private boolean contextSuppresses(Node node, Rule rule) {
        if (suppresses(node, rule)) {
            return true;
        }

        if (node instanceof RootNode) {
            // This logic is here to suppress violations on the root node
            // based on an annotation of its child. In Java for instance
            // you cannot annotate the root node, because you can only annotate
            // declarations. But an annotation on a toplevel class, or on
            // the package declaration, would suppress violations on the root
            // node as well.
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
    private boolean suppresses(final Node node, Rule rule) {
        return getAnnotations(node).any(it -> annotationSuppresses(it, rule));
    }

    private boolean annotationSuppresses(A annotation, Rule rule) {
        List<AnnotationPartWrapper> applicableParts = new ArrayList<>();
        walkAnnotation(annotation, (parm, stringValue) -> {
            if (annotationParamSuppresses(stringValue, rule)) {
                applicableParts.add(new AnnotationPartWrapper(parm, stringValue));
            }
            return false;
        });

        AnnotationPartWrapper mostSpecific = getMostSpecific(applicableParts);
        if (mostSpecific != null) {
            mostSpecific.node.getUserMap().compute(KEY_SUPPRESSED_ANY_VIOLATION, a -> Boolean.TRUE);
            return true;
        }
        return false;
    }

    /**
     * If several parts match (eg "PMD.RuleName" and "PMD") then we take the most specific and mark it as used.
     */
    private static @Nullable AnnotationPartWrapper getMostSpecific(List<AnnotationPartWrapper> parts) {
        if (parts.isEmpty()) {
            return null;
        } else if (parts.size() == 1) {
            return parts.get(0);
        }
        parts.sort(AbstractAnnotationSuppressor::compareSpecificity);
        if (parts.stream().allMatch(p -> isPmdSuppressor(p.stringValue))) {
            // If they are all pmd suppressors then we can take the most specific and assume that
            // the more generic
            return parts.get(parts.size() - 1);
        } else {
            // Otherwise the non-pmd suppressors are found at the start of the list as they are
            // classified as less-specific than any PMD suppressor.
            return parts.get(0);
        }
    }

    /**
     * Walk the individual suppression specifications of an annotation (usually strings within the annotation).
     * For each of those, call the callback. If the callback returns true, interrupt the walk and return true.
     * Otherwise, continue the walk.
     *
     * @param annotation An annotation
     * @param callbacks Callback object
     *
     * @return True if the callback returned true once
     */
    protected abstract boolean walkAnnotation(A annotation, AnnotationWalkCallbacks callbacks);

    /** Return the annotations attached to the given node. */
    protected abstract NodeStream<A> getAnnotations(Node n);

    /** Return a nice toString for the given annotation. */
    protected String getAnnotationName(A annotation) {
        return "@SuppressWarnings annotation";
    }

    /**
     * Return whether one of the annotation params suppresses the given rule.
     * The default implementation uses sensible values, so call super.
     */
    protected boolean annotationParamSuppresses(String stringVal, Rule rule) {
        return "PMD".equals(stringVal) || ("PMD." + rule.getName()).equals(stringVal) || "all".equals(stringVal);
    }

    /**
     * Return whether the annotation param may be suppressing warnings from other tools.
     * If this returns NO, then the parameter may be marked as unused and reported by the
     * rule {@link UnnecessaryPmdSuppressionRule}.
     */
    protected OptionalBool isSuppressingNonPmdWarnings(String stringVal, A annotation) {
        if (isPmdSuppressor(stringVal)) {
            return OptionalBool.NO;
        }
        return OptionalBool.UNKNOWN;
    }

    /** Callbacks for a walk over an annotation. */
    protected interface AnnotationWalkCallbacks {

        /**
         * Process one parameter of the annotation being walked.
         *
         * @param annotationParam The node corresponding to the parameter
         * @param stringValue The string extracted from the node
         */
        boolean processNode(Node annotationParam, @NonNull String stringValue);
    }

    private static final SimpleDataKey<Boolean> KEY_SUPPRESSED_ANY_VIOLATION =
            DataMap.simpleDataKey("pmd.core.suppressed.any");

    /**
     * Return the set of rule names for which the given annotation has suppressed at least one violation.
     *
     * @param annotation An annotation
     */
    private Set<UnusedSuppressorNode> getUnusedSuppressorNodes(A annotation) {
        Set<UnusedSuppressorNode> unusedParts = new HashSet<>();
        MutableBoolean entireAnnotationIsUnused = new MutableBoolean(true);
        MutableBoolean anySuppressor = new MutableBoolean(false);
        walkAnnotation(annotation, (annotationParam, stringValue) -> {
            anySuppressor.setTrue();

            boolean suppressedAny =
                    annotationParam.getUserMap().getOrDefault(KEY_SUPPRESSED_ANY_VIOLATION, Boolean.FALSE);
            if (suppressedAny) {
                entireAnnotationIsUnused.setFalse();
            } else {
                if (isSuppressingNonPmdWarnings(stringValue, annotation) == OptionalBool.NO) {
                    unusedParts.add(makeAnnotationPartSuppressor(annotation, annotationParam, stringValue));
                } else {
                    entireAnnotationIsUnused.setFalse();
                }
            }
            return false;
        });

        if (anySuppressor.isTrue() && entireAnnotationIsUnused.isTrue()) {
            return Collections.singleton(makeFullAnnotationSuppressor(annotation));
        } else {
            return unusedParts;
        }
    }

    private static boolean isPmdSuppressor(String stringValue) {
        return "PMD".equals(stringValue) || stringValue.startsWith("PMD.");
    }

    private SuppressorNodeImpl makeAnnotationPartSuppressor(A annotation, Node annotationPart, String stringValue) {
        String message = "Unnecessary suppression \"" + stringValue + "\" in " + getAnnotationName(annotation);
        return new SuppressorNodeImpl(annotationPart, message);
    }

    private SuppressorNodeImpl makeFullAnnotationSuppressor(A annotation) {
        String message = "Unnecessary " + getAnnotationName(annotation);
        return new SuppressorNodeImpl(annotation, message);
    }

    private static final class SuppressorNodeImpl implements UnusedSuppressorNode {
        private final Node location;
        private final String message;

        SuppressorNodeImpl(Node node, String message) {
            this.location = node;
            this.message = message;
        }

        @Override
        public Reportable getLocation() {
            return location;
        }

        @Override
        public String unusedReason() {
            return message;
        }
    }

    private static int compareSpecificity(AnnotationPartWrapper fstPart, AnnotationPartWrapper sndPart) {
        String fst = fstPart.stringValue;
        String snd = sndPart.stringValue;

        if (fst.equals(snd)) {
            return 0;
        }
        if ("all".equals(snd)) {
            return 1;
        } else if ("all".equals(fst)) {
            return -1;
        }

        // this is the case for "fallthrough" and such, they may suppress warnings that PMD did not cause
        if (!isPmdSuppressor(snd)) {
            return 1;
        } else if (!isPmdSuppressor(fst)) {
            return -1;
        }

        if ("PMD".equals(snd)) {
            return 1;
        } else if ("PMD".equals(fst)) {
            return -1;
        } else {
            throw AssertionUtil.shouldNotReachHere(
                    "Logically if we are here then both strings are of the form PMD.RuleName and should therefore be equal!");
        }
    }

    private static final class AnnotationPartWrapper {
        private final Node node;
        private final String stringValue;

        private AnnotationPartWrapper(Node node, String stringValue) {
            this.node = node;
            this.stringValue = stringValue;
        }
    }
}
