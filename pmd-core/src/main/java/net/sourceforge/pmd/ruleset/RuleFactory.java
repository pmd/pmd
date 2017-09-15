/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ruleset;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.properties.AbstractPropertyDescriptorFactory;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyDescriptorFactory;
import net.sourceforge.pmd.properties.PropertyDescriptorField;
import net.sourceforge.pmd.properties.PropertyDescriptorUtil;

/**
 * Builds rules from rule XML nodes. You cannot use a single one to concurrently build several rules.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class RuleFactory {

    public static final RuleFactory INSTANCE = new RuleFactory();


    private RuleFactory() {

    }


    /**
     * Parses a rule element and returns a new rule instance.
     *
     *
     * <p>Notes: The ruleset name is not set here. Exceptions thrown from this class indicate invalid XML structure,
     * with regards to the expected schema, while RuleBuilder validates the semantics.
     *
     * @param ruleElement The rule element to parse
     *
     * @return A new instance of the rule in this element
     */
    public Rule buildRule(Element ruleElement) {

        checkRequiredAttributesArePresent(ruleElement);

        String name = ruleElement.getAttribute("name");

        RuleBuilder builder = new RuleBuilder(name,
                                              ruleElement.getAttribute("class"),
                                              ruleElement.getAttribute("language"));


        if (ruleElement.hasAttribute("minimumLanguageVersion")) {
            builder.minimumLanguageVersion(ruleElement.getAttribute("minimumLanguageVersion"));
        }

        if (ruleElement.hasAttribute("maximumLanguageVersion")) {
            builder.maximumLanguageVersion(ruleElement.getAttribute("maximumLanguageVersion"));
        }

        if (ruleElement.hasAttribute("since")) {
            builder.since(ruleElement.getAttribute("since"));
        }

        builder.since(ruleElement.getAttribute("since"));
        builder.message(ruleElement.getAttribute("message"));
        builder.externalInfoUrl(ruleElement.getAttribute("externalInfoUrl"));
        builder.setDeprecated(hasAttributeSetTrue(ruleElement, "deprecated"));
        builder.usesDFA(hasAttributeSetTrue(ruleElement, "dfa"));
        builder.usesTyperesolution(hasAttributeSetTrue(ruleElement, "typeResolution"));
        builder.usesMetrics(hasAttributeSetTrue(ruleElement, "metrics"));


        Element propertiesElement = null;

        final NodeList nodeList = ruleElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            String nodeName = node.getNodeName();
            switch (nodeName) {
            case "description":
                builder.description(parseTextNode(node));
                break;
            case "example":
                builder.addExample(parseTextNode(node));
                break;
            case "priority":
                builder.priority(Integer.parseInt(parseTextNode(node).trim()));
                break;
            case "properties":
                parsePropertiesForDefinitions(builder, node);
                propertiesElement = (Element) node;
                break;
            default:
                throw new IllegalArgumentException("Unexpected element <" + nodeName
                                                       + "> encountered as child of <rule> element for Rule "
                                                       + name);
            }
        }

        Rule rule;
        try {
            rule = builder.build();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
            return null;
        }


        if (propertiesElement != null) {
            overrideProperties(rule, propertiesElement);
        }

        return rule;
    }


    private void checkRequiredAttributesArePresent(Element ruleElement) {
        final List<String> required = Arrays.asList("name",
                                                    "class");

        for (String att : required) {
            if (!ruleElement.hasAttribute(att)) {
                throw new IllegalArgumentException("Missing '" + att + "' attribute");
            }
        }
    }


    /**
     * Parses a properties element looking only for property value overrides.
     *
     * @param propertiesNode Node to parse
     *
     * @return The map of overridden properties names to their value
     */
    private Map<String, String> parsePropertiesForOverrides(Element propertiesNode) {
        Map<String, String> overridenProperties = new HashMap<>();

        for (int i = 0; i < propertiesNode.getChildNodes().getLength(); i++) {
            Node node = propertiesNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("property")
                && !isPropertyDefinition((Element) node)) {
                Entry<String, String> overridden = parsePropertyOverride((Element) node);
                overridenProperties.put(overridden.getKey(), overridden.getValue());
            }
        }

        return overridenProperties;
    }


    /**
     * Parses the properties node and adds property definitions to the builder. Doesn't care for value overriding, that
     * will be handled after the rule instantiation.
     *
     * @param builder        Rule builder
     * @param propertiesNode Node to parse
     */
    private void parsePropertiesForDefinitions(RuleBuilder builder, Node propertiesNode) {

        for (int i = 0; i < propertiesNode.getChildNodes().getLength(); i++) {
            Node node = propertiesNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("property")
                && isPropertyDefinition((Element) node)) {
                PropertyDescriptor<?> descriptor = parsePropertyDefinition((Element) node);
                builder.defineProperty(descriptor);
            }
        }
    }


    private Entry<String, String> parsePropertyOverride(Element propertyElement) {
        String name = propertyElement.getAttribute(PropertyDescriptorField.NAME.attributeName());
        return new SimpleEntry<>(name, valueFrom(propertyElement));
    }


    /**
     * Overrides the rule's properties with the values defined in the element.
     *
     * @param rule          The rule
     * @param propertiesElt The {@literal <properties>} element
     */
    private void overrideProperties(Rule rule, Element propertiesElt) {
        Map<String, String> overridden = parsePropertiesForOverrides(propertiesElt);

        for (Entry<String, String> e : overridden.entrySet()) {
            PropertyDescriptor<?> descriptor = rule.getPropertyDescriptor(e.getKey());
            if (descriptor == null) {
                throw new IllegalArgumentException(
                    "Cannot set non-existent property '" + e.getKey() + "' on Rule " + rule.getName());
            }

            setRulePropertyCapture(rule, descriptor, e.getValue());
        }
    }


    private <T> void setRulePropertyCapture(Rule rule, PropertyDescriptor<T> descriptor, String value) {
        rule.setProperty(descriptor, descriptor.valueFrom(value));
    }


    private static boolean isPropertyDefinition(Element node) {
        return StringUtils.isNotBlank(node.getAttribute(PropertyDescriptorField.TYPE.attributeName()));
    }


    /**
     * Parses a property definition node and returns the defined property descriptor.
     *
     * @param propertyElement Property node to parse
     *
     * @return The property descriptor
     */
    private static PropertyDescriptor<?> parsePropertyDefinition(Element propertyElement) {

        String typeId = propertyElement.getAttribute(PropertyDescriptorField.TYPE.attributeName());
        String strValue = valueFrom(propertyElement);

        PropertyDescriptorFactory<?> pdFactory = PropertyDescriptorUtil.factoryFor(typeId);
        if (pdFactory == null) {
            throw new RuntimeException("No property descriptor factory for type: " + typeId);
        }

        Set<PropertyDescriptorField> valueKeys = pdFactory.expectableFields();
        Map<PropertyDescriptorField, String> values = new HashMap<>(valueKeys.size());

        // populate a map of values for an individual descriptor
        for (PropertyDescriptorField field : valueKeys) {
            String valueStr = propertyElement.getAttribute(field.attributeName());
            if (valueStr != null) {
                values.put(field, valueStr);
            }
        }

        if (StringUtils.isBlank(values.get(PropertyDescriptorField.DEFAULT_VALUE))) {
            NodeList children = propertyElement.getElementsByTagName(PropertyDescriptorField.DEFAULT_VALUE.attributeName());
            if (children.getLength() == 1) {
                values.put(PropertyDescriptorField.DEFAULT_VALUE, children.item(0).getTextContent());
            } else {
                throw new RuntimeException("No value defined!");
            }
        }

        // casting is not pretty but prevents the interface from having this method
        return (PropertyDescriptor<?>) ((AbstractPropertyDescriptorFactory) pdFactory).createExternalWith(values);
    }


    /** Gets the string value from a property node. */
    private static String valueFrom(Element propertyNode) {

        String strValue = propertyNode.getAttribute(PropertyDescriptorField.DEFAULT_VALUE.attributeName());

        if (StringUtils.isNotBlank(strValue)) {
            return strValue;
        }

        final NodeList nodeList = propertyNode.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("value")) {
                return parseTextNode(node);
            }
        }
        return null;
    }


    private static boolean hasAttributeSetTrue(Element element, String attributeId) {
        return element.hasAttribute(attributeId) && "true".equalsIgnoreCase(element.getAttribute(attributeId));
    }


    /**
     * Parse a String from a textually type node.
     *
     * @param node The node.
     *
     * @return The String.
     */
    private static String parseTextNode(Node node) {

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
