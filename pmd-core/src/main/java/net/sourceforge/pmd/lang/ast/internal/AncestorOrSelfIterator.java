/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;

/** Iterates over a node and its descendants. */
public class AncestorOrSelfIterator implements Iterator<@NonNull Node> {


    private Node next;

    public AncestorOrSelfIterator(Node top) {
        next = top;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }


    @Override
    public Node next() {
        Node n = next;
        if (n == null) {
            throw new NoSuchElementException();
        }
        next = n.jjtGetParent();
        return n;
    }
}
