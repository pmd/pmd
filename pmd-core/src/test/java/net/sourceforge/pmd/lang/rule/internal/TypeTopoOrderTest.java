/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.io.Closeable;
import java.io.InputStream;
import java.io.Serializable;

import net.sourceforge.pmd.Rule;

/**
 * @author Clément Fournier
 */
public class TypeTopoOrderTest extends ComparatorTest<Class<?>> {

    @Override
    Iterable<? extends Class<?>> generator() {
        return listOf(
            Object.class,
            String.class,
            Monoid.class,
            Rule.class,
            InputStream.class,
            Closeable.class,
            AutoCloseable.class,
            Comparable.class,
            Serializable.class,
            Integer.class,
            Long.class,
            Double.class,
            Boolean.class,
            Number.class
        );
    }

    @Override
    int compare(Class<?> a, Class<?> b) {
        return TopoOrder.TYPE_HIERARCHY_ORDERING.compare(a, b);
    }

    @Override
    String toString(Class<?> aClass) {
        return aClass.getSimpleName();
    }
}
