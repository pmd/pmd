/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

final class ConsList<T> extends AbstractList<T> {

    private final List<? extends T> head;
    private final List<? extends T> tail;
    private final int size;

    ConsList(List<? extends T> head, List<? extends T> tail) {
        this.head = head;
        this.tail = tail;
        this.size = head.size() + tail.size();
    }

    @Override
    public T get(int index) {
        Validate.validIndex(this, index);
        if (index < head.size()) {
            return head.get(index);
        }
        return tail.get(index - head.size());
    }

    @Override
    public Iterator<T> iterator() {
        return IteratorUtil.concat(head.iterator(), tail.iterator());
    }

    @Override
    public int size() {
        return size;
    }
}
