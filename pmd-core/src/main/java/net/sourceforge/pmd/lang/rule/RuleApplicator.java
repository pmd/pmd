/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.benchmark.TimedOperation;
import net.sourceforge.pmd.benchmark.TimedOperationCategory;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This is a base class for RuleChainVisitor implementations which extracts
 * interesting nodes from an AST, and lets each Rule visit the nodes it has
 * expressed interest in.
 */
public class RuleApplicator {

    private static final Logger LOG = Logger.getLogger(RuleApplicator.class.getName());


    public void apply(List<Node> nodes, List<Rule> rules, RuleContext ctx) {
        Map<Boolean, List<Rule>> isNotRChain = rules.stream().collect(Collectors.groupingBy(it -> it.getRuleChainVisits().isEmpty()));
        isNotRChain.getOrDefault(true, Collections.emptyList()).forEach(rule -> applySingleRule(nodes, ctx, rule));

        for (Node root : nodes) {
            applyRecursive(root, isNotRChain.getOrDefault(false, Collections.emptyList()), ctx);
        }
    }

    private void applyRecursive(Node node, Collection<Rule> rules, RuleContext ctx) {
        doApply(node, rules, ctx);
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            applyRecursive(node.jjtGetChild(i), rules, ctx);
        }
    }

    /**
     * Index a single node for visitation by rules.
     */
    protected void doApply(Node node, Collection<Rule> rules, RuleContext ctx) {
        for (Rule rule : rules) {
            if (rule.shouldVisit(node)) {
                applySingleRule(Collections.singletonList(node), ctx, rule);
            }
        }
    }

    private void applySingleRule(List<Node> nodes, RuleContext ctx, Rule rule) {
        try (TimedOperation rcto = TimeTracker.startOperation(TimedOperationCategory.RULE, rule.getName())) {
            rule.apply(nodes, ctx);
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
