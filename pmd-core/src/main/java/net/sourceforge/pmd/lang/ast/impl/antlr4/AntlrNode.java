/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.tree.ParseTree;

import net.sourceforge.pmd.lang.ast.TextAvailableNode;

/**
 * Base interface for all Antlr-based implementation of Node interface.
 * <p>
 * Initially all the methods implemented here will be no-op due to scope limitations
 */
public interface AntlrNode extends ParseTree, TextAvailableNode {


    @Override
    AntlrNode getChild(int index);


    @Override
    AntlrNode getParent();
}
