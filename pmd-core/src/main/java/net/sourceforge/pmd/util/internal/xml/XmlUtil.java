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
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.validation.SchemaFactory;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

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

    public static DocumentBuilderFactory newDocumentBuilderFactory() throws ParserConfigurationException {
        // TODO: PMD 8: use DocumentBuilderFactory.newDefaultInstance();
        // In case there is some other implementation on the classpath. We always want the JDK's default implementation
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl", null);
        /*
         * parser hardening
         * https://cheatsheetseries.owasp.org/cheatsheets/XML_External_Entity_Prevention_Cheat_Sheet.html#documentbuilderfactory-dom
         */
        // This is the PRIMARY defense. If DTDs (doctypes) are disallowed, almost all XML entity attacks are prevented
        // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

        // If you can't completely disable DTDs, then at least do the following:
        // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
        // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
        // JDK7+ - http://xml.org/sax/features/external-general-entities
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);

        // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
        // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
        // JDK7+ - http://xml.org/sax/features/external-parameter-entities
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

        // Disable external DTDs as well
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks"
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);

        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return dbf;
    }

    public static SAXParserFactory newSAXParserFactory() throws SAXNotSupportedException, SAXNotRecognizedException, ParserConfigurationException {
        // TODO: PMD 8: use SAXParserFactory.newDefaultInstance();
        // In case there is some other implementation on the classpath. We always want the JDK's default implementation
        SAXParserFactory spf = SAXParserFactory.newInstance("com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl", null);
        spf.setNamespaceAware(true);
        spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        spf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        return spf;
    }

    public static TransformerFactory newTransformerFactory() throws TransformerConfigurationException {
        // TODO: PMD 8: use TransformerFactory.newDefaultInstance()
        // We have saxon on the classpath and newInstance() would pick that implementation by default. But we want
        // the JDK default implementation
        TransformerFactory factory = TransformerFactory
                .newInstance("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl", null);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        return factory;
    }

    public static SchemaFactory newSchemaFactory() throws SAXNotSupportedException, SAXNotRecognizedException {
        // TODO: PMD 8: use SchemaFactory.newDefaultInstance();
        // In case there is some other implementation on the classpath. We always want the JDK's default implementation
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI,
                "com.sun.org.apache.xerces.internal.jaxp.validation.XMLSchemaFactory", null);
        sf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        sf.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        sf.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        return sf;
    }
}
