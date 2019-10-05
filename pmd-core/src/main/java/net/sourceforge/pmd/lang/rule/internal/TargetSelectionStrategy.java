/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;


import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.internal.RuleApplicator.NodeIdx;

/** A strategy for selecting nodes that will be targeted by a rule. */
public abstract class TargetSelectionStrategy {

    TargetSelectionStrategy() {

    }


    abstract Iterator<? extends Node> getVisitedNodes(NodeIdx index);


    public static final class StringRulechainVisits extends TargetSelectionStrategy {

        private final Set<String> visits;

        public StringRulechainVisits(Set<String> visits) {
            this.visits = visits;
        }


        @Override
        Iterator<? extends Node> getVisitedNodes(NodeIdx index) {
            return index.getByName(visits).iterator();
        }
    }

    public static final class ClassRulechainVisits extends TargetSelectionStrategy {

        public static final TargetSelectionStrategy ROOT_ONLY = new ClassRulechainVisits(Collections.singleton(RootNode.class));

        private final Set<? extends Class<?>> visits;

        public ClassRulechainVisits(Set<? extends Class<?>> visits) {
            this.visits = visits;
        }


        @Override
        Iterator<? extends Node> getVisitedNodes(NodeIdx index) {
            return index.getByClass(visits).iterator();
        }
    }

}
