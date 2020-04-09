/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.properties.constraints.PropertyConstraint;


/**
 * Strategy to serialize a value to and from XML. Some strategies support
 * mapping to and from a string, without XML structure. They can be identified
 * with {@link #supportsStringMapping()}. All the standard properties
 * provided by {@link PropertyFactory} do.
 */
public abstract class XmlMapper<T> {

    /* package */ XmlMapper() {
    }

    /**
     * Extract the value from an XML element. If an error occurs, throws
     * an exception with {@link XmlErrorReporter#error(Node, Throwable)}
     * on the most specific node (the type of exception is unspecified).
     * This will check property constraints if any.
     */
    public abstract T fromXml(Element element, XmlErrorReporter err);


    /** Write the value into the given XML element. */
    public abstract void toXml(Element container, T value);

    /**
     * Returns true if this syntax knows how to map values of type {@code T}
     * from and to a simple string, without XML.
     */
    public boolean supportsStringMapping() {
        return false;
    }

    /**
     * Returns the constraints that this mapper applies to values
     * after parsing them. This may be used for documentation, or
     * to check a constraint on a value that was not parsed from
     * XML.
     *
     * @implNote See {@link ConstraintDecorator}
     */
    public abstract List<PropertyConstraint<? super T>> getConstraints();

    /**
     * Returns a new XML mapper that will check parsed values with
     * the given constraint.
     */
    public XmlMapper<T> withConstraint(PropertyConstraint<? super T> t) {
        return new ConstraintDecorator<>(this, Collections.singletonList(t));
    }

    /**
     * Read the value from a string, if it is supported.
     *
     * @throws UnsupportedOperationException if unsupported, see {@link #supportsStringMapping()}
     * @throws IllegalArgumentException      if something goes wrong (but should be reported on the error reporter)
     */
    public T fromString(String attributeData) {
        throw new UnsupportedOperationException("Check #supportsStringMapping()");
    }

    /**
     * Format the value to a string.
     *
     * @throws UnsupportedOperationException if unsupported, see {@link #supportsStringMapping()}
     * @throws IllegalArgumentException      if something goes wrong (but should be reported on the error reporter)
     */
    public String toString(T value) {
        throw new UnsupportedOperationException("Check #supportsStringMapping()");
    }


    /** Get the name that should be used for the element to represent [value]. */
    public abstract String getWriteElementName(T value);


    /** Get all names that can be read using this syntax. */
    public abstract Set<String> getReadElementNames();


    /**
     * Returns some examples for what XML output this strategy produces.
     * For example, {@code <value>1</value>}.
     */
    public final List<String> getExamples() {
        return examplesImpl("", "    ");
    }

    /**
     * Builds examples (impl).
     *
     * @param curIndent  Indentation of the current level
     * @param baseIndent Base indentation string, adding one indent level concats this with the [curIndent]
     */
    protected abstract List<String> examplesImpl(String curIndent, String baseIndent);

    @Override
    public String toString() {
        return getExamples().get(0);
    }

    /**
     * A mapper that has a single name for read and write.
     */
    abstract static class StableXmlMapper<T> extends XmlMapper<T> {

        private final String eltName;

        /* package */ StableXmlMapper(String eltName) {
            this.eltName = eltName;
        }

        @Override
        public String getWriteElementName(T value) {
            return eltName;
        }

        @Override
        public Set<String> getReadElementNames() {
            return setOf(eltName);
        }
    }
}
