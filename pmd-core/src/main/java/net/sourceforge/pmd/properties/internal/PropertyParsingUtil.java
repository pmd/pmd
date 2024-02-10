/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import net.sourceforge.pmd.properties.ConstraintViolatedException;
import net.sourceforge.pmd.properties.PropertyConstraint;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.PropertySerializer;
import net.sourceforge.pmd.util.IteratorUtil;
import net.sourceforge.pmd.util.internal.xml.XmlUtil;

/**
 * This is internal API and shouldn't be used directly by clients.
 */
public final class PropertyParsingUtil {

    public static final ValueSyntax<String> STRING = ValueSyntax.withDefaultToString(String::trim);
    public static final ValueSyntax<Character> CHARACTER =
        ValueSyntax.partialFunction(
            c -> Character.toString(c),
            s -> s.charAt(0),
            PropertyConstraint.fromPredicate(
                s -> s.length() == 1,
                "Should be exactly one character in length"
            ));

    public static final ValueSyntax<Pattern> REGEX = ValueSyntax.withDefaultToString(Pattern::compile);
    public static final ValueSyntax<Integer> INTEGER = ValueSyntax.withDefaultToString(preTrim(Integer::valueOf));
    public static final ValueSyntax<Long> LONG = ValueSyntax.withDefaultToString(preTrim(Long::valueOf));
    public static final ValueSyntax<Boolean> BOOLEAN = ValueSyntax.withDefaultToString(preTrim(Boolean::valueOf));
    public static final ValueSyntax<Double> DOUBLE = ValueSyntax.withDefaultToString(preTrim(Double::valueOf));


    public static final PropertySerializer<List<Integer>> INTEGER_LIST = numberList(INTEGER);
    public static final PropertySerializer<List<Double>> DOUBLE_LIST = numberList(DOUBLE);
    public static final PropertySerializer<List<Long>> LONG_LIST = numberList(LONG);

    public static final PropertySerializer<List<Character>> CHAR_LIST = otherList(CHARACTER);
    public static final PropertySerializer<List<String>> STRING_LIST = otherList(STRING);

    private PropertyParsingUtil() {

    }


    private static <T extends Number> PropertySerializer<List<T>> numberList(ValueSyntax<T> valueSyntax) {
        return delimitedString(valueSyntax, Collectors.toList());
    }

    private static <T> PropertySerializer<List<T>> otherList(ValueSyntax<T> valueSyntax) {
        return delimitedString(valueSyntax, Collectors.toList() /* prefer old syntax for now */);
    }

    private static <T> Function<String, ? extends T> preTrim(Function<? super String, ? extends T> parser) {
        return parser.compose(String::trim);
    }

    public static <T> PropertySerializer<Optional<T>> toOptional(PropertySerializer<T> itemSyntax, String missingValue) {
        return ValueSyntax.create(
            opt -> opt.map(itemSyntax::toString).orElse(missingValue),
            str -> {
                if (str.equals(missingValue)) {
                    return Optional.empty();
                }
                return Optional.of(itemSyntax.fromString(str));
            }
        );
    }


    /**
     * Checks the result of the constraints defined by this mapper on
     * the given element. Returns all failures as a list of strings.
     */
    public static <T> void checkConstraintsThrow(T t, List<? extends PropertyConstraint<? super T>> constraints) {
        ConstraintViolatedException exception = null;
        for (PropertyConstraint<? super T> constraint : constraints) {
            try {
                constraint.validate(t);
            } catch (ConstraintViolatedException e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }

        if (exception != null) {
            throw exception;
        }
    }

    public static <T> PropertySerializer<T> withAllConstraints(PropertySerializer<T> mapper, List<PropertyConstraint<? super T>> constraints) {
        PropertySerializer<T> result = mapper;
        for (PropertyConstraint<? super T> constraint : constraints) {
            result = result.withConstraint(constraint);
        }

        return result;
    }

    /**
     * Builds an XML syntax that understands delimited {@code <value>} syntax.
     *
     * @param <T>        Type of items
     * @param <C>        Type of collection to handle
     * @param itemSyntax Serializer for the items, must support string mapping
     * @param collector  Collector to create the collection from strings
     *
     * @throws IllegalArgumentException If the item syntax doesn't support string mapping
     */
    public static <T, C extends Iterable<T>> PropertySerializer<C> delimitedString(PropertySerializer<T> itemSyntax,
                                                                                   Collector<? super T, ?, ? extends C> collector) {
        String delim = "" + PropertyFactory.DEFAULT_DELIMITER;
        return ValueSyntax.create(
            coll -> IteratorUtil.toStream(coll.iterator()).map(itemSyntax::toString).collect(Collectors.joining(delim)),
            string -> parseListWithEscapes(string, PropertyFactory.DEFAULT_DELIMITER, itemSyntax::fromString).stream().collect(collector)
        );
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
    public static <U> List<U> parseListWithEscapes(String str, char delimiter, Function<? super String, ? extends U> extractor) {
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
                result.add(extractor.apply(currentToken.toString()));
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
            result.add(extractor.apply(currentToken.toString()));
        }
        return result;
    }


    public static <T> ValueSyntax<T> enumerationParser(final Map<String, T> mappings, Function<? super T, String> reverseFun) {

        if (mappings.containsValue(null)) {
            throw new IllegalArgumentException("Map may not contain entries with null values");
        }

        return ValueSyntax.partialFunction(
            reverseFun,
            mappings::get,
            PropertyConstraint.fromPredicate(
                mappings::containsKey,
                "Should be " + XmlUtil.formatPossibleNames(XmlUtil.toConstants(mappings.keySet()))
            )
        );
    }
}
