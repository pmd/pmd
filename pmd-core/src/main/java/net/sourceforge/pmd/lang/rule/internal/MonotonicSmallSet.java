/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Set that can only grow, optimised for case empty/singleton.
 */
public class MonotonicSmallSet<T> implements Set<T> {

    private Set<T> mySet;
    private SetState state;

    public MonotonicSmallSet() {
        mySet = Collections.emptySet();
        state = SetState.EMPTY;
    }

    public MonotonicSmallSet(MonotonicSmallSet<T> other) {
        this.mySet = other.state.copy(other.mySet);
        this.state = other.state;
    }

    @Override
    public int size() {
        return mySet.size();
    }

    @Override
    public boolean isEmpty() {
        return mySet.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return mySet.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return mySet.iterator();
    }

    @Override
    public Object[] toArray() {
        return mySet.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return mySet.toArray(a);
    }

    @Override
    public boolean add(T t) {
        return state.add(this, t);
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return mySet.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        boolean added = false;
        for (T t : c) {
            added |= add(t);
        }
        return added;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    private enum SetState {
        MORE {
            @Override
            <T> boolean add(MonotonicSmallSet<T> base, T t) {
                return base.mySet.add(t);
            }

            @Override
            <T> Set<T> copy(Set<T> from) {
                return new HashSet<>(from);
            }
        },
        SINGLE {
            @Override
            <T> boolean add(MonotonicSmallSet<T> base, T t) {
                if (base.mySet.contains(t)) {
                    return false;
                }
                base.mySet = new HashSet<>(base.mySet);
                base.mySet.add(t);
                base.state = MORE;
                return true;
            }
        },
        EMPTY {
            @Override
            <T> boolean add(MonotonicSmallSet<T> base, T t) {
                base.mySet = Collections.singleton(t);
                base.state = SINGLE;
                return true;
            }
        },
        ;

        abstract <T> boolean add(MonotonicSmallSet<T> base, T t);


        <T> Set<T> copy(Set<T> from) {
            return from;
        }

    }
}
