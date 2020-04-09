/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml.internal;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import net.sourceforge.pmd.properties.xml.XmlErrorReporter;


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

    PROPERTY_ELT("property"),

    PROPERTIES("properties"),
    DEPRECATED("deprecated"),

    ;

    private final String name;


    SchemaConstants(String name) {
        this.name = name;
    }


    public boolean getAsBooleanAttr(Element e, boolean defaultValue) {
        String attr = e.getAttribute(name);
        return attr != null ? Boolean.parseBoolean(attr) : defaultValue;
    }

    @NonNull
    public String getAttributeOrThrow(Element element, XmlErrorReporter err) {
        String attribute = element.getAttribute(name);
        if (attribute == null) {
            throw err.error(element, XmlErrorMessages.MISSING_REQUIRED_ATTRIBUTE, name);
        }

        return attribute;
    }

    @Nullable
    public String getAttributeOpt(Element element) {
        String attr = element.getAttribute(name);
        return attr.isEmpty() ? null : attr;
    }

    @Nullable
    public Attr getAttributeNode(Element element) {
        return element.getAttributeNode(name);
    }

    public List<Element> getChildrenIn(Element elt) {
        return XmlUtils.getElementChildrenNamed(elt, name)
                       .collect(Collectors.toList());
    }

    public List<Element> getElementChildrenNamedReportOthers(Element elt, XmlErrorReporter err) {
        return XmlUtils.getElementChildrenNamedReportOthers(elt, setOf(name), err)
                       .collect(Collectors.toList());
    }

    public Element getSingleChildIn(Element elt, XmlErrorReporter err) {
        return XmlUtils.getSingleChildIn(elt, err, setOf(name));
    }

    public void setOn(Element element, String value) {
        element.setAttribute(name, value);
    }

    /**
     * Returns the String name of this attribute.
     *
     * @return The attribute's name
     */
    public String xmlName() {
        return name;
    }


    @Override
    public String toString() {
        return xmlName();
    }


}
