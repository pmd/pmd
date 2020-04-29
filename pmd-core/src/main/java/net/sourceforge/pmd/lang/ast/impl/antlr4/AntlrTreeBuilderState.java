/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.ArrayDeque;
import java.util.Deque;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Don't extend me, compose me.
 */
public abstract class AntlrTreeBuilderState<B extends AbstractAntlrNode<B, ?, ?>> implements ParseTreeListener {

    private final Deque<B> stack = new ArrayDeque<>();
    private final Deque<Integer> marks = new ArrayDeque<>();

    private @Nullable B toPushNext;


    public void setNodeToPush(B nextNode) {
        assert toPushNext == null : "Already a node waiting";
        toPushNext = nextNode;
    }

    public B top() {
        return stack.getFirst();
    }

    @Override
    public void visitTerminal(TerminalNode node) {

    }

    @Override
    public void visitErrorNode(ErrorNode node) {

    }

    protected abstract B defaultNode(AntlrParseTreeBase parseTree);

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        AntlrParseTreeBase tree = (AntlrParseTreeBase) ctx;
        B toPush;
        if (toPushNext != null) {
            toPush = toPushNext;
            toPushNext = null;
        } else {
            toPush = defaultNode(tree);
        }
        marks.push(stack.size());
        stack.push(toPush);
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        B top = stack.pop();
        Integer mark = marks.pop();
        int arity = stack.size() - mark;
        while (arity-- > 0) {
            top.addChild(stack.pop(), arity);
        }
        stack.push(top);
    }
}
