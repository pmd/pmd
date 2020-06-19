/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util.xml;

import static net.sourceforge.pmd.internal.util.xml.XmlErrorMessages.ERR__MISSING_REQUIRED_ELEMENT;
import static net.sourceforge.pmd.internal.util.xml.XmlErrorMessages.IGNORED__DUPLICATE_CHILD_ELEMENT;
import static net.sourceforge.pmd.internal.util.xml.XmlErrorMessages.IGNORED__UNEXPECTED_ELEMENT;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sourceforge.pmd.properties.xml.XmlMapper;

import com.github.oowekyala.ooxml.messages.XmlErrorReporter;

public final class XmlUtil {

    private XmlUtil() {

    }

    public static List<Node> toList(NodeList lst) {
        ArrayList<Node> nodes = new ArrayList<>();
        for (int i = 0; i < lst.getLength(); i++) {
            nodes.add(lst.item(i));
        }
        return nodes;
    }

    public static Stream<Element> getElementChildren(Element parent) {
        return toList(parent.getChildNodes()).stream()
                                             .filter(it -> it.getNodeType() == Node.ELEMENT_NODE)
                                             .map(Element.class::cast);
    }

    public static Stream<Element> getElementChildrenNamed(Element parent, Set<String> names) {
        return getElementChildren(parent).filter(e -> names.contains(e.getTagName()));
    }

    public static Stream<Element> getElementChildrenNamedReportOthers(Element parent, Set<String> names, XmlErrorReporter err) {
        return getElementChildren(parent)
            .map(it -> {
                if (names.contains(it.getTagName())) {
                    return it;
                } else {
                    err.warn(it, IGNORED__UNEXPECTED_ELEMENT, it.getTagName(), formatPossibleNames(names));
                    return null;
                }
            }).filter(Objects::nonNull);
    }

    public static Stream<Element> getElementChildrenNamed(Element parent, String name) {
        return getElementChildren(parent).filter(e -> name.equals(e.getTagName()));
    }

    public static <T> T expectElement(XmlErrorReporter err, Element elt, XmlMapper<T> syntax) {

        if (!syntax.getReadElementNames().contains(elt.getTagName())) {
            err.warn(elt, "Wrong name, expected " + formatPossibleNames(syntax.getReadElementNames()));
        } else {
            return syntax.fromXml(elt, err);
        }

        return null;
    }


    public static List<Element> getChildrenExpectSingleName(Element elt, String name, XmlErrorReporter err) {
        return XmlUtil.getElementChildren(elt).peek(it -> {
            if (!it.getTagName().equals(name)) {
                err.warn(it, IGNORED__UNEXPECTED_ELEMENT, it.getTagName(), name);
            }
        }).collect(Collectors.toList());
    }

    public static Element getSingleChildIn(Element elt, boolean throwOnMissing, XmlErrorReporter err, Set<String> names) {
        List<Element> children = getElementChildrenNamed(elt, names).collect(Collectors.toList());
        if (children.size() == 1) {
            return children.get(0);
        } else if (children.isEmpty()) {
            if (throwOnMissing) {
                throw err.error(elt, ERR__MISSING_REQUIRED_ELEMENT, formatPossibleNames(names));
            } else {
                return null;
            }
        } else {
            for (int i = 1; i < children.size(); i++) {
                Element child = children.get(i);
                err.warn(child, IGNORED__DUPLICATE_CHILD_ELEMENT, child.getTagName());
            }
            return children.get(0);
        }
    }

    @Nullable
    public static String formatPossibleNames(Set<String> names) {
        if (names.isEmpty()) {
            return null;
        } else if (names.size() == 1) {
            return "'" + names.iterator().next() + "'";
        } else {
            return "one of " + names.stream().map(it -> "'" + it + "'").collect(Collectors.joining(", "));
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
