/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static net.sourceforge.pmd.internal.util.IteratorUtil.toIterable;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;

/** Applies a set of rules to a set of ASTs. */
public class RuleApplicator {

    private static final Logger LOG = Logger.getLogger(RuleApplicator.class.getName());
    // we reuse the type lattice from run to run, eventually it converges
    // towards the final topology (all node types have been encountered)
    // and there's no need to perform more topological checks when freezing
    // it
    // This has a cache hit ratio of more than 99% on longer runs, making
    // the indexing time insignificant
    private final NodeIdx idx = new NodeIdx();

    public void apply(Collection<? extends Node> nodes, Collection<? extends Rule> rules, RuleContext ctx) {
        idx.clear();

        for (Node root : nodes) {
            indexTree(root, idx);
        }

        idx.complete();

        applyOnIndex(idx, rules, ctx);
    }

    private void applyOnIndex(NodeIdx idx, Collection<? extends Rule> rules, RuleContext ctx) {
        for (Rule rule : rules) {

            for (Node node : toIterable(rule.getTargetingStrategy().getVisitedNodes(idx))) {

                try (TimedOperation rcto = TimeTracker.startOperation(TimedOperationCategory.RULE, rule.getName())) {
                    rule.apply(node, ctx);
                    rcto.close(1);
                } catch (RuntimeException e) {
                    if (ctx.isIgnoreExceptions()) {
                        ctx.getReport().addError(new Report.ProcessingError(e, ctx.getSourceCodeFilename()));

                        if (LOG.isLoggable(Level.WARNING)) {
                            LOG.log(Level.WARNING, "Exception applying rule " + rule.getName() + " on file "
                                + ctx.getSourceCodeFilename() + ", continuing with next rule", e);
                        }
                    } else {
                        throw e;
                    }
                }
            }
        }
    }


    private void indexTree(Node top, NodeIdx idx) {
        idx.indexNode(top);
        for (Node child : top.children()) {
            indexTree(child, idx);
        }
    }

    static class NodeIdx {

        private final LatticeRelation<Class<?>, Set<Node>> byClass;
        private final Map<String, List<Node>> byName;


        NodeIdx() {
            byClass = new LatticeRelation<>(
                IdMonoid.forSet(),
                IdMonoid.forMutableSet(),
                TopoOrder.TYPE_HIERARCHY_ORDERING,
                NodeIdx::filterClassFromIndex,
                Class::getSimpleName
            );
            byName = new HashMap<>();
        }

        // prune non-public classes from the index, also abstract classes,
        // which are supposed to be implementation details
        private static boolean filterClassFromIndex(Class<?> klass) {
            return Modifier.isPublic(klass.getModifiers())
                && Node.class.isAssignableFrom(klass)
                && (!Modifier.isAbstract(klass.getModifiers()) || klass.isInterface());
        }

        void indexNode(Node n) {
            byName.computeIfAbsent(n.getXPathNodeName(), k -> new ArrayList<>()).add(n);
            byClass.put(n.getClass(), Collections.singleton(n));
        }

        /**
         * Freezing the lattice divides by on-average 6 the number of
         * recursive computations (and hence of lists created).
         */
        void complete() {
            byClass.freezeTopo();
        }

        void clear() {
            byClass.unfreezeTopo();
            byClass.clearValues();
            byName.clear();
        }

        // TODO this could be parameterized by a DataKey and extensible
        Iterator<Node> getByName(String n) {
            return byName.getOrDefault(n, Collections.emptyList()).iterator();
        }

        Iterator<Node> getByClass(Class<?> n) {
            return byClass.get(n).iterator();
        }

        Iterator<Node> getByName(Collection<String> n) {
            return IteratorUtil.flatMap(n.iterator(), this::getByName);
        }

        Iterator<Node> getByClass(Collection<? extends Class<?>> n) {
            return IteratorUtil.flatMap(n.iterator(), this::getByClass);
        }
    }
}
