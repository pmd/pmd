/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.internal.RuleApplicator.ApplicatorBuilder;
import net.sourceforge.pmd.lang.rule.internal.RuleApplicator.NodeIdx;

/** A strategy for selecting nodes that will be targeted by a rule. */
public abstract class RuleTargetSelector {

    RuleTargetSelector() {
        // package private
    }

    abstract void prepare(ApplicatorBuilder idx);


    abstract Iterator<? extends Node> getVisitedNodes(NodeIdx index);


    public static final class StringRulechainVisits extends RuleTargetSelector {

        private final Set<String> visits;

        public StringRulechainVisits(Collection<String> visits) {
            this.visits = new HashSet<>(visits);
        }

        @Override
        void prepare(ApplicatorBuilder builder) {
            builder.registerXPathNames(visits);
        }

        @Override
        Iterator<? extends Node> getVisitedNodes(NodeIdx index) {
            return index.getByName(visits);
        }
    }

    public static final class ClassRulechainVisits extends RuleTargetSelector {

        public static final RuleTargetSelector ROOT_ONLY = new ClassRulechainVisits(Collections.singleton(RootNode.class));

        private final Set<Class<? extends Node>> visits;

        public ClassRulechainVisits(Collection<Class<? extends Node>> visits) {
            this.visits = new HashSet<>(visits);
        }

        @Override
        void prepare(ApplicatorBuilder builder) {
            builder.registerClasses(visits);
        }

        @Override
        Iterator<? extends Node> getVisitedNodes(NodeIdx index) {
            return index.getByClass(visits);
        }
    }

}
