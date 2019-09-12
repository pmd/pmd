/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.internal.parsers;

import org.w3c.dom.Element;


/**
 * Strategy to serialize a property to and from XML.
 *
 * @author Clément Fournier
 */
public abstract class XmlSyntax<T> {

    private final String eltName;

    /* package */ XmlSyntax(String eltName) {
        this.eltName = eltName;
    }

    /** Extract the value from an XML element. */
    public abstract T fromXml(Element element, XmlErrorReporter err);


    /** Write the value into the given XML element. */
    public abstract void toXml(Element container, T value);


    public final String getElementName() {
        return eltName;
    }


}
