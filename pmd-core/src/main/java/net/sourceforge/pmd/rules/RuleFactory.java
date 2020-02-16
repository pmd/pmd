/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.rules;

import static net.sourceforge.pmd.properties.PropertyDescriptorField.DEFAULT_VALUE;

import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetReference;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyDescriptorField;
import net.sourceforge.pmd.properties.PropertyTypeId;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder;
import net.sourceforge.pmd.util.ResourceLoader;


/**
 * Builds rules from rule XML nodes.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
@InternalApi
@Deprecated
public class RuleFactory {

    private static final Logger LOG = Logger.getLogger(RuleFactory.class.getName());

    private static final String DEPRECATED = "deprecated";
    private static final String NAME = "name";
    private static final String MESSAGE = "message";
    private static final String EXTERNAL_INFO_URL = "externalInfoUrl";
    private static final String MINIMUM_LANGUAGE_VERSION = "minimumLanguageVersion";
    private static final String MAXIMUM_LANGUAGE_VERSION = "maximumLanguageVersion";
    private static final String SINCE = "since";
    private static final String PROPERTIES = "properties";
    private static final String PRIORITY = "priority";
    private static final String EXAMPLE = "example";
    private static final String DESCRIPTION = "description";
    private static final String PROPERTY = "property";
    private static final String CLASS = "class";

    private static final List<String> REQUIRED_ATTRIBUTES = Collections.unmodifiableList(Arrays.asList(NAME, CLASS));

    private final ResourceLoader resourceLoader;

    /**
     * @deprecated Use {@link #RuleFactory(ResourceLoader)} instead.
     */
    @Deprecated
    public RuleFactory() {
        this(new ResourceLoader());
    }

    /**
     * @param resourceLoader The resource loader to load the rule from jar
     */
    public RuleFactory(final ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Decorates a referenced rule with the metadata that are overridden in the given rule element.
     *
     * <p>Declaring a property in the overriding element throws an exception (the property must exist in the referenced
     * rule).
     *
     * @param referencedRule Referenced rule
     * @param ruleSetReference the ruleset, where the referenced rule is defined
     * @param ruleElement    Element overriding some metadata about the rule
     *
     * @return A rule reference to the referenced rule
     */
    public RuleReference decorateRule(Rule referencedRule, RuleSetReference ruleSetReference, Element ruleElement) {
        RuleReference ruleReference = new RuleReference(referencedRule, ruleSetReference);

        if (ruleElement.hasAttribute(DEPRECATED)) {
            ruleReference.setDeprecated(Boolean.parseBoolean(ruleElement.getAttribute(DEPRECATED)));
        }
        if (ruleElement.hasAttribute(NAME)) {
            ruleReference.setName(ruleElement.getAttribute(NAME));
        }
        if (ruleElement.hasAttribute(MESSAGE)) {
            ruleReference.setMessage(ruleElement.getAttribute(MESSAGE));
        }
        if (ruleElement.hasAttribute(EXTERNAL_INFO_URL)) {
            ruleReference.setExternalInfoUrl(ruleElement.getAttribute(EXTERNAL_INFO_URL));
        }

        for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
            Node node = ruleElement.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                switch (node.getNodeName()) {
                case DESCRIPTION:
                    ruleReference.setDescription(parseTextNode(node));
                    break;
                case EXAMPLE:
                    ruleReference.addExample(parseTextNode(node));
                    break;
                case PRIORITY:
                    ruleReference.setPriority(RulePriority.valueOf(Integer.parseInt(parseTextNode(node))));
                    break;
                case PROPERTIES:
                    setPropertyValues(ruleReference, (Element) node);
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected element <" + node.getNodeName()
                                                       + "> encountered as child of <rule> element for Rule "
                                                       + ruleReference.getName());
                }
            }
        }

        return ruleReference;
    }

    /**
     * Parses a rule element and returns a new rule instance.
     *
     * <p>Notes: The ruleset name is not set here. Exceptions raised from this method indicate invalid XML structure,
     * with regards to the expected schema, while RuleBuilder validates the semantics.
     *
     * @param ruleElement The rule element to parse
     *
     * @return A new instance of the rule described by this element
     * @throws IllegalArgumentException if the element doesn't describe a valid rule.
     */
    public Rule buildRule(Element ruleElement) {
        checkRequiredAttributesArePresent(ruleElement);

        String name = ruleElement.getAttribute(NAME);

        RuleBuilder builder = new RuleBuilder(name,
                resourceLoader,
                ruleElement.getAttribute(CLASS),
                ruleElement.getAttribute("language"));

        if (ruleElement.hasAttribute(MINIMUM_LANGUAGE_VERSION)) {
            builder.minimumLanguageVersion(ruleElement.getAttribute(MINIMUM_LANGUAGE_VERSION));
        }

        if (ruleElement.hasAttribute(MAXIMUM_LANGUAGE_VERSION)) {
            builder.maximumLanguageVersion(ruleElement.getAttribute(MAXIMUM_LANGUAGE_VERSION));
        }

        if (ruleElement.hasAttribute(SINCE)) {
            builder.since(ruleElement.getAttribute(SINCE));
        }

        builder.message(ruleElement.getAttribute(MESSAGE));
        builder.externalInfoUrl(ruleElement.getAttribute(EXTERNAL_INFO_URL));
        builder.setDeprecated(hasAttributeSetTrue(ruleElement, DEPRECATED));
        builder.usesDFA(hasAttributeSetTrue(ruleElement, "dfa"));
        builder.usesTyperesolution(hasAttributeSetTrue(ruleElement, "typeResolution"));
        // Disabled until it's safe
        // builder.usesMultifile(hasAttributeSetTrue(ruleElement, "multifile"));

        Element propertiesElement = null;

        final NodeList nodeList = ruleElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            switch (node.getNodeName()) {
            case DESCRIPTION:
                builder.description(parseTextNode(node));
                break;
            case EXAMPLE:
                builder.addExample(parseTextNode(node));
                break;
            case PRIORITY:
                builder.priority(Integer.parseInt(parseTextNode(node).trim()));
                break;
            case PROPERTIES:
                parsePropertiesForDefinitions(builder, node);
                propertiesElement = (Element) node;
                break;
            default:
                throw new IllegalArgumentException("Unexpected element <" + node.getNodeName()
                                                   + "> encountered as child of <rule> element for Rule "
                                                   + name);
            }
        }

        Rule rule;
        try {
            rule = builder.build();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            LOG.log(Level.SEVERE, "Error instantiating a rule", e);
            throw new RuntimeException(e);
        }

        if (propertiesElement != null) {
            setPropertyValues(rule, propertiesElement);
        }

        return rule;
    }

    private void checkRequiredAttributesArePresent(Element ruleElement) {
        // add an attribute name here to make it required

        for (String att : REQUIRED_ATTRIBUTES) {
            if (!ruleElement.hasAttribute(att)) {
                throw new IllegalArgumentException("Missing '" + att + "' attribute");
            }
        }
    }

    /**
     * Parses a properties element looking only for the values of the properties defined or overridden.
     *
     * @param propertiesNode Node to parse
     *
     * @return A map of property names to their value
     */
    private Map<String, String> getPropertyValuesFrom(Element propertiesNode) {
        Map<String, String> overridenProperties = new HashMap<>();

        for (int i = 0; i < propertiesNode.getChildNodes().getLength(); i++) {
            Node node = propertiesNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && PROPERTY.equals(node.getNodeName())) {
                Entry<String, String> overridden = getPropertyValue((Element) node);
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
            if (node.getNodeType() == Node.ELEMENT_NODE && PROPERTY.equals(node.getNodeName())
                && isPropertyDefinition((Element) node)) {
                PropertyDescriptor<?> descriptor = parsePropertyDefinition((Element) node);
                builder.defineProperty(descriptor);
            }
        }
    }

    /**
     * Gets a mapping of property name to its value from the given property element.
     *
     * @param propertyElement Property element
     *
     * @return An entry of property name to its value
     */
    private Entry<String, String> getPropertyValue(Element propertyElement) {
        String name = propertyElement.getAttribute(PropertyDescriptorField.NAME.attributeName());
        return new SimpleEntry<>(name, valueFrom(propertyElement));
    }

    /**
     * Overrides the rule's properties with the values defined in the element.
     *
     * @param rule          The rule
     * @param propertiesElt The {@literal <properties>} element
     */
    private void setPropertyValues(Rule rule, Element propertiesElt) {
        Map<String, String> overridden = getPropertyValuesFrom(propertiesElt);

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

    /**
     * Finds out if the property element defines a property.
     *
     * @param node Property element
     *
     * @return True if this element defines a new property, false if this is just stating a value
     */
    private static boolean isPropertyDefinition(Element node) {
        return node.hasAttribute(PropertyDescriptorField.TYPE.attributeName());
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

        PropertyDescriptorExternalBuilder<?> pdFactory = PropertyTypeId.factoryFor(typeId);
        if (pdFactory == null) {
            throw new IllegalArgumentException("No property descriptor factory for type: " + typeId);
        }

        Map<PropertyDescriptorField, String> values = new HashMap<>();
        NamedNodeMap atts = propertyElement.getAttributes();

        /// populate a map of values for an individual descriptor
        for (int i = 0; i < atts.getLength(); i++) {
            Attr a = (Attr) atts.item(i);
            values.put(PropertyDescriptorField.getConstant(a.getName()), a.getValue());
        }

        if (StringUtils.isBlank(values.get(DEFAULT_VALUE))) {
            NodeList children = propertyElement.getElementsByTagName(DEFAULT_VALUE.attributeName());
            if (children.getLength() == 1) {
                values.put(DEFAULT_VALUE, children.item(0).getTextContent());
            } else {
                throw new IllegalArgumentException("No value defined!");
            }
        }

        // casting is not pretty but prevents the interface from having this method
        return pdFactory.build(values);
    }

    /** Gets the string value from a property node. */
    private static String valueFrom(Element propertyNode) {
        String strValue = propertyNode.getAttribute(DEFAULT_VALUE.attributeName());

        if (StringUtils.isNotBlank(strValue)) {
            return strValue;
        }

        final NodeList nodeList = propertyNode.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && "value".equals(node.getNodeName())) {
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
