/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import net.sourceforge.pmd.lang.ast.RootNode;

/**
 * A base root node for Antlr implementations. Such a node wraps the
 * top level context produced by the parser.
 *
 * @param <T> Type of the toplevel context
 */
public class AntlrFileNode<T extends AntlrBaseNode> extends AntlrBaseNode implements RootNode {

    private final T toplevel;
    private final String xpathName;

    public AntlrFileNode(T toplevel, String langName) {
        this.toplevel = toplevel;
        super.start = toplevel.start;
        super.stop = toplevel.stop;
        assert start != null && stop != null : "Coordinates not set: " + toplevel;
        xpathName = langName + "File";
    }

    @Override
    public AntlrBaseNode getParent() {
        return null;
    }

    @Override
    public T getChild(int index) {
        return index != 0 ? null : toplevel;
    }

    @Override
    public int getNumChildren() {
        return 1;
    }

    @Override
    public int getIndexInParent() {
        return -1;
    }

    @Override
    public String getXPathNodeName() {
        return xpathName;
    }
}
