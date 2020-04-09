/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.w3c.dom.Element;

import net.sourceforge.pmd.properties.xml.XmlMapper.StableXmlMapper;

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
class ValueSyntax<T> extends StableXmlMapper<T> {

    private static final String VALUE_NAME = "value";
    private final Function<? super T, String> toString;
    private final Function<String, ? extends T> fromString;
    private final boolean delimited;

    ValueSyntax(Function<? super T, String> toString,
                Function<String, ? extends T> fromString,
                boolean delimited) {
        super(VALUE_NAME);
        this.toString = toString;
        this.fromString = fromString;
        this.delimited = delimited;
    }

    @Override
    public boolean supportsStringMapping() {
        return true;
    }

    @Override
    public boolean isStringParserDelimited() {
        return delimited;
    }

    @Override
    public T fromString(String attributeData) {
        return fromString.apply(attributeData);
    }

    @Override
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
    protected List<String> examplesImpl(String curIndent, String baseIndent) {
        return Collections.singletonList(curIndent + "<value>data</value>");
    }

    static <T> ValueSyntax<T> createNonDelimited(Function<String, ? extends T> fromString) {
        return new ValueSyntax<>(Objects::toString, fromString, false);
    }

    static <T> ValueSyntax<T> createDelimited(Function<? super T, String> toString,
                                              Function<String, ? extends T> fromString) {
        return new ValueSyntax<>(toString, fromString, true);
    }
}
