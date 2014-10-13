/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * The RuleChainVisitor understands how to visit an AST for a particular
 * Language.
 */
public interface RuleChainVisitor {
    /**
     * Add the given rule to the visitor.
     * 
     * @param ruleSet The RuleSet to which the rule belongs.
     * @param rule The rule to add.
     */
    void add(RuleSet ruleSet, Rule rule);

    /**
     * Visit all the given Nodes provided using the given RuleContext. Every
     * Rule added will visit the AST as appropriate.
     * 
     * @param nodes The Nodes to visit.
     * @param ctx The RuleContext.
     */
    void visitAll(List<Node> nodes, RuleContext ctx);
}
