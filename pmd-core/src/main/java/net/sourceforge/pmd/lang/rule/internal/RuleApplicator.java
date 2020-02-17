/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
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
    // and there's no need to perform more topological checks when freezing it

    // This has an excellent cache hit ratio on longer runs, making the indexing
    // time insignificant
    private final NodeIdx idx;

    private RuleApplicator(NodeIdx index) {
        this.idx = index;
    }


    public void index(Collection<? extends Node> nodes) {
        idx.prepare();

        for (Node root : nodes) {
            indexTree(root, idx);
        }

        idx.complete();
    }

    public void apply(Collection<? extends Rule> rules, RuleContext ctx) {
        applyOnIndex(idx, rules, ctx);
    }

    private void applyOnIndex(NodeIdx idx, Collection<? extends Rule> rules, RuleContext ctx) {
        for (Rule rule : rules) {
            if (!RuleSet.applies(rule, ctx.getLanguageVersion())) {
                continue;
            }

            Iterator<? extends Node> targets = rule.getTargetingStrategy().getVisitedNodes(idx);
            while (targets.hasNext()) {
                Node node = targets.next();

                try (TimedOperation rcto = TimeTracker.startOperation(TimedOperationCategory.RULE, rule.getName())) {
                    rule.apply(node, ctx);
                    rcto.close(1);
                } catch (RuntimeException e) {
                    if (ctx.isIgnoreExceptions()) {
                        ctx.getReport().addError(new ProcessingError(e, ctx.getSourceCodeFilename()));

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


    public static RuleApplicator build(Iterable<? extends Rule> rules) {
        ApplicatorBuilder builder = new ApplicatorBuilder();
        for (Rule it : rules) {
            it.getTargetingStrategy().prepare(builder);
        }
        return builder.build();
    }


    private void indexTree(Node top, NodeIdx idx) {
        idx.indexNode(top);
        for (Node child : top.children()) {
            indexTree(child, idx);
        }
    }

    public static class ApplicatorBuilder {

        private final Set<String> namesToIndex = new HashSet<>();
        private final Set<Class<? extends Node>> classesToIndex = new HashSet<>();


        void registerXPathNames(Set<String> names) {
            namesToIndex.addAll(names);
        }

        void registerClasses(Set<Class<? extends Node>> names) {
            classesToIndex.addAll(names);
        }

        RuleApplicator build() {
            return new RuleApplicator(new NodeIdx(namesToIndex, classesToIndex));
        }
    }


    static class NodeIdx {

        private final LatticeRelation<Class<?>, Node> byClass;
        private final Set<String> interestingNames;
        private final Map<String, List<Node>> byName;


        NodeIdx(Set<String> interestingNames, Set<Class<? extends Node>> classesToIndex) {

            byClass = new LatticeRelation<>(
                TopoOrder.TYPE_HIERARCHY_ORDERING,
                classesToIndex,
                Class::getSimpleName
            );
            this.interestingNames = interestingNames;
            byName = new HashMap<>();
        }

        void indexNode(Node n) {
            if (interestingNames.contains(n.getXPathNodeName())) {
                byName.computeIfAbsent(n.getXPathNodeName(), k -> new ArrayList<>()).add(n);
            }
            byClass.put(n.getClass(), n);
        }

        void complete() {
            byClass.freezeTopo();
        }

        void prepare() {
            byClass.unfreezeTopo();
            byClass.clearValues();
            byName.clear();
        }

        // TODO this could be parameterized by a DataKey and extensible
        Iterator<Node> getByName(String n) {
            return byName.getOrDefault(n, Collections.emptyList()).iterator();
        }

        Iterator<Node> getByClass(Class<? extends Node> n) {
            return byClass.get(n).iterator();
        }

        Iterator<Node> getByName(Collection<String> n) {
            return IteratorUtil.flatMap(n.iterator(), this::getByName);
        }

        Iterator<Node> getByClass(Collection<? extends Class<? extends Node>> n) {
            return IteratorUtil.flatMap(n.iterator(), this::getByClass);
        }
    }
}
