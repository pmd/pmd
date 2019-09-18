/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.InternalApi;


@InternalApi
public final class XmlSyntaxUtils {

    public static final ValueSyntax<String> STRING = new ValueSyntax<>(Function.identity());
    public static final ValueSyntax<Character> CHARACTER = new ValueSyntax<>(value -> {
        if (value == null || value.length() != 1) {
            throw new IllegalArgumentException("missing/ambiguous character value for string \"" + value + "\"");
        }
        return value.charAt(0);
    });

    public static final ValueSyntax<Pattern> REGEX = new ValueSyntax<>(Pattern::compile);
    public static final ValueSyntax<Integer> INTEGER = new ValueSyntax<>(Integer::valueOf);
    public static final ValueSyntax<Long> LONG = new ValueSyntax<>(Long::valueOf);
    public static final ValueSyntax<Boolean> BOOLEAN = new ValueSyntax<>(Boolean::valueOf);
    public static final ValueSyntax<Double> DOUBLE = new ValueSyntax<>(Double::valueOf);


    public static final XmlSyntax<List<Integer>> INTEGER_LIST = numberList(INTEGER);
    public static final XmlSyntax<List<Double>> DOUBLE_LIST = numberList(DOUBLE);
    public static final XmlSyntax<List<Long>> LONG_LIST = numberList(LONG);

    public static final XmlSyntax<List<Character>> CHAR_LIST = otherList(CHARACTER);
    public static final XmlSyntax<List<String>> STRING_LIST = otherList(STRING);

    public static final XmlSyntax<List<Pattern>> REGEX_LIST = new SeqSyntax<>(REGEX, ArrayList::new);


    private XmlSyntaxUtils() {

    }


    private static <T extends Number> XmlSyntax<List<T>> numberList(ValueSyntax<T> valueSyntax) {
        return seqAndDelimited(valueSyntax, ArrayList::new, true, ',');
    }

    private static <T> XmlSyntax<List<T>> otherList(ValueSyntax<T> valueSyntax) {
        return seqAndDelimited(valueSyntax, ArrayList::new, true /* for now */, '|');
    }

    public static <T> XmlSyntax<Optional<T>> toOptional(XmlSyntax<T> itemSyntax) {
        return new OptionalSyntax<>(itemSyntax);
    }

    /**
     * Builds an XML syntax that understands a {@code <seq>} syntax and
     * a delimited {@code <value>} syntax.
     *
     * @param itemSyntax        Serializer for the items, must support string mapping
     * @param emptyCollSupplier Supplier for the collection
     * @param preferOldSyntax   If true, the property will be written with {@code <value>},
     *                          otherwise with {@code <seq>}.
     * @param delimiter         Delimiter for the {@code <value>} syntax
     * @param <T>               Type of items
     * @param <C>               Type of collection to handle
     *
     * @throws IllegalArgumentException If the item syntax doesn't support string mapping
     */
    public static <T, C extends Collection<T>> XmlSyntax<C> seqAndDelimited(XmlSyntax<T> itemSyntax,
                                                                            Supplier<C> emptyCollSupplier,
                                                                            boolean preferOldSyntax,
                                                                            char delimiter) {
        if (!itemSyntax.supportsStringMapping()) {
            throw new IllegalArgumentException("Item syntax does not support string mapping " + itemSyntax);
        }
        return new SyntaxSet<>(
            new SeqSyntax<>(itemSyntax, emptyCollSupplier),
            delimitedString(itemSyntax::toString, itemSyntax::fromString, delimiter, emptyCollSupplier),
            preferOldSyntax
        );
    }

    public static <T, C extends Collection<T>> XmlSyntax<C> onlySeq(XmlSyntax<T> itemSyntax,
                                                                    Supplier<C> emptyCollSupplier) {
        return new SeqSyntax<>(itemSyntax, emptyCollSupplier);
    }


    private static <T, C extends Collection<T>> ValueSyntax<C> delimitedString(
        Function<? super T, String> toString,
        Function<String, ? extends T> fromString,
        char delimiter,
        Supplier<C> emptyCollSupplier
    ) {
        String delim = "" + delimiter;
        return new ValueSyntax<>(
            coll -> coll.stream().map(toString).collect(Collectors.joining(delim)),
            string -> {
                C coll = emptyCollSupplier.get();
                coll.addAll(parseListWithEscapes(string, delimiter, fromString));
                return coll;
            }
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


    static String enquote(String it) {
        return "'" + it + "'";
    }


    // nullable
    static String formatPossibilities(Set<String> names) {
        if (names.isEmpty()) {
            return null;
        } else if (names.size() == 1) {
            return enquote(names.iterator().next());
        } else {
            return "one of " + names.stream().map(XmlSyntaxUtils::enquote).collect(Collectors.joining(", "));
        }
    }

    public static <T> ValueSyntax<T> enumerationParser(final Map<String, T> mappings) {

        if (mappings.containsValue(null)) {
            throw new IllegalArgumentException("Map may not contain entries with null values");
        }

        return new ValueSyntax<>(value -> {
            if (!mappings.containsKey(value)) {
                throw new IllegalArgumentException("Value was not in the set " + mappings.keySet());
            }
            return mappings.get(value);
        });
    }
}
