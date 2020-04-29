/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.ArrayDeque;
import java.util.Deque;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Don't extend me, compose me.
 */
public class AntlrTreeBuilderState<B extends AbstractAntlrNode<B, ?, ?>> implements ParseTreeListener {


    private final ParseTreeVisitor<B> nodeFactory;

    private final Deque<B> stack = new ArrayDeque<>();
    private final Deque<Integer> marks = new ArrayDeque<>();
    private int sp = 0;
    private int mk = 0;

    public AntlrTreeBuilderState(ParseTreeVisitor<B> nodeFactory) {
        this.nodeFactory = nodeFactory;
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

    @Override
    public void enterEveryRule(ParserRuleContext ctx) {
        marks.push(mk);
        mk = sp;
    }

    @Override
    public void exitEveryRule(ParserRuleContext ctx) {
        B newNode = nodeFactory.visit(ctx);
        ((AntlrParseTreeBase) ctx).pmdNode = newNode;

        int arity = nodeArity();
        mk = marks.pop();
        while (arity-- > 0) {
            newNode.addChild(popNode(), arity);
        }
        pushNode(newNode);
    }

    public int nodeArity() {
        return sp - mk;
    }


    private void pushNode(B toPush) {
        stack.push(toPush);
        sp++;
    }

    public B popNode() {
        --sp;
        if (sp < mk) {
            mk = marks.pop();
        }
        return stack.pop();
    }
}
