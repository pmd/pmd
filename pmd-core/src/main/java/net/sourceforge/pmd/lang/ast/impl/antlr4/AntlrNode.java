/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.tree.ParseTree;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * Base interface for all Antlr-based implementation of Node interface.
 * <p>
 * Initially all the methods implemented here will be no-op due to scope limitations
 */
public interface AntlrNode extends Node, ParseTree {


    @Override
    AntlrNode getChild(int index);


    @Override
    AntlrNode getParent();


    @Override
    @SuppressWarnings("unchecked")
    default NodeStream<? extends AntlrNode> children() {
        return (NodeStream<? extends AntlrNode>) Node.super.children();
    }


}
