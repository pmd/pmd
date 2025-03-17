/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;
import net.sourceforge.pmd.util.DataMap;
import net.sourceforge.pmd.util.DataMap.SimpleDataKey;

public abstract class AbstractAnnotationSuppressor<A extends Node> implements ViolationSuppressor {

    private final Class<A> annotationClass;

    protected AbstractAnnotationSuppressor(Class<A> annotationClass) {
        this.annotationClass = annotationClass;
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
        return tree.descendants(annotationClass).crossFindBoundaries().toStream().map(this::getUnusedSuppressorNodes).flatMap(Set::stream).collect(Collectors.toSet());
    }


    private boolean contextSuppresses(Node node, Rule rule) {
        if (suppresses(node, rule)) {
            return true;
        }

        if (node instanceof RootNode) {
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
        return walkAnnotation(annotation, (parm, stringValue) -> {
            if (annotationParamSuppresses(stringValue, rule)) {
                parm.getUserMap().compute(KEY_SUPPRESSED_ANY_VIOLATION, a -> Boolean.TRUE);
                return true;
            }
            return false;
        });
    }

    protected abstract boolean walkAnnotation(A annotation, AnnotationWalkCallbacks callbacks);

    protected abstract NodeStream<A> getAnnotations(Node n);


    protected boolean annotationParamSuppresses(String stringVal, Rule rule) {
        return "PMD".equals(stringVal) || ("PMD." + rule.getName()).equals(stringVal) || "all".equals(stringVal);
    }

    protected interface AnnotationWalkCallbacks {

        boolean processNode(Node annotationParam, String stringValue);

    }

    private static final SimpleDataKey<Boolean> KEY_SUPPRESSED_ANY_VIOLATION = DataMap.simpleDataKey("pmd.core.suppressed.any");

    /**
     * Return the set of rule names for which the given annotation has suppressed at least one violation.
     *
     * @param annotation An annotation
     */
    private Set<UnusedSuppressorNode> getUnusedSuppressorNodes(A annotation) {
        Set<Node> unusedParts = new HashSet<>();
        MutableBoolean entireAnnotationIsUnused = new MutableBoolean(false);
        walkAnnotation(annotation, (annotationParam, stringValue) -> {
            boolean suppressedAny = annotationParam.getUserMap().getOrDefault(KEY_SUPPRESSED_ANY_VIOLATION, Boolean.FALSE);
            if (suppressedAny) {
                entireAnnotationIsUnused.setFalse();
            } else {
                if (stringValue.startsWith("PMD")) {
                    // we don't report other kinds of warnings, although maybe we should
                    unusedParts.add(annotationParam);
                } else {
                    entireAnnotationIsUnused.setFalse();
                }
            }
            return false;
        });

        if (entireAnnotationIsUnused.booleanValue()) {
            return Collections.singleton(new UnusedSuppressorNode() {
                @Override
                public Reportable getLocation() {
                    return annotation;
                }

                @Override
                public String unusedReason() {
                    return "Unnecessary PMD suppression annotation";
                }
            });
        } else {
            return toUnusedSuppressors(unusedParts);
        }
    }

    private static Set<UnusedSuppressorNode> toUnusedSuppressors(Set<Node> unusedParts) {
        Set<UnusedSuppressorNode> unused = new HashSet<>();
        for (Node value : unusedParts) {
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
