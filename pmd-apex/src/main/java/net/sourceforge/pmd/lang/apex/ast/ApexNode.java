/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * Root interface implemented by all Apex nodes. Apex nodes wrap a tree
 * obtained from an external parser.
 *
 * @param <T> placeholder
 */
public interface ApexNode<T> extends Node {

    /**
     * Accept the visitor.
     */
    Object jjtAccept(ApexParserVisitor visitor, Object data);


    /**
     * Accept the visitor. *
     *
     * @deprecated This method is not useful, the logic for combining
     *     children values should be present on the visitor, not the node
     */
    @Deprecated
    Object childrenAccept(ApexParserVisitor visitor, Object data);


    @Override
    Iterable<? extends ApexNode<?>> children();


    @Override
    ApexNode<?> getChild(int index);


    @Override
    ApexNode<?> getParent();

    boolean hasRealLoc();

    String getDefiningType();

    String getNamespace();
}
