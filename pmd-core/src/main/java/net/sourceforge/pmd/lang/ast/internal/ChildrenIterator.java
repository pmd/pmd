/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;

class ChildrenIterator implements Iterator<@NonNull Node> {

    private final Node top;
    private int i;

    ChildrenIterator(Node top) {
        this.top = top;
    }

    @Override
    public boolean hasNext() {
        return i < top.jjtGetNumChildren();
    }


    @Override
    public @NonNull
    Node next() {
        return top.jjtGetChild(i++);
    }
}
