/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.exception.ExceptionContext;

import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.util.StringUtil;

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


    public void index(RootNode root) {
        idx.reset();
        indexTree(root, idx);
    }

    public void apply(Collection<? extends Rule> rules, FileAnalysisListener listener) {
        applyOnIndex(idx, rules, listener);
    }

    private void applyOnIndex(TreeIndex idx, Collection<? extends Rule> rules, FileAnalysisListener listener) {
        for (Rule rule : rules) {
            RuleContext ctx = RuleContext.create(listener, rule);
            rule.start(ctx);
            try {

                Iterator<? extends Node> targets = rule.getTargetSelector().getVisitedNodes(idx);
                while (targets.hasNext()) {
                    Node node = targets.next();
                    if (!RuleSet.applies(rule, node.getAstInfo().getLanguageVersion())) {
                        continue;
                    }

                    try (TimedOperation rcto = TimeTracker.startOperation(TimedOperationCategory.RULE, rule.getName())) {
                        rule.apply(node, ctx);
                        rcto.close(1);
                    } catch (RuntimeException e) {
                        reportOrRethrow(listener, rule, node, AssertionUtil.contexted(e), true);
                    } catch (StackOverflowError e) {
                        reportOrRethrow(listener, rule, node, AssertionUtil.contexted(e), SystemProps.isErrorRecoveryMode());
                    } catch (AssertionError e) {
                        reportOrRethrow(listener, rule, node, AssertionUtil.contexted(e), SystemProps.isErrorRecoveryMode());
                    }
                }
            } finally {
                rule.end(ctx);
            }
        }
    }


    private <E extends Throwable> void reportOrRethrow(FileAnalysisListener listener, Rule rule, Node node, E e, boolean reportAndDontThrow) throws E {
        if (e instanceof ExceptionContext) {
            ((ExceptionContext) e).addContextValue("Rule applied on node", node);
        }

        if (reportAndDontThrow) {
            reportException(listener, rule, node, e);
        } else {
            throw e;
        }
    }


    private void reportException(FileAnalysisListener listener, Rule rule, Node node, Throwable e) {
        // The listener handles logging if needed,
        // it may also rethrow the error.
        listener.onError(new ProcessingError(e, node.getAstInfo().getFileName()));

        if (LOG.isLoggable(Level.WARNING)) { //fixme
            LOG.log(Level.WARNING, "Exception applying rule " + rule.getName() + " on file "
                + node.getAstInfo().getFileName() + ", continuing with next rule", e);

            String nodeToString = StringUtil.elide(node.toString(), 600, " ... (truncated)");
            LOG.log(Level.WARNING, "Exception occurred on node " + nodeToString);
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
