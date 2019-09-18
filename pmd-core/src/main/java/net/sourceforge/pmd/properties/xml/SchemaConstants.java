/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.properties.xml;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;


/**
 * Constants of the ruleset schema.
 */
public enum SchemaConstants {

    /** The type of the property. */
    TYPE("type"),
    /** The name of the property. */
    NAME("name"),
    /** The description of the property. */
    DESCRIPTION("description"),
    /** The default value. */
    PROPERTY_VALUE("value"),
    /** For multi-valued properties, this defines the delimiter of the single values. */
    DELIMITER("delimiter");

    private final String attributeName;


    SchemaConstants(String attributeName) {
        this.attributeName = attributeName;
    }

    @NonNull
    public String getAttributeOrThrow(Element element, XmlErrorReporter err) {
        String attribute = element.getAttribute(attributeName);
        if (attribute == null) {
            throw err.error(element, XmlUtils.MISSING_REQUIRED_ATTRIBUTE, attributeName);
        }

        return attribute;
    }

    @Nullable
    public String getAttributeOpt(Element element) {
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
