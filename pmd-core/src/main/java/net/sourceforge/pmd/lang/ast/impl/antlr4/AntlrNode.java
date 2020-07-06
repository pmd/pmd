/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

/**
 * Base interface for all Antlr-based implementation of the Node interface.
 */
public interface AntlrNode<N extends AntlrNode<N>> extends GenericNode<N> {

}
