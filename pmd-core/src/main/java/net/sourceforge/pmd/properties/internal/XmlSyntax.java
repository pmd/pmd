/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;


/**
 * Strategy to serialize a value to and from XML.
 *
 * @author Clément Fournier
 */
public abstract class XmlSyntax<T> {

    private final String eltName;
    private final Set<String> readNames;

    /* package */ XmlSyntax(String eltName) {
        this(eltName, Collections.singleton(eltName));
    }

    /* package */ XmlSyntax(String eltName, Set<String> readNames) {
        this.eltName = eltName;
        this.readNames = readNames;
    }

    /** Extract the value from an XML element. */
    public abstract T fromXml(Element element, XmlErrorReporter err);


    /** Write the value into the given XML element. */
    public abstract void toXml(Element container, T value);

    public boolean supportsStringMapping() {
        return false;
    }

    /**
     * Read the value from a string.
     * @throws UnsupportedOperationException if unsupported, see {@link #supportsStringMapping()}
     * @throws IllegalArgumentException if something goes wrong (but should be reported on the error reporter)
     */
    public T fromString(String attributeData) {
        throw new UnsupportedOperationException();
    }

    /**
     * Format the value to a string.
     *
     * @throws UnsupportedOperationException if unsupported, see {@link #supportsStringMapping()}
     * @throws IllegalArgumentException      if something goes wrong (but should be reported on the error reporter)
     */
    public String toString(T value) {
        throw new UnsupportedOperationException();
    }


    /** Get the preferred name used to write elements. */
    public final String getWriteElementName() {
        return eltName;
    }

    public final Set<String> getSupportedReadElementNames() {
        return readNames;
    }

    /**
     * Returns some examples for what XML output this strategy produces.
     * For example, {@code <value>1</value>}.
     */
    public abstract List<String> examples();

    @Override
    public String toString() {
        return examples().get(0);
    }
}
