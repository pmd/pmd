/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import java.util.Objects;
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

    public ValueSyntax(Function<String, ? extends T> fromString) {
        super(VALUE_NAME);
        this.toString = Objects::toString;
        this.fromString = fromString;
    }

    @Override
    public T fromString(String attributeData) {
        return fromString.apply(attributeData);
    }

    public String toString(T data) {
        return toString.apply(data);
    }

    @Override
    public void toXml(Element container, T value) {
        // TODO CDATA/ xml escape
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

    @Override
    public String example() {
        return "<" + getWriteElementName() + ">data</" + getWriteElementName() + ">";
    }

}
