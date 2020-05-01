/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.List;

import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Base class for the parser rule contexts, use {@code contextSuperClass} option
 * in the antlr grammar.
 */
public abstract class AntlrParseTreeBase implements RuleNode {

    AntlrNode<?> pmdNode;

    public RecognitionException exception;

    protected AntlrParseTreeBase() {
    }

    protected AntlrParseTreeBase(ParserRuleContext parent, int invokingStateNumber) {
        super(parent, invokingStateNumber);
    }


    @Override
    public RuleContext getRuleContext() {
        return null;
    }

    @Override
    public ParseTree getParent() {
        return null;
    }

    @Override
    public ParseTree getChild(int i) {
        return null;
    }

    @Override
    public void setParent(RuleContext parent) {

    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public String toStringTree(Parser parser) {
        return null;
    }

    @Override
    public Interval getSourceInterval() {
        return null;
    }

    @Override
    public Object getPayload() {
        return null;
    }

    @Override
    public int getChildCount() {
        return 0;
    }

    @Override
    public String toStringTree() {
        return null;
    }

    protected <T> T getRuleContext(Class<T> klass, int idx) {

    }
    protected <T> List<T> getRuleContexts(Class<T> klass) {

    }

    protected TerminalNode getToken(int kind, int idx) {

    }

    public abstract int getRuleIndex();


    public abstract void enterRule(ParseTreeListener listener);


    public abstract void exitRule(ParseTreeListener listener);


    public abstract <T> T accept(ParseTreeVisitor<? extends T> visitor);
}
