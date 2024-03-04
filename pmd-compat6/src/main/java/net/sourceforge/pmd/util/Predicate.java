/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This class has been taken from 7.0.0-SNAPSHOT

package net.sourceforge.pmd.util;

/**
 * Simple predicate of one argument.
 *
 * @param <T> the type of the input to the predicate
 */
//@Experimental
public interface Predicate<T> {

    boolean test(T t);
}
