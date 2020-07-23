/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class StreamUtils {

    private StreamUtils() {

    }

    public static <T> Stream<T> streamOf(Collection<T> c) {
        return c.stream();
    }

    @SafeVarargs
    public static <T> Stream<T> streamOf(T... c) {
        return Stream.of(c);
    }

    public static <T> Stream<T> streamOf(T c) {
        return Stream.of(c);
    }

    public static <T> List<T> toList(Stream<T> c) {
        return c.collect(Collectors.toList());
    }

    public static <T> Stream<T> streamOf(Iterator<T> c) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(c, 0), false);
    }

}
