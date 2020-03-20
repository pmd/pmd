/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * Internal API of {@link RuleTargetSelector}.
 */
public abstract class TargetSelectorInternal {

    protected TargetSelectorInternal() {
        // inheritance only
    }

    protected abstract void prepare(ApplicatorBuilder builder);


    protected abstract Iterator<? extends Node> getVisitedNodes(TreeIndex index);


    protected static class ApplicatorBuilder {

        private final Set<String> namesToIndex = new HashSet<>();
        private final Set<Class<? extends Node>> classesToIndex = new HashSet<>();


        public void registerXPathNames(Set<String> names) {
            namesToIndex.addAll(names);
        }

        public void registerClasses(Set<Class<? extends Node>> names) {
            classesToIndex.addAll(names);
        }

        RuleApplicator build() {
            return new RuleApplicator(new TreeIndex(namesToIndex, classesToIndex));
        }
    }
}
