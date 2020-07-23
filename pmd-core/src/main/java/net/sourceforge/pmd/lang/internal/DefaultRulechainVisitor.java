/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.internal;

import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.RuleChainVisitor;

/**
 * @deprecated See {@link RuleChainVisitor}
 */
@Deprecated
@InternalApi
public class DefaultRulechainVisitor extends AbstractRuleChainVisitor {

    @Override
    protected void visit(Rule rule, Node node, RuleContext ctx) {
        rule.apply(Collections.singletonList(node), ctx);
    }

    @Override
    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        for (Node node : nodes) {
            indexNodeRec(node);
        }
    }

    protected void indexNodeRec(Node top) {
        indexNode(top);
        for (Node child : top.children()) {
            indexNodeRec(child);
        }
    }
}
