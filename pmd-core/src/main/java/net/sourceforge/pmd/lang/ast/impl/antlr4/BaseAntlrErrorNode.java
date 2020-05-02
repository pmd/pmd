/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

public abstract class BaseAntlrErrorNode<N extends GenericNode<N>> extends BaseAntlrTerminalNode<N> {

    protected BaseAntlrErrorNode(Token symbol) {
        super(symbol, true);
    }

    @Override
    protected final AntlrErrorPmdAdapter<N> asAntlrNode() {
        return (AntlrErrorPmdAdapter<N>) super.asAntlrNode();
    }


    @Override
    public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
        return visitor.visitErrorNode(asAntlrNode());
    }


    @Override
    public @NonNull String getText() {
        return getFirstAntlrToken().getText();
    }

    @Override
    public final String getXPathNodeName() {
        return "Error";
    }
}
