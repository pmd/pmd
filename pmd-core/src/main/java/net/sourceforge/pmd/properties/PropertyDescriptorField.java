/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;

import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.properties.xml.XmlErrorReporter;


/**
 * Field names for parsing the properties out of the ruleset xml files. These are intended to be used as the keys to a
 * map of fields to values. Most property descriptors can be built directly from such a map using their factory.
 *
 * @author Brian Remedios
 * @see RuleSetFactory
 * @see PropertyTypeId
 * @deprecated Will be removed with 7.0.0
 */
@Deprecated
public enum PropertyDescriptorField {

    /** The type of the property. */
    TYPE("type"),
    /** The name of the property. */
    NAME("name"),
    /** The description of the property. */
    DESCRIPTION("description"),
    /** The default value. */
    DEFAULT_VALUE("value"),
    /** For multi-valued properties, this defines the delimiter of the single values. */
    DELIMITER("delimiter");

    private final String attributeName;


    PropertyDescriptorField(String attributeName) {
        this.attributeName = attributeName;
    }

    @NonNull
    public String getOrThrow(Element element, XmlErrorReporter err) {
        String attribute = element.getAttribute(attributeName);
        if (attribute == null) {
            throw err.error(element, "Attribute '" + attributeName + "' is required, but missing");
        }

        return attribute;
    }

    @Nullable
    public String getOptional(Element element) {
        return element.getAttribute(attributeName);
    }

    public void setOn(Element element, String value) {
        element.setAttribute(attributeName, value);
    }

    /**
     * Returns the String name of this attribute.
     *
     * @return The attribute's name
     */
    public String attributeName() {
        return attributeName;
    }


    @Override
    public String toString() {
        return attributeName();
    }


}
