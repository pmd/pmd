/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import java.util.function.Function;

/**
 * Parses a value from a string.
 *
 * @param <U> The type of the value to parse
 */
@FunctionalInterface
public interface StringParser<U> extends Function<String, U> {

    /** An alias for {@link #valueOf(String)}. */
    @Override
    default U apply(String s) throws IllegalArgumentException {
        return valueOf(s);
    }


    /**
     * Extracts a primitive from a string.
     *
     * @param value The string to parse
     *
     * @return The primitive found
     * @throws IllegalArgumentException if the value couldn't be parsed
     */
    U valueOf(String value) throws IllegalArgumentException;

}
