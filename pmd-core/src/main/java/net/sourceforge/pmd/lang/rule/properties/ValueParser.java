/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.properties;

/**
 * Parses a value from a string.
 *
 * @param <U> The type of the value to parse
 */
// FUTURE @FunctionalInterface
public interface ValueParser<U> {


    /** Extracts characters. */
    ValueParser<Character> CHARACTER_PARSER = new ValueParser<Character>() {
        @Override
        public Character valueOf(String value) {
            if (value == null || value.length() != 1) {
                throw new IllegalArgumentException("missing/ambiguous character value");
            }
            return value.charAt(0);
        }
    };

    // FUTURE Integer::valueOf
    /** Extracts integers. */
    ValueParser<Integer> INTEGER_PARSER = new ValueParser<Integer>() {
        @Override
        public Integer valueOf(String value) {
            return Integer.valueOf(value);
        }
    };

    // FUTURE Boolean::valueOf
    /** Extracts booleans. */
    ValueParser<Boolean> BOOLEAN_PARSER = new ValueParser<Boolean>() {
        @Override
        public Boolean valueOf(String value) {
            return Boolean.valueOf(value);
        }
    };

    // FUTURE Float::valueOf
    /** Extracts floats. */
    ValueParser<Float> FLOAT_PARSER = new ValueParser<Float>() {
        @Override
        public Float valueOf(String value) {
            return Float.valueOf(value);
        }
    };

    // FUTURE Long::valueOf
    /** Extracts longs. */
    ValueParser<Long> LONG_PARSER = new ValueParser<Long>() {
        @Override
        public Long valueOf(String value) {
            return Long.valueOf(value);
        }
    };

    // FUTURE Double::valueOf
    /** Extracts doubles. */
    ValueParser<Double> DOUBLE_PARSER = new ValueParser<Double>() {
        @Override
        public Double valueOf(String value) {
            return Double.valueOf(value);
        }
    };


    /**
     * Extracts a primitive from a string.
     *
     * @param value The string to parse
     *
     * @return The primitive found
     *
     * @throws IllegalArgumentException if the value couldn't be parsed
     */
    U valueOf(String value) throws IllegalArgumentException;

}
