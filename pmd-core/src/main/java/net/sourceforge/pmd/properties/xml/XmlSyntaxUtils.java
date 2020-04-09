/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;


import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.internal.util.PredicateUtil;
import net.sourceforge.pmd.properties.constraints.PropertyConstraint;

/**
 * This is internal API and shouldn't be used directly by clients.
 */
@InternalApi
public final class XmlSyntaxUtils {

    public static final ValueSyntax<String> STRING = ValueSyntax.createNonDelimited(Function.identity());
    public static final ValueSyntax<Character> CHARACTER = ValueSyntax.createNonDelimited(value -> {
        if (value == null || value.length() != 1) {
            throw new IllegalArgumentException("missing/ambiguous character value for string \"" + value + "\"");
        }
        return value.charAt(0);
    });

    public static final ValueSyntax<Pattern> REGEX = ValueSyntax.createNonDelimited(Pattern::compile);
    public static final ValueSyntax<Integer> INTEGER = ValueSyntax.createNonDelimited(Integer::valueOf);
    public static final ValueSyntax<Long> LONG = ValueSyntax.createNonDelimited(Long::valueOf);
    public static final ValueSyntax<Boolean> BOOLEAN = ValueSyntax.createNonDelimited(Boolean::valueOf);
    public static final ValueSyntax<Double> DOUBLE = ValueSyntax.createNonDelimited(Double::valueOf);


    public static final XmlMapper<List<Integer>> INTEGER_LIST = numberList(INTEGER);
    public static final XmlMapper<List<Double>> DOUBLE_LIST = numberList(DOUBLE);
    public static final XmlMapper<List<Long>> LONG_LIST = numberList(LONG);

    public static final XmlMapper<List<Character>> CHAR_LIST = otherList(CHARACTER);
    public static final XmlMapper<List<String>> STRING_LIST = otherList(STRING);

    private XmlSyntaxUtils() {

    }


    private static <T extends Number> XmlMapper<List<T>> numberList(ValueSyntax<T> valueSyntax) {
        return seqAndDelimited(valueSyntax, Collectors.toList(), true, ',');
    }

    private static <T> XmlMapper<List<T>> otherList(ValueSyntax<T> valueSyntax) {
        return seqAndDelimited(valueSyntax, Collectors.toList(), true /* for now */, '|');
    }

    public static <T> XmlMapper<Optional<T>> toOptional(XmlMapper<T> itemSyntax) {
        return new OptionalSyntax<>(itemSyntax);
    }

    public static <T> XmlMapper<T> withAllConstraints(XmlMapper<T> mapper, List<PropertyConstraint<? super T>> constraints) {
        XmlMapper<T> result = mapper;
        for (PropertyConstraint<? super T> constraint : constraints) {
            result = result.withConstraint(constraint);
        }

        return result;
    }

    /**
     * Builds an XML syntax that understands a {@code <seq>} syntax and
     * a delimited {@code <value>} syntax.
     *
     * @param itemSyntax      Serializer for the items, must support string mapping
     * @param collector       Collector to create the collection from strings
     * @param preferOldSyntax If true, the property will be written with {@code <value>},
     *                        otherwise with {@code <seq>}.
     * @param delimiter       Delimiter for the {@code <value>} syntax
     * @param <T>             Type of items
     * @param <C>             Type of collection to handle
     *
     * @throws IllegalArgumentException If the item syntax doesn't support string mapping
     */
    public static <T, C extends Iterable<T>> XmlMapper<C> seqAndDelimited(XmlMapper<T> itemSyntax,
                                                                          Collector<? super T, ?, ? extends C> collector,
                                                                          boolean preferOldSyntax,
                                                                          char delimiter) {
        if (!itemSyntax.supportsStringMapping()) {
            throw new IllegalArgumentException("Item syntax does not support string mapping " + itemSyntax);
        }
        return new MapperSet<>(
            new SeqSyntax<>(itemSyntax, collector),
            delimitedString(itemSyntax::toString, itemSyntax::fromString, delimiter, collector),
            preferOldSyntax
        );
    }

    public static <T, C extends Iterable<T>> XmlMapper<C> onlySeq(XmlMapper<T> itemSyntax,
                                                                  Collector<? super T, ?, ? extends C> collector) {
        return new SeqSyntax<>(itemSyntax, collector);
    }


    private static <T, C extends Iterable<T>> ValueSyntax<C> delimitedString(
        Function<? super T, String> toString,
        Function<String, ? extends T> fromString,
        char delimiter,
        Collector<? super T, ?, ? extends C> collector
    ) {
        String delim = "" + delimiter;
        return ValueSyntax.createDelimited(
            coll -> IteratorUtil.stream(coll.iterator()).map(toString).collect(Collectors.joining(delim)),
            string -> parseListWithEscapes(string, delimiter, fromString).stream().collect(collector)
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

        PropertyConstraint<T> constraint =
            PropertyConstraint.fromPredicate(PredicateUtil.always(), "Should be in set " + mappings.keySet());

        return new ValueSyntax<T>(
            reverseFun,
            value -> {
                if (!mappings.containsKey(value)) {
                    throw new IllegalArgumentException("Value is not in the set " + mappings.keySet());
                }
                return mappings.get(value);
            },
            false
        ) {
            @Override
            public List<PropertyConstraint<? super T>> getConstraints() {
                return listOf(constraint);
            }
        };

    }
}
