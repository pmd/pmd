package net.sourceforge.pmd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.ast.CompilationUnit;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.Benchmark;

/**
 * This is a base class for RuleChainVisitor implementations which
 * extracts interesting nodes from an AST, and lets each Rule visit
 * the nodes it has expressed interest in.
 */
public abstract class AbstractRuleChainVisitor implements RuleChainVisitor {
    /**
     * These are all the rules participating in the RuleChain, grouped by RuleSet.
     */
    protected Map<RuleSet, List<Rule>> ruleSetRules = new LinkedHashMap<RuleSet, List<Rule>>();

    /**
     * This is a mapping from node names to nodes instances for the current AST.
     */
    protected Map<String, List<SimpleNode>> nodeNameToNodes;

    /**
     * @see RuleChainVisitor#add(RuleSet, Rule)
     */
    public void add(RuleSet ruleSet, Rule rule) {
	if (!ruleSetRules.containsKey(ruleSet)) {
	    ruleSetRules.put(ruleSet, new ArrayList<Rule>());
	}
	ruleSetRules.get(ruleSet).add(rule);
    }

    /**
     * @see RuleChainVisitor#visitAll(List, RuleContext)
     */
    public void visitAll(List<CompilationUnit> astCompilationUnits, RuleContext ctx) {
        initialize();
        clear();

        // Perform a visitation of the AST to index nodes which need visiting by
        // type
        long start = System.nanoTime();
        indexNodes(astCompilationUnits, ctx);
        long end = System.nanoTime();
        Benchmark.mark(Benchmark.TYPE_RULE_CHAIN_VISIT, end - start, 1);

        // For each RuleSet, only if this source file applies
        for (RuleSet ruleSet : ruleSetRules.keySet()) {
            if (!ruleSet.applies(ctx.getSourceCodeFile())) {
        	continue;
            }
            // For each rule, allow it to visit the nodes it desires
            int visits = 0;
            start = System.nanoTime();
            for (Rule rule: ruleSetRules.get(ruleSet)) {
                final List<String> nodeNames = rule.getRuleChainVisits();
                for (int j = 0; j < nodeNames.size(); j++) {
                    List<SimpleNode> nodes = nodeNameToNodes.get(nodeNames.get(j));
                    for (SimpleNode node: nodes) {
                        // Visit with underlying Rule, not the RuleReference
                        while (rule instanceof RuleReference) {
                            rule = ((RuleReference)rule).getRule();
                        }
                        visit(rule, node, ctx);
                    }
                    visits += nodes.size();
                }
                end = System.nanoTime();
                Benchmark.mark(Benchmark.TYPE_RULE_CHAIN_RULE, rule.getName(), end - start, visits);
                start = end;
            }
        }
    }

    /**
     * Visit the given rule to the given node.
     */
    protected abstract void visit(Rule rule, SimpleNode node, RuleContext ctx);

    /**
     * Index all nodes for visitation by rules.
     */
    protected abstract void indexNodes(List<CompilationUnit> astCompilationUnits, RuleContext ctx);

    /**
     * Index a single node for visitation by rules.
     */
    protected void indexNode(SimpleNode node) {
        List<SimpleNode> nodes = nodeNameToNodes.get(node.toString());
        if (nodes != null) {
            nodes.add(node);
        }
    }

    /**
     * Initialize the RuleChainVisitor to be ready to perform visitations. This
     * method should not be called until it is known that all Rules participating
     * in the RuleChain are ready to be initialized themselves.  Some rules
     * may require full initialization to determine if they will participate in
     * the RuleChain, so this has been delayed as long as possible to ensure
     * that manipulation of the Rules is no longer occurring.
     */
    protected void initialize() {
        if (nodeNameToNodes != null) {
            return;
        }

        // Determine all node types that need visiting
        Set<String> visitedNodes = new HashSet<String>();
        for (Iterator<Map.Entry<RuleSet, List<Rule>>> entryIterator = ruleSetRules.entrySet().iterator(); entryIterator.hasNext();) {
            Map.Entry<RuleSet, List<Rule>> entry = entryIterator.next();
            for (Iterator<Rule> ruleIterator = entry.getValue().iterator(); ruleIterator.hasNext();) {
                Rule rule = ruleIterator.next();
                if (rule.usesRuleChain()) {
                    visitedNodes.addAll(rule.getRuleChainVisits());
                }
                else {
                    // Drop rules which do not participate in the rule chain.
                    ruleIterator.remove();
                }
            }
            // Drop RuleSets in which all Rules have been dropped.
            if (entry.getValue().isEmpty()) {
        	entryIterator.remove();
            }
        }

        // Setup the data structure to manage mapping node names to node
        // instances.  We intend to reuse this data structure between
        // visits to different ASTs.
        nodeNameToNodes = new HashMap<String, List<SimpleNode>>();
        for (String s: visitedNodes) {
            List<SimpleNode> nodes = new ArrayList<SimpleNode>(100);
            nodeNameToNodes.put(s, nodes);
        }
    }

    /**
     * Clears the internal data structure used to manage the nodes visited
     * between visiting different ASTs.
     */
    protected void clear() {
        for (List<SimpleNode> l: nodeNameToNodes.values()) {
            l.clear();
        }
    }
}
