/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.antlr;

import java.util.List;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.AntlrBaseNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public abstract class AbstractAntlrVisitor<T> extends AbstractRule implements ParseTreeVisitor<T> {

    protected RuleContext data;

    @Override
    public void start(RuleContext ctx) {
        data = ctx;
    }

    @Override
    public T visit(ParseTree tree) {
        return tree.accept(this);
    }

    @Override
    public T visitChildren(RuleNode node) {
        T result = this.defaultResult();
        int n = node.getChildCount();

        for (int i = 0; i < n && this.shouldVisitNextChild(node, result); ++i) {
            ParseTree c = node.getChild(i);
            T childResult = c.accept(this);
            result = this.aggregateResult(result, childResult);
        }

        return result;
    }

    @Override
    public T visitTerminal(TerminalNode node) {
        return this.defaultResult();
    }

    @Override
    public T visitErrorNode(ErrorNode node) {
        return this.defaultResult();
    }

    protected T defaultResult() {
        return null;
    }

    protected T aggregateResult(T aggregate, T nextResult) {
        return nextResult;
    }

    protected boolean shouldVisitNextChild(RuleNode node, T currentResult) {
        return true;
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        visitAll(nodes);
    }

    protected void visitAll(List<? extends Node> nodes) {
        for (Node n : nodes) {
            AntlrBaseNode node = (AntlrBaseNode) n;
            visit(node);
        }
    }

    public T visit(final AntlrBaseNode node) {
        return node.accept(this);
    }
}
