/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal.parsers;

import java.util.function.Function;

import org.w3c.dom.Element;

/**
 * Serialize to and from a simple string. Examples:
 *
 * <pre>{@code
 *  <value>1</value>
 *  <value>someString</value>
 *  <value>1,2,3</value>
 * }</pre>
 */
public final class ValueSyntax<T> extends XmlSyntax<T> {

    private final Function<? super T, String> toString;
    private final Function<String, ? extends T> fromString;

    public ValueSyntax(Function<? super T, String> toString,
                       Function<String, ? extends T> fromString) {
        super("value");
        this.toString = toString;
        this.fromString = fromString;
    }

    public final String toString(T t) {
        return toString.apply(t);
    }

    public final T fromString(String string) {
        return fromString.apply(string);
    }

    @Override
    public void toXml(Element container, T value) {
        container.setTextContent(toString.apply(value));
    }

    @Override
    public T fromXml(Element element, XmlErrorReporter err) {
        try {
            return fromString.apply(element.getTextContent());
        } catch (IllegalArgumentException e) {
            throw err.error(element, e);
        }
    }
}
