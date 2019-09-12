/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    public static final XmlSyntax<List<Boolean>> BOOLEAN_LIST = otherList(BOOLEAN);

    public static final XmlSyntax<List<Pattern>> REGEX_LIST = new SeqSyntax<>(REGEX, ArrayList::new);


    private XmlSyntaxUtils() {

    }


    private static <T extends Number> XmlSyntax<List<T>> numberList(ValueSyntax<T> valueSyntax) {
        return withSeq(valueSyntax,
                       ArrayList::new,
                       true,
                       ","
        );
    }

    private static <T> XmlSyntax<List<T>> otherList(ValueSyntax<T> valueSyntax) {
        return withSeq(valueSyntax,
                       ArrayList::new,
                       true,
                       "|"
        );
    }

    static <T, C extends Collection<T>> XmlSyntax<C> withSeq(ValueSyntax<T> itemSyntax,
                                                             Supplier<C> emptyCollSupplier,
                                                             boolean preferOldSyntax,
                                                             String delimiter) {
        return new SyntaxSet<>(
            new SeqSyntax<>(itemSyntax, emptyCollSupplier),
            delimitedString(itemSyntax::toString, itemSyntax::fromString, delimiter, emptyCollSupplier),
            preferOldSyntax
        );
    }


    public static <T, C extends Collection<T>> ValueSyntax<C> delimitedString(
        Function<? super T, String> toString,
        Function<String, ? extends T> fromString,
        String delimiter,
        Supplier<C> emptyCollSupplier
    ) {

        return new ValueSyntax<>(
            coll -> coll.stream().map(toString).collect(Collectors.joining(delimiter)),
            string -> {
                C coll = emptyCollSupplier.get();
                for (String item : string.split(Pattern.quote(delimiter))) {
                    coll.add(fromString.apply(item));
                }
                return coll;
            }
        );
    }
}
