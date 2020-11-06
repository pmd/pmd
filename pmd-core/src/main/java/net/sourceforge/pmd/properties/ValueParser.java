/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Parses a value from a string.
 *
 * @param <U> The type of the value to parse
 */
// FUTURE @FunctionalInterface
@Deprecated
@InternalApi
public interface ValueParser<U> {

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
