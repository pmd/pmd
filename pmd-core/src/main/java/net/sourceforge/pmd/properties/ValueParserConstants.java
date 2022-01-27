/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * This class will be completely scrapped with 7.0.0. It only hid away the syntactic
 * overhead caused by the lack of lambdas in Java 7.
 *
 * @author Clément Fournier
 * @since 6.0.0
 * @deprecated Was internal API
 */
@Deprecated
@InternalApi
public final class ValueParserConstants {


    /** Extracts characters. */
    static final ValueParser<Character> CHARACTER_PARSER = new ValueParser<Character>() {
        @Override
        public Character valueOf(String value) {
            if (value == null || value.length() != 1) {
                throw new IllegalArgumentException("missing/ambiguous character value");
            }
            return value.charAt(0);
        }
    };
    /** Extracts strings. That's a dummy used to return a list in StringMultiProperty. */
    static final ValueParser<String> STRING_PARSER = new ValueParser<String>() {
        @Override
        public String valueOf(String value) {
            return StringUtils.trim(value);
        }
    };
    /** Extracts integers. */
    static final ValueParser<Integer> INTEGER_PARSER = new ValueParser<Integer>() {
        @Override
        public Integer valueOf(String value) {
            return Integer.valueOf(StringUtils.trim(value));
        }
    };
    /** Extracts booleans. */
    static final ValueParser<Boolean> BOOLEAN_PARSER = new ValueParser<Boolean>() {
        @Override
        public Boolean valueOf(String value) {
            return Boolean.valueOf(StringUtils.trim(value));
        }
    };
    /** Extracts floats. */
    static final ValueParser<Float> FLOAT_PARSER = new ValueParser<Float>() {
        @Override
        public Float valueOf(String value) {
            return Float.valueOf(value);
        }
    };
    /** Extracts longs. */
    static final ValueParser<Long> LONG_PARSER = new ValueParser<Long>() {
        @Override
        public Long valueOf(String value) {
            return Long.valueOf(StringUtils.trim(value));
        }
    };
    /** Extracts doubles. */
    static final ValueParser<Double> DOUBLE_PARSER = new ValueParser<Double>() {
        @Override
        public Double valueOf(String value) {
            return Double.valueOf(value);
        }
    };
    /** Extracts files */
    static final ValueParser<File> FILE_PARSER = new ValueParser<File>() {
        @Override
        public File valueOf(String value) throws IllegalArgumentException {
            return new File(StringUtils.trim(value));
        }
    };

    /** Compiles a regex. */
    static final ValueParser<Pattern> REGEX_PARSER = new ValueParser<Pattern>() {
        @Override
        public Pattern valueOf(String value) throws IllegalArgumentException {
            return Pattern.compile(value);
        }
    };


    private ValueParserConstants() {

    }


    static <T> ValueParser<T> enumerationParser(final Map<String, T> mappings) {

        if (mappings.containsValue(null)) {
            throw new IllegalArgumentException("Map may not contain entries with null values");
        }

        return new ValueParser<T>() {
            @Override
            public T valueOf(String value) throws IllegalArgumentException {
                String trimmedValue = StringUtils.trim(value);
                if (!mappings.containsKey(trimmedValue)) {
                    throw new IllegalArgumentException("Value " + value + " was not in the set " + mappings.keySet());
                }
                return mappings.get(trimmedValue);
            }
        };
    }


    /**
     * Returns a value parser parsing lists of values of type U.
     *
     * @param parser    Parser used to parse a single value
     * @param delimiter Char delimiting values
     * @param <U>       Element type of the target list
     *
     * @return A list of values
     */
    public static <U> ValueParser<List<U>> multi(final ValueParser<U> parser, final char delimiter) {
        return new ValueParser<List<U>>() {
            @Override
            public List<U> valueOf(String value) throws IllegalArgumentException {
                return parsePrimitives(value, delimiter, parser);
            }
        };
    }


    /**
     * Parses a string into a list of values of type {@literal <U>}.
     *
     * @param toParse   The string to parse
     * @param delimiter The delimiter to use
     * @param extractor The function mapping a string to an instance of {@code <U>}
     * @param <U>       The type of the values to parse
     *
     * @return A list of values
     */
    // FUTURE 1.8 : use java.util.function.Function<String, U> in place of ValueParser<U>,
    // replace ValueParser constants with static functions
    static <U> List<U> parsePrimitives(String toParse, char delimiter, ValueParser<U> extractor) {
        String[] values = StringUtils.split(toParse, delimiter);
        List<U> result = new ArrayList<>();
        for (String s : values) {
            result.add(extractor.valueOf(s));
        }
        return result;
    }

    private static final char ESCAPE_CHAR = '\\';

    /**
     * Parse a list delimited with the given delimiter, converting individual
     * values to type {@code <U>} with the given extractor. Any character is
     * escaped with a backslash. This is useful to escape the delimiter, and
     * to escape the backslash. For example:
     * <pre>{@code
     *
     * "a,c"  -> [ "a", "c" ]
     * "a\,c" -> [ "a,c" ]
     * "a\c"  -> [ "ac" ]
     * "a\\c" -> [ "a\c" ]
     * "a\"   -> [ "a\"  ]   (a backslash at the end of the string is just a backslash)
     *
     * }</pre>
     */
    static <U> List<U> parseListWithEscapes(String str, char delimiter, ValueParser<U> extractor) {
        if (str.isEmpty()) {
            return Collections.emptyList();
        }

        List<U> result = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inEscapeMode = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (inEscapeMode) {
                inEscapeMode = false;
                currentToken.append(c);
            } else if (c == delimiter) {
                result.add(extractor.valueOf(currentToken.toString()));
                currentToken = new StringBuilder();
            } else if (c == ESCAPE_CHAR && i < str.length() - 1) {
                // this is ordered this way so that if the delimiter is
                // itself a backslash, no escapes are processed.
                inEscapeMode = true;
            } else {
                currentToken.append(c);
            }
        }

        if (currentToken.length() > 0) {
            result.add(extractor.valueOf(currentToken.toString()));
        }
        return result;
    }

}
