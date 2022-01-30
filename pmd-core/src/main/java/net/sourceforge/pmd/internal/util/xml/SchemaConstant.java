/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util.xml;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.github.oowekyala.ooxml.messages.XmlErrorReporter;


/**
 * Constants of the ruleset schema.
 */
public class SchemaConstant {

    private final String name;


    public SchemaConstant(String name) {
        this.name = name;
    }


    public boolean getAsBooleanAttr(Element e, boolean defaultValue) {
        String attr = e.getAttribute(name);
        return e.hasAttribute(name) ? Boolean.parseBoolean(attr) : defaultValue;
    }

    public @NonNull String getAttributeOrThrow(Element element, XmlErrorReporter err) {
        String attribute = element.getAttribute(name);
        if (!element.hasAttribute(name)) {
            throw err.error(element, XmlErrorMessages.ERR__MISSING_REQUIRED_ATTRIBUTE, name);
        }

        return attribute;
    }

    public @NonNull String getNonBlankAttributeOrThrow(Element element, XmlErrorReporter err) {
        String attribute = element.getAttribute(name);
        if (!element.hasAttribute(name)) {
            throw err.error(element, XmlErrorMessages.ERR__MISSING_REQUIRED_ATTRIBUTE, name);
        } else if (StringUtils.isBlank(attribute)) {
            throw err.error(element, XmlErrorMessages.ERR__BLANK_REQUIRED_ATTRIBUTE, name);
        }
        return attribute;
    }

    public @Nullable String getAttributeOpt(Element element) {
        String attr = element.getAttribute(name);
        return attr.isEmpty() ? null : attr;
    }

    public @Nullable Attr getAttributeNode(Element element) {
        return element.getAttributeNode(name);
    }

    public boolean hasAttribute(Element element) {
        return element.hasAttribute(name);
    }

    public List<Element> getChildrenIn(Element elt) {
        return XmlUtil.getElementChildrenNamed(elt, name)
                      .collect(Collectors.toList());
    }

    public List<Element> getElementChildrenNamedReportOthers(Element elt, XmlErrorReporter err) {
        return XmlUtil.getElementChildrenNamedReportOthers(elt, setOf(name), err)
                      .collect(Collectors.toList());
    }

    public Element getSingleChildIn(Element elt, XmlErrorReporter err) {
        return XmlUtil.getSingleChildIn(elt, true, err, setOf(name));
    }

    public Element getOptChildIn(Element elt, XmlErrorReporter err) {
        return XmlUtil.getSingleChildIn(elt, false, err, setOf(name));
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
