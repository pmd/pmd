/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Adds additional key/value pairs to a violation in a language-specific manner.
 * The keys are completely free. {@link RuleViolation} defines some of these keys.
 */
@FunctionalInterface
public interface ViolationDecorator {

    /**
     * Compute additional key/value pairs about the violation that should be
     * reflected in {@link RuleViolation#getAdditionalInfo()}. This additional
     * info should be accumulated into the {@code additionalInfo} parameter.
     *
     * @param violationNode  The node on which the violation was reported
     * @param additionalInfo Accumulator
     */
    void decorate(Node violationNode, Map<String, String> additionalInfo);

    static Map<String, String> apply(ViolationDecorator decorator, Node violationNode) {
        Map<String, String> additionalInfo = new HashMap<>();
        decorator.decorate(violationNode, additionalInfo);
        if (!additionalInfo.isEmpty()) {
            return Collections.unmodifiableMap(additionalInfo);
        } else {
            return Collections.emptyMap();
        }
    }

    /**
     * Apply several decorators in a chain.
     */
    static ViolationDecorator chain(List<? extends ViolationDecorator> list) {
        return (node, map) -> {
            for (ViolationDecorator decorator : list) {
                decorator.decorate(node, map);
            }
        };
    }

    static ViolationDecorator noop() {
        return (node, map) -> { };
    }
}
