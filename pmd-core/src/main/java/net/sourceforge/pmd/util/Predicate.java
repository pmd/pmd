/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

/**
 * Simple predicate of one argument.
 *
 * @param <T> the type of the input to the predicate
 */
// TODO java8 - replace with java.util.function.Predicate
public interface Predicate<T> {

    boolean test(T t);
}
