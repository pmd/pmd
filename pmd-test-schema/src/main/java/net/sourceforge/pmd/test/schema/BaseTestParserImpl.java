/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.schema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.test.schema.TestSchemaParser.PmdXmlReporter;
import net.sourceforge.pmd.util.StringUtil;

import com.github.oowekyala.ooxml.DomUtils;
import com.github.oowekyala.ooxml.messages.PositionedXmlDoc;
import com.github.oowekyala.ooxml.messages.XmlPosition;
import com.github.oowekyala.ooxml.messages.XmlPositioner;

/**
 * @author Clément Fournier
 */
class BaseTestParserImpl {

    static class ParserV1 extends BaseTestParserImpl {

    }

    public RuleTestCollection parseDocument(Rule rule, PositionedXmlDoc positionedXmlDoc, PmdXmlReporter err) {
        Document doc = positionedXmlDoc.getDocument();
        Element root = doc.getDocumentElement();

        Map<String, Element> codeFragments = parseCodeFragments(err, root);

        Set<String> usedFragments = new HashSet<>();
        List<Element> testCodes = DomUtils.childrenNamed(root, "test-code");
        RuleTestCollection result = new RuleTestCollection();
        for (int i = 0; i < testCodes.size(); i++) {
            RuleTestDescriptor descriptor = new RuleTestDescriptor(i, rule.deepCopy());

            try (PmdXmlReporter errScope = err.newScope()) {
                parseSingleTest(testCodes.get(i), descriptor, codeFragments, usedFragments, positionedXmlDoc.getPositioner(), errScope);
                if (!errScope.hasError()) {
                    result.addTest(descriptor);
                }
            }
        }

        codeFragments.keySet().removeAll(usedFragments);
        codeFragments.forEach((id, node) -> err.at(node).warn("Unused code fragment"));

        return result;
    }

    private Map<String, Element> parseCodeFragments(PmdXmlReporter err, Element root) {
        Map<String, Element> codeFragments = new HashMap<>();

        for (Element node : DomUtils.childrenNamed(root, "code-fragment")) {
            Attr id = getRequiredAttribute("id", node, err);
            if (id == null) {
                continue;
            }

            Element prev = codeFragments.put(id.getValue(), node);
            if (prev != null) {
                err.at(prev).error("Fragment with duplicate id ''{0}'' is ignored", id.getValue());
            }
        }
        return codeFragments;
    }

    private void parseSingleTest(Element testCode,
                                 RuleTestDescriptor descriptor,
                                 Map<String, Element> fragments,
                                 Set<String> usedFragments,
                                 XmlPositioner xmlPositioner,
                                 PmdXmlReporter err) {
        {
            String description = getSingleChildText(testCode, "description", true, err);
            if (description == null) {
                return;
            }
            descriptor.setDescription(description.trim());
        }

        parseBoolAttribute(testCode, "reinitializeRule", true, err, "Attribute 'reinitializeRule' is deprecated and ignored, assumed true");
        parseBoolAttribute(testCode, "useAuxClasspath", true, err, "Attribute 'useAuxClasspath' is deprecated and ignored, assumed true");

        boolean disabled = parseBoolAttribute(testCode, "disabled", false, err, null)
                          | !parseBoolAttribute(testCode, "regressionTest", true, err, "Attribute ''regressionTest'' is deprecated, use ''disabled'' with inverted value");

        descriptor.setDisabled(disabled);


        boolean focused = parseBoolAttribute(testCode, "focused", false, err,
                                             "Attribute focused is used, do not forget to remove it when checking in sources");

        descriptor.setFocused(focused);


        Properties properties = parseRuleProperties(testCode, descriptor.getRule(), err);
        descriptor.getProperties().putAll(properties);

        parseExpectedProblems(testCode, descriptor, err);

        String code = getTestCode(testCode, fragments, usedFragments, err);
        if (code == null) {
            return;
        }
        descriptor.setCode(code);


        LanguageVersion lversion = parseLanguageVersion(testCode, err);
        if (lversion != null) {
            descriptor.setLanguageVersion(lversion);
        }

        XmlPosition startPosition = xmlPositioner.startPositionOf(testCode);
        descriptor.setLineNumber(startPosition.getLine());
    }

    private void parseExpectedProblems(Element testCode, RuleTestDescriptor descriptor, PmdXmlReporter err) {
        Node expectedProblemsNode = getSingleChild(testCode, "expected-problems", true, err);
        if (expectedProblemsNode == null) {
            return;
        }
        int expectedProblems = Integer.parseInt(parseTextNode(expectedProblemsNode));

        List<String> expectedMessages = Collections.emptyList();
        {
            Element messagesNode = getSingleChild(testCode, "expected-messages", false, err);
            if (messagesNode != null) {
                expectedMessages = new ArrayList<>();
                List<Element> messageNodes = DomUtils.childrenNamed(messagesNode, "message");
                if (messageNodes.size() != expectedProblems) {
                    err.at(expectedProblemsNode).error("Number of ''expected-messages'' ({0}) does not match", messageNodes.size());
                    return;
                }

                for (Node message : messageNodes) {
                    expectedMessages.add(parseTextNode(message));
                }
            }
        }

        List<Integer> expectedLineNumbers = Collections.emptyList();
        {
            Element lineNumbers = getSingleChild(testCode, "expected-linenumbers", false, err);
            if (lineNumbers != null) {
                expectedLineNumbers = new ArrayList<>();
                String[] linenos = parseTextNode(lineNumbers).split(",");
                if (linenos.length != expectedProblems) {
                    err.at(expectedProblemsNode).error("Number of ''expected-linenumbers'' ({0}) does not match", linenos.length);
                    return;
                }
                for (String num : linenos) {
                    expectedLineNumbers.add(Integer.valueOf(num.trim()));
                }
            }
        }

        descriptor.recordExpectedViolations(
            expectedProblems,
            expectedLineNumbers,
            expectedMessages
        );

    }

