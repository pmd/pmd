/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.internal.xml;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Wraps the name of eg an attribute or element, and provides convenience
 * methods to query the DOM.
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

    public @NonNull String getAttributeOrThrow(Element element, PmdXmlReporter err) {
        String attribute = element.getAttribute(name);
        if (!element.hasAttribute(name)) {
            throw err.at(element).error(XmlErrorMessages.ERR__MISSING_REQUIRED_ATTRIBUTE, name);
        }

        return attribute;
    }

    public @NonNull String getNonBlankAttributeOrThrow(Element element, PmdXmlReporter err) {
        String attribute = element.getAttribute(name);
        if (!element.hasAttribute(name)) {
            throw err.at(element).error(XmlErrorMessages.ERR__MISSING_REQUIRED_ATTRIBUTE, name);
        } else if (StringUtils.isBlank(attribute)) {
            throw err.at(element).error(XmlErrorMessages.ERR__BLANK_REQUIRED_ATTRIBUTE, name);
        }
        return attribute;
    }

    public @Nullable String getAttributeOrNull(Element element) {
        if (element.hasAttribute(name)) {
            return element.getAttribute(name);
        }
        return null;
    }

    public Optional<String> getAttributeOpt(Element element) {
        Attr attr = element.getAttributeNode(name);
        return Optional.ofNullable(attr).map(Attr::getValue);
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

    public List<Element> getElementChildrenNamedReportOthers(Element elt, PmdXmlReporter err) {
        return XmlUtil.getElementChildrenNamedReportOthers(elt, setOf(this), err)
                      .collect(Collectors.toList());
    }

    public Element getSingleChildIn(Element elt, PmdXmlReporter err) {
        return XmlUtil.getSingleChildIn(elt, true, err, setOf(this));
    }

    public Element getOptChildIn(Element elt, PmdXmlReporter err) {
        return XmlUtil.getSingleChildIn(elt, false, err, setOf(this));
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


    public boolean matchesElt(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SchemaConstant that = (SchemaConstant) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public @NonNull String getNonBlankAttribute(Element ruleElement, PmdXmlReporter err) {
        String clazz = getAttributeOrThrow(ruleElement, err);
        if (StringUtils.isBlank(clazz)) {
            Attr node = getAttributeNode(ruleElement);
            throw err.at(node).error("Attribute {0} may not be blank", this);
        }
        return clazz;
    }
}
