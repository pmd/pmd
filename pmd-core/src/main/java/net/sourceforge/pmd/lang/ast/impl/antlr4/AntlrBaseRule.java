/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.List;

import org.antlr.v4.runtime.tree.AbstractParseTreeVisitor;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;

/**
 * Base implementation of an antlr rule.
 */
public abstract class AntlrBaseRule extends AbstractRule {

    protected AntlrBaseRule() {
        // inheritance constructor
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        AbstractParseTreeVisitor<Void> visitor = buildVisitor(ctx);
        assert visitor != null : "Rule should provide a non-null visitor";

        for (Node node : nodes) {
            assert node instanceof AntlrBaseInnerNode : "Incorrect node type " + node + " passed to " + this;

            ((AntlrBaseInnerNode) node).accept(visitor);
        }
    }

    /**
     * Returns a rule visitor that can visit nodes for the given rule context.
     * This visitor should explore the nodes it's interested in and report
     * violations on the given rule context.
     *
     * @param ruleCtx Object that accumulates rule violations
     *
     * @return A visitor bound to the given rule context
     */
    public abstract AbstractParseTreeVisitor<Void> buildVisitor(RuleContext ruleCtx);

}
