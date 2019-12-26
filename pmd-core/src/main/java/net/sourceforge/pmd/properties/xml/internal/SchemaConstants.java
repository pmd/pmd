/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.properties.xml.internal;

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
    PROPERTY_VALUE("value");

    private final String name;


    SchemaConstants(String name) {
        this.name = name;
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
        return XmlUtils.getElementChildren(elt)
                       .filter(it -> it.getTagName().equals(name))
                       .collect(Collectors.toList());
    }

    public Element getSingleChildIn(Element elt, XmlErrorReporter err) {
        List<Element> children = getChildrenIn(elt);
        if (children.size() == 1) {
            return children.get(0);
        } else if (children.size() == 0) {
            throw err.error(elt, XmlErrorMessages.MISSING_REQUIRED_ELEMENT, name);
        } else {
            for (int i = 1; i < children.size(); i++) {
                err.warn(children.get(i), XmlErrorMessages.IGNORED_DUPLICATE_CHILD_ELEMENT, name);
            }
            return children.get(0);
        }
    }

    public void setOn(Element element, String value) {
        element.setAttribute(name, value);
    }

    /**
     * Returns the String name of this attribute.
     *
     * @return The attribute's name
     */
    public String attributeName() {
        return name;
    }


    @Override
    public String toString() {
        return attributeName();
    }


}
