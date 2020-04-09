/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.w3c.dom.Element;

import net.sourceforge.pmd.internal.util.PredicateUtil;
import net.sourceforge.pmd.internal.util.xml.XmlErrorReporter;
import net.sourceforge.pmd.properties.constraints.PropertyConstraint;
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
    private final Function<? super T, @NonNull String> toString;
    private final Function<@NonNull String, ? extends T> fromString;

    // these are not applied, just used to document the possible values
    private final List<PropertyConstraint<? super T>> docConstraints;

    ValueSyntax(Function<? super T, String> toString,
                Function<@NonNull String, ? extends T> fromString,
                List<PropertyConstraint<? super T>> docConstraints) {
        super(VALUE_NAME);
        this.toString = toString;
        this.fromString = fromString;
        this.docConstraints = docConstraints;
    }

    @Override
    public boolean supportsStringMapping() {
        return true;
    }

    @Override
    public List<PropertyConstraint<? super T>> getConstraints() {
        return docConstraints;
    }

    @Override
    public T fromString(@NonNull String attributeData) {
        return fromString.apply(attributeData);
    }

    @Override
    public @NonNull String toString(T data) {
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

    /**
     * Creates a value syntax that cannot parse just any string, but
     * which only applies the fromString parser if a precondition holds.
     * The precondition is represented by a constraint on strings, and
     * is documented as a constraint on the returned XML mapper.
     */
    static <T> ValueSyntax<T> partialFunction(Function<? super T, @NonNull String> toString,
                                              Function<@NonNull String, ? extends T> fromString,
                                              PropertyConstraint<? super @NonNull String> checker) {
        PropertyConstraint<T> docConstraint = PropertyConstraint.fromPredicate(
            PredicateUtil.always(),
            checker.getConstraintDescription()
        );

        return new ValueSyntax<>(
            toString,
            s -> {
                String error = checker.validate(s);
                if (error != null) {
                    throw new IllegalArgumentException(error);
                }
                return fromString.apply(s);
            },
            listOf(docConstraint)
        );
    }

    static <T> ValueSyntax<T> withDefaultToString(Function<String, ? extends T> fromString) {
        return new ValueSyntax<>(Objects::toString, fromString, Collections.emptyList());
    }

    static <T> ValueSyntax<T> create(Function<? super T, String> toString,
                                     Function<String, ? extends T> fromString) {
        return new ValueSyntax<>(toString, fromString, Collections.emptyList());
    }
}
