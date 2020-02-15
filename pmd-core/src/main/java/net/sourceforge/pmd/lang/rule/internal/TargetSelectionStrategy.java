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
public abstract class TargetSelectionStrategy {

    TargetSelectionStrategy() {
        // package private
    }

    abstract void prepare(ApplicatorBuilder idx);


    abstract Iterator<? extends Node> getVisitedNodes(NodeIdx index);


    public static final class StringRulechainVisits extends TargetSelectionStrategy {

        private final Set<String> visits;

        public StringRulechainVisits(Collection<String> visits) {
            this.visits = new HashSet<>(visits);
        }

        @Override
        void prepare(ApplicatorBuilder builder) {
            // nothing to do
            builder.registerXPathNames(visits);
        }

        @Override
        Iterator<? extends Node> getVisitedNodes(NodeIdx index) {
            return index.getByName(visits);
        }
    }

    public static final class ClassRulechainVisits extends TargetSelectionStrategy {

        public static final TargetSelectionStrategy ROOT_ONLY = new ClassRulechainVisits(Collections.singleton(RootNode.class));

        private final Set<Class<? extends Node>> visits;

        public ClassRulechainVisits(Collection<Class<? extends Node>> visits) {
            this.visits = new HashSet<>(visits);
        }

        @Override
        void prepare(ApplicatorBuilder builder) {
            // builder doesn't support filtering classes
            //
            // Even when there are only a few rules, and the indexing
            // time outweighs the rule application time, the parsing/symbol table/ type res
            // vastly outweighs both of those. So it might be plain unnecessary

            // The only way to implement that I can think of would become very inefficient
            // when the set of interesting classes becomes large.

            // TODO The only optimisation that would be relevant, would be to skip
            //  indexing if the only found strategy is ROOT_ONLY
        }

        @Override
        Iterator<? extends Node> getVisitedNodes(NodeIdx index) {
            return index.getByClass(visits);
        }
    }

}
