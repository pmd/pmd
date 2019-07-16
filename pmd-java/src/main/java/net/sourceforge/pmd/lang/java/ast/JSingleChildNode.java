/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A node that has a single type of children, but can have no children.
 *
 * Package private, the methods are exposed nevertheless.
 *
 * @author Clément Fournier
 */
interface JSingleChildNode<T extends JavaNode> extends JavaNode {


    @Override
    @Nullable
    default T getLastChild() {
        return jjtGetNumChildren() > 0 ? jjtGetChild(jjtGetNumChildren() - 1) : null;

    }


    @Override
    @Nullable
    default T getFirstChild() {
        return jjtGetNumChildren() > 0 ? jjtGetChild(0) : null;
    }


    @Override
    T jjtGetChild(int index);
}