    private String getTestCode(Element testCode, Map<String, Element> fragments, Set<String> usedFragments, PmdXmlReporter err) {
        String code = getSingleChildText(testCode, "code", false, err);
        if (code == null) {
            // Should have a coderef
            List<Element> coderefs = DomUtils.childrenNamed(testCode, "code-ref");
            if (coderefs.isEmpty()) {
                throw new RuntimeException(
                    "Required tag is missing from the test-xml. Supply either a code or a code-ref tag");
            }
            Element coderef = coderefs.get(0);
            Attr id = getRequiredAttribute("id", coderef, err);
            if (id == null) {
                return null;
            }
            Element fragment = fragments.get(id.getValue());
            if (fragment == null) {
                err.at(id).error("Unknown id, known IDs are {0}", fragments.keySet());
                return null;
            }
            usedFragments.add(id.getValue());
            code = parseTextNodeNoTrim(fragment);
        }
        // first trim empty lines at beginning/end, then trim any indentation
        code = StringUtil.trimIndent(Chars.wrap(code).trimBlankLines()).toString();
        return code;
    }

    private LanguageVersion parseLanguageVersion(Element testCode, PmdXmlReporter err) {
        Node sourceTypeNode = getSingleChild(testCode, "source-type", false, err);
        if (sourceTypeNode == null) {
            return null;
        }
        String languageVersionString = parseTextNode(sourceTypeNode);
        LanguageVersion languageVersion = parseSourceType(languageVersionString);
        if (languageVersion != null) {
            return languageVersion;
        }

        err.at(sourceTypeNode).error("Unknown language version ''{0}''", languageVersionString);
        return null;
    }

    /** FIXME this is stupid, the language version may be of a different language than the Rule... */
    private static LanguageVersion parseSourceType(String languageIdAndVersion) {
        final String version;
        final String languageId;
        if (languageIdAndVersion.contains(" ")) {
            version = StringUtils.trimToNull(languageIdAndVersion.substring(languageIdAndVersion.lastIndexOf(' ') + 1));
            languageId = languageIdAndVersion.substring(0, languageIdAndVersion.lastIndexOf(' '));
        } else {
            version = null;
            languageId = languageIdAndVersion;
        }
        Language language = LanguageRegistry.PMD.getLanguageById(languageId);
        if (language != null) {
            if (version == null) {
                return language.getDefaultVersion();
            } else {
                return language.getVersion(version);
            }
        }
        return null;
    }

    private Properties parseRuleProperties(Element testCode, PropertySource knownProps, PmdXmlReporter err) {
        Properties properties = new Properties();
        for (Element ruleProperty : DomUtils.childrenNamed(testCode, "rule-property")) {
            Node nameAttr = getRequiredAttribute("name", ruleProperty, err);
            if (nameAttr == null) {
                continue;
            }
            String propertyName = nameAttr.getNodeValue();
            if (knownProps.getPropertyDescriptor(propertyName) == null) {
                String knownNames = knownProps.getPropertyDescriptors().stream().map(PropertyDescriptor::name)
                        .collect(Collectors.joining(", "));
                err.at(nameAttr).error("Unknown property, known property names are {0}", knownNames);
                continue;
            }
            properties.setProperty(propertyName, parseTextNode(ruleProperty));
        }
        return properties;
    }

    private Attr getRequiredAttribute(String name, Element ruleProperty, PmdXmlReporter err) {
        Attr nameAttr = (Attr) ruleProperty.getAttributes().getNamedItem(name);
        if (nameAttr == null) {
            err.at(ruleProperty).error("Missing ''{0}'' attribute", name);
            return null;
        }
        return nameAttr;
    }

    private boolean parseBoolAttribute(Element testCode, String attrName, boolean defaultValue, PmdXmlReporter err, String deprecationMessage) {
        Attr attrNode = testCode.getAttributeNode(attrName);
        if (attrNode != null) {
            if (deprecationMessage != null) {
                err.at(attrNode).warn(deprecationMessage);
            }
            return Boolean.parseBoolean(attrNode.getNodeValue());
        }
        return defaultValue;
    }


    private String getSingleChildText(Element parentElm, String nodeName, boolean required, PmdXmlReporter err) {
        Node node = getSingleChild(parentElm, nodeName, required, err);
        if (node == null) {
            return null;
        }
        return parseTextNodeNoTrim(node);
    }

    private Element getSingleChild(Element parentElm, String nodeName, boolean required, PmdXmlReporter err) {
        List<Element> nodes = DomUtils.childrenNamed(parentElm, nodeName);
        if (nodes.isEmpty()) {
            if (required) {
                err.at(parentElm).error("Required child ''{0}'' is missing", nodeName);
            }
            return null;
        } else if (nodes.size() > 1) {
            err.at(nodes.get(1)).error("Duplicate tag ''{0}'' is ignored", nodeName);
        }
        return nodes.get(0);
    }

    private static String parseTextNode(Node exampleNode) {
        return parseTextNodeNoTrim(exampleNode).trim();
    }

    private static String parseTextNodeNoTrim(Node exampleNode) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < exampleNode.getChildNodes().getLength(); i++) {
            Node node = exampleNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.CDATA_SECTION_NODE || node.getNodeType() == Node.TEXT_NODE) {
                buffer.append(node.getNodeValue());
            }
        }
        return buffer.toString();
    }


}
