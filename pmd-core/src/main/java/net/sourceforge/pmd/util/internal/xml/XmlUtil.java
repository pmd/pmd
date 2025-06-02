/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.internal.xml;

import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.ERR__MISSING_REQUIRED_ELEMENT;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.IGNORED__DUPLICATE_CHILD_ELEMENT;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.IGNORED__UNEXPECTED_ELEMENT;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.IGNORED__UNEXPECTED_ELEMENT_IN;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.StringUtil;

import com.github.oowekyala.ooxml.DomUtils;

public final class XmlUtil {

    private XmlUtil() {

    }

    public static Stream<Element> getElementChildren(Element parent) {
        return DomUtils.asList(parent.getChildNodes()).stream()
                       .filter(it -> it.getNodeType() == Node.ELEMENT_NODE)
                       .map(Element.class::cast);
    }

    public static List<Element> getElementChildrenList(Element parent) {
        return getElementChildren(parent).collect(Collectors.toList());
    }

    public static Stream<Element> getElementChildrenNamed(Element parent, Set<SchemaConstant> names) {
        return getElementChildren(parent).filter(e -> matchesName(e, names));
    }

    public static Stream<Element> getElementChildrenNamedReportOthers(Element parent, Set<SchemaConstant> names, PmdXmlReporter err) {
        return getElementChildren(parent)
            .map(it -> {
                if (matchesName(it, names)) {
                    return it;
                } else {
                    err.at(it).warn(IGNORED__UNEXPECTED_ELEMENT_IN, it.getTagName(), formatPossibleNames(names));
                    return null;
                }
            }).filter(Objects::nonNull);
    }

    public static boolean matchesName(Element elt, Set<SchemaConstant> names) {
        return names.stream().anyMatch(it -> it.xmlName().equals(elt.getTagName()));
    }

    public static void reportIgnoredUnexpectedElt(Element parent,
                                                  Element unexpectedChild,
                                                  Set<SchemaConstant> names,
                                                  PmdXmlReporter err) {
        err.at(unexpectedChild).warn(IGNORED__UNEXPECTED_ELEMENT,
                                     unexpectedChild.getTagName(),
                                     parent.getTagName(),
                                     formatPossibleNames(names));
    }

    public static Stream<Element> getElementChildrenNamed(Element parent, String name) {
        return getElementChildren(parent).filter(e -> name.equals(e.getTagName()));
    }


    public static List<Element> getChildrenExpectSingleName(Element elt, String name, PmdXmlReporter err) {
        return XmlUtil.getElementChildren(elt).peek(it -> {
            if (!it.getTagName().equals(name)) {
                err.at(it).warn(IGNORED__UNEXPECTED_ELEMENT_IN, it.getTagName(), name);
            }
        }).collect(Collectors.toList());
    }

    public static Element getSingleChildIn(Element elt, boolean throwOnMissing, PmdXmlReporter err, Set<SchemaConstant> names) {
        List<Element> children = getElementChildrenNamed(elt, names).collect(Collectors.toList());
        if (children.size() == 1) {
            return children.get(0);
        } else if (children.isEmpty()) {
            if (throwOnMissing) {
                throw err.at(elt).error(ERR__MISSING_REQUIRED_ELEMENT, formatPossibleNames(names));
            } else {
                return null;
            }
        } else {
            for (int i = 1; i < children.size(); i++) {
                Element child = children.get(i);
                err.at(child).warn(IGNORED__DUPLICATE_CHILD_ELEMENT, child.getTagName());
            }
            return children.get(0);
        }
    }

    public static Set<SchemaConstant> toConstants(Set<String> names) {
        return CollectionUtil.map(Collectors.toSet(), names, SchemaConstant::new);
    }

    public static @Nullable String formatPossibleNames(Set<SchemaConstant> names) {
        if (names.isEmpty()) {
            return null;
        } else if (names.size() == 1) {
            return StringUtil.inSingleQuotes(names.iterator().next().xmlName());
        } else {
            return "one of " + names.stream()
                                    .map(SchemaConstant::xmlName)
                                    .map(StringUtil::inSingleQuotes)
                                    .collect(Collectors.joining(", "));
        }
    }

    /**
     * Parse a String from a textually type node.
     *
     * @param node The node.
     *
     * @return The String.
     */
    public static String parseTextNode(Node node) {
        final int nodeCount = node.getChildNodes().getLength();
        if (nodeCount == 0) {
            return "";
        }

        StringBuilder buffer = new StringBuilder();

        for (int i = 0; i < nodeCount; i++) {
            Node childNode = node.getChildNodes().item(i);
            if (childNode.getNodeType() == Node.CDATA_SECTION_NODE || childNode.getNodeType() == Node.TEXT_NODE) {
                buffer.append(childNode.getNodeValue());
            }
        }
        return buffer.toString();
    }

}
