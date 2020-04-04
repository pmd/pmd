/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.AbstractList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.Validate;

import net.sourceforge.pmd.internal.util.IteratorUtil;

final class ConsList<T> extends AbstractList<T> {

    private final List<T> head;
    private final List<T> tail;
    private final int size;

    ConsList(List<T> head, List<T> tail) {
        this.head = head;
        this.tail = tail;
        size = head.size() + tail.size();
    }

    ConsList(T head, List<T> tail) {
        this(Collections.singletonList(head), tail);
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

    static <T> List<T> cons(List<T> head, List<T> tail) {
        if (head.isEmpty()) {
            return tail;
        } else if (tail.isEmpty()) {
            return head;
        }
        return new ConsList<>(head, tail);
    }
}
