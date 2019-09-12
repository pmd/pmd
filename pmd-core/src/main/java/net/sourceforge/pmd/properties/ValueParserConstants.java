/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.properties.internal.ValueParser;


/**
 * This class will be completely scrapped with 7.0.0. It only hid away the syntactic
 * overhead caused by the lack of lambdas in Java 7.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class ValueParserConstants {


    /** Extracts characters. */
    static final ValueParser<Character> CHARACTER_PARSER = value -> {
        if (value == null || value.length() != 1) {
            throw new IllegalArgumentException("missing/ambiguous character value for string \"" + value + "\"");
        }
        return value.charAt(0);
    };
    /** Extracts strings. That's a dummy used to return a list in StringMultiProperty. */
    static final ValueParser<String> STRING_PARSER = value -> value;
    /** Extracts integers. */
    static final ValueParser<Integer> INTEGER_PARSER = Integer::valueOf;
    /** Extracts booleans. */
    static final ValueParser<Boolean> BOOLEAN_PARSER = Boolean::valueOf;
    /** Extracts floats. */
    static final ValueParser<Float> FLOAT_PARSER = Float::valueOf;
    /** Extracts longs. */
    static final ValueParser<Long> LONG_PARSER = Long::valueOf;
    /** Extracts doubles. */
    static final ValueParser<Double> DOUBLE_PARSER = Double::valueOf;
    /** Extracts files */
    static final ValueParser<File> FILE_PARSER = File::new;
    /** Compiles a regex. */
    static final ValueParser<Pattern> REGEX_PARSER = Pattern::compile;


    private ValueParserConstants() {

    }


    static <T> ValueParser<T> enumerationParser(final Map<String, T> mappings) {

        if (mappings.containsValue(null)) {
            throw new IllegalArgumentException("Map may not contain entries with null values");
        }

        return value -> {
            if (!mappings.containsKey(value)) {
                throw new IllegalArgumentException("Value was not in the set " + mappings.keySet());
            }
            return mappings.get(value);
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
        return value -> parsePrimitives(value, delimiter, parser);
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
    private static <U> List<U> parsePrimitives(String toParse, char delimiter, Function<? super String, ? extends U> extractor) {
        String[] values = StringUtils.split(toParse, delimiter);
        List<U> result = new ArrayList<>();
        for (String s : values) {
            result.add(extractor.apply(s));
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
