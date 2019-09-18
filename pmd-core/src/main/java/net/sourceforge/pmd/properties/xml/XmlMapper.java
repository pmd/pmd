/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;


/**
 * Strategy to serialize a value to and from XML.
 *
 * @author Clément Fournier
 */
public abstract class XmlMapper<T> {

    /* package */ XmlMapper() {
    }

    /** Extract the value from an XML element. */
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
     * Read the value from a string.
     * @throws UnsupportedOperationException if unsupported, see {@link #supportsStringMapping()}
     * @throws IllegalArgumentException if something goes wrong (but should be reported on the error reporter)
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
    public abstract List<String> examples();

    @Override
    public String toString() {
        return examples().get(0);
    }

    abstract static class StableXmlMapper<T> extends XmlMapper<T> {

        private final String eltName;
        private final Set<String> readNames;

        /* package */ StableXmlMapper(String eltName) {
            this(eltName, Collections.singleton(eltName));
        }

        /* package */ StableXmlMapper(String eltName, Set<String> readNames) {
            this.eltName = eltName;
            this.readNames = readNames;
        }

        @Override
        public String getWriteElementName(T value) {
            return eltName;
        }

        @Override
        public Set<String> getReadElementNames() {
            return readNames;
        }
    }
}
