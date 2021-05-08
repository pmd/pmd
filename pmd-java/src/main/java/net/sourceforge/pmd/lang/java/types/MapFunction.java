/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;


import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * A partial function built on a map.
 */
abstract class MapFunction<T, R> implements Function<T, R> {

    private final Map<T, R> map;

    MapFunction(Map<T, R> map) {
        this.map = map;
    }

    protected Map<T, R> getMap() {
        return map;
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public String toString() {
        return map.entrySet().stream()
                  .map(it -> it.getKey() + " => " + it.getValue())
                  .collect(Collectors.joining("; ", getClass().getSimpleName() + "[", "]"));
    }
}
