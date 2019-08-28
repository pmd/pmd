/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.ast.Node;

/** Applies a set of rules to a set of ASTs. */
public class RuleApplicator {

    private static final Logger LOG = Logger.getLogger(RuleApplicator.class.getName());

    public void apply(List<Node> nodes, List<Rule> rules, RuleContext ctx) {
        NodeIdx idx = new NodeIdx();

        for (Node root : nodes) {
            indexTree(root, idx);
        }

        idx.complete();

        applyRecursive(idx, rules, ctx);
    }

    private void applyRecursive(NodeIdx idx, Collection<Rule> rules, RuleContext ctx) {
        for (Rule rule : rules) {

            Stream<Node> nodes = rule.getRuleChainVisits().isEmpty() ? idx.getByClass(rule.getRuleChainVisitsSet())
                                                                     : idx.getByName(rule.getRuleChainVisits());

            Iterable<Node> it = nodes::iterator;

            for (Node node : it) {
                try (TimedOperation rcto = TimeTracker.startOperation(TimedOperationCategory.RULE, rule.getName())) {
                    rule.apply(Collections.singletonList(node), ctx);
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
        for (int i = 0; i < top.jjtGetNumChildren(); i++) {
            indexTree(top.jjtGetChild(i), idx);
        }
    }

    private static class NodeIdx {

        private static final Monoid<List<Node>> monoid = Monoid.forList();
        private final Heap<Class<?>, List<Node>> byClass;
        private final Map<String, List<Node>> byName;

        public NodeIdx(int baseCap) {
            byClass = new Heap<>(monoid, TopoOrder.TYPE_ORDER, baseCap);
            byName = new HashMap<>(baseCap);
        }

        public NodeIdx() {
            byClass = new Heap<>(monoid, TopoOrder.TYPE_ORDER);
            byName = new HashMap<>();
        }

        public void indexNode(Node n) {
            byName.computeIfAbsent(n.getXPathNodeName(), k -> new ArrayList<>()).add(n);
            byClass.put(n.getClass(), Collections.singletonList(n));
        }

        public void complete() {
            byClass.freeze();
        }

        public Stream<Node> getByName(String n) {
            return byName.getOrDefault(n, Collections.emptyList()).stream();
        }

        public Stream<Node> getByClass(Class<?> n) {
            return byClass.get(n).stream();
        }

        public Stream<Node> getByName(Collection<String> n) {
            return n.stream().flatMap(this::getByName);
        }

        public Stream<Node> getByClass(Collection<Class<?>> n) {
            return n.stream().flatMap(this::getByClass);
        }
    }
}
