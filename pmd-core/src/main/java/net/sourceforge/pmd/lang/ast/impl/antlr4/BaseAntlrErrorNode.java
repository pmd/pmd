/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Token;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class BaseAntlrErrorNode<N extends AntlrNode<N>> extends BaseAntlrTerminalNode<N> {

    protected BaseAntlrErrorNode(Token symbol) {
        super(symbol, true);
    }

    @Override
    protected final AntlrErrorPmdAdapter<N> asAntlrNode() {
        return (AntlrErrorPmdAdapter<N>) super.asAntlrNode();
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
