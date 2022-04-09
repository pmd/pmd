/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;


import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.internal.TargetSelectorInternal;
import net.sourceforge.pmd.lang.rule.internal.TreeIndex;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * A strategy for selecting nodes that will be targeted by a rule.
 *
 * @see Rule#getTargetSelector()
 */
public abstract class RuleTargetSelector extends TargetSelectorInternal {

    RuleTargetSelector() {
        // package private, prevents subclassing (all the API is protected and internal)
    }

    /**
     * Target nodes having one of the given XPath local name.
     *
     * @param names XPath names
     *
     * @return A selector
     *
     * @throws IllegalArgumentException If the argument is empty
     */
    public static RuleTargetSelector forXPathNames(Collection<String> names) {
        if (names.isEmpty()) {
            throw new IllegalArgumentException("Cannot visit zero nodes");
        }
        return new StringRulechainVisits(names);
    }

    /**
     * Target nodes that are subtypes of any of the given classes.
     *
     * @param types Node types
     *
     * @return A selector
     *
     * @throws IllegalArgumentException If the argument is empty
     * @throws NullPointerException     If the argument is null
     * @throws NullPointerException     If any of the elements is null
     */
    public static RuleTargetSelector forTypes(Collection<Class<? extends Node>> types) {
        if (types.isEmpty()) {
            throw new IllegalArgumentException("Cannot visit zero types");
        }
        return new ClassRulechainVisits(types);
    }

    /**
     * Target nodes that are subtypes of any of the given classes.
     *
     * @param types Node types
     *
     * @return A selector
     *
     * @throws NullPointerException if any of the arguments is null
     */
    @SafeVarargs
    public static RuleTargetSelector forTypes(Class<? extends Node> first, Class<? extends Node>... types) {
        return forTypes(CollectionUtil.listOf(first, types));
    }

    /**
     * Target only the root of the tree.
     */
    public static RuleTargetSelector forRootOnly() {
        return ClassRulechainVisits.ROOT_ONLY;
    }

    @InternalApi
    public boolean isRuleChain() {
        return this != ClassRulechainVisits.ROOT_ONLY; // NOPMD #3205
    }

    private static final class StringRulechainVisits extends RuleTargetSelector {

        private final Set<String> visits;

        StringRulechainVisits(Collection<String> visits) {
            this.visits = new HashSet<>(visits);
        }

        @Override
        protected void prepare(ApplicatorBuilder builder) {
            builder.registerXPathNames(visits);
        }

        @Override
        protected Iterator<? extends Node> getVisitedNodes(TreeIndex index) {
            return index.getByName(visits);
        }

        @Override
        public String toString() {
            return "XPathNameVisits" + visits;
        }
    }

    private static final class ClassRulechainVisits extends RuleTargetSelector {

        public static final RuleTargetSelector ROOT_ONLY = new ClassRulechainVisits(Collections.singleton(RootNode.class));

        private final Set<Class<? extends Node>> visits;

        ClassRulechainVisits(Collection<Class<? extends Node>> visits) {
            if (visits.contains(null)) {
                throw new NullPointerException("Null element in class visits " + visits);
            }
            this.visits = new LinkedHashSet<>(visits);
        }

        @Override
        protected void prepare(ApplicatorBuilder builder) {
            builder.registerClasses(visits);
        }

        @Override
        protected Iterator<? extends Node> getVisitedNodes(TreeIndex index) {
            return index.getByClass(visits);
        }

        @Override
        public String toString() {
            return "ClassVisits" + visits;
        }
    }
}
