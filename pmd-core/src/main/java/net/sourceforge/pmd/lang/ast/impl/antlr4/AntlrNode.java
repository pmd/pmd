/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

/**
 *
 */
public interface AntlrNode<N extends GenericNode<N>> extends GenericNode<N> {

    <P, R> R acceptVisitor(AntlrTreeVisitor<P, R, ?> visitor, P data);
}
