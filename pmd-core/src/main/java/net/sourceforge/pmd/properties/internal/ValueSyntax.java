/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;

/**
 * Serialize to and from a simple string. Examples:
 *
 * <pre>{@code
 *  <value>1</value>
 *  <value>someString</value>
 *  <value>1,2,3</value>
 * }</pre>
 *
 * <pre>This class is special because it enables compatibility with the
 * pre 7.0.0 XML syntax.
 */
public final class ValueSyntax<T> extends XmlSyntax<T> {

    private static final String VALUE_NAME = "value";
    private final Function<? super T, String> toString;
    private final Function<String, ? extends T> fromString;

    public ValueSyntax(Function<? super T, String> toString,
                       Function<String, ? extends T> fromString) {
        super(VALUE_NAME);
        this.toString = toString;
        this.fromString = fromString;
    }

    @Override
    public @Nullable T fromString(Element owner, String attributeData, XmlErrorReporter err) {
        try {
            return fromString.apply(attributeData);
        } catch (IllegalArgumentException e) {
            throw err.error(owner, e);
        }
    }

    @Override
    public void toXml(Element container, T value) {
        container.setTextContent(toString.apply(value));
    }

    @Override
    public T fromXml(Element element, XmlErrorReporter err) {
        return fromString(element, element.getTextContent(), err);
    }

    @Override
    public String example() {
        return "<" + getWriteElementName() + ">data</" + getWriteElementName() + ">";
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
