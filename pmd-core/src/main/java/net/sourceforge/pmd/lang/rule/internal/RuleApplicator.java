/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.ast.Node;

/** Applies a set of rules to a set of ASTs. */
public class RuleApplicator {

    private static final Logger LOG = Logger.getLogger(RuleApplicator.class.getName());
    // we reuse the type lattice from run to run, eventually it converges
    // towards the final topology (all node types have been encountered)
    // This has excellent performance! Indexing time is insignificant
    // compared to rule application for any non-trivial ruleset. Even
    // when you use a single rule, indexing time is insignificant compared
    // to eg type resolution.

    private final TreeIndex idx;

    public RuleApplicator(TreeIndex index) {
        this.idx = index;
    }


    public void index(Collection<? extends Node> nodes) {
        idx.reset();
        for (Node root : nodes) {
            indexTree(root, idx);
        }
    }

    public void apply(Collection<? extends Rule> rules, RuleContext ctx) {
        applyOnIndex(idx, rules, ctx);
    }

    private void applyOnIndex(TreeIndex idx, Collection<? extends Rule> rules, RuleContext ctx) {
        for (Rule rule : rules) {

            Iterator<? extends Node> targets = rule.getTargetSelector().getVisitedNodes(idx);
            while (targets.hasNext()) {
                Node node = targets.next();
                if (!RuleSet.applies(rule, node.getLanguageVersion())) {
                    continue;
                }

                try (TimedOperation rcto = TimeTracker.startOperation(TimedOperationCategory.RULE, rule.getName())) {
                    rule.apply(node, ctx);
                    rcto.close(1);
                } catch (RuntimeException e) {
                    if (ctx.isIgnoreExceptions()) {
                        ctx.reportError(new ProcessingError(e, ctx.getSourceCodeFilename()));

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

    private void indexTree(Node top, TreeIndex idx) {
        idx.indexNode(top);
        for (Node child : top.children()) {
            indexTree(child, idx);
        }
    }

    public static RuleApplicator build(Iterable<? extends Rule> rules) {
        TargetSelectorInternal.ApplicatorBuilder builder = new TargetSelectorInternal.ApplicatorBuilder();
        for (Rule it : rules) {
            it.getTargetSelector().prepare(builder);
        }
        return builder.build();
    }

}
