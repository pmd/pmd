/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.rules;

import static net.sourceforge.pmd.properties.xml.internal.SchemaConstants.PROPERTY_VALUE;
import static net.sourceforge.pmd.properties.xml.internal.XmlUtils.formatPossibleNames;
import static net.sourceforge.pmd.properties.xml.internal.XmlUtils.getSingleChildIn;
import static net.sourceforge.pmd.properties.xml.internal.XmlUtils.parseTextNode;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetReference;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.PropertyBuilder;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyTypeId;
import net.sourceforge.pmd.properties.PropertyTypeId.BuilderAndMapper;
import net.sourceforge.pmd.properties.xml.XmlErrorReporter;
import net.sourceforge.pmd.properties.xml.XmlMapper;
import net.sourceforge.pmd.properties.xml.internal.SchemaConstants;
import net.sourceforge.pmd.properties.xml.internal.XmlErrorMessages;
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
                    setPropertyValues(ruleReference, (Element) node, dummyErrorReporter());
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
        builder.setDeprecated(SchemaConstants.DEPRECATED.getAsBooleanAttr(ruleElement, false));

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
            setPropertyValues(rule, propertiesElement, dummyErrorReporter());
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
     * Overrides the rule's properties with the values defined in the element.
     *
     * @param rule          The rule
     * @param propertiesElt The {@literal <properties>} element
     */
    private void setPropertyValues(Rule rule, Element propertiesElt, XmlErrorReporter err) {
        Set<String> overridden = new HashSet<>();

        for (Element element : SchemaConstants.PROPERTY_ELT.getElementChildrenNamedReportOthers(propertiesElt, err)) {
            String name = SchemaConstants.NAME.getAttributeOrThrow(element, err);
            if (!overridden.add(name)) {
                err.warn(element, XmlErrorMessages.DUPLICATE_PROPERTY_SETTER, name);
                continue;
            }

            PropertyDescriptor<?> desc = rule.getPropertyDescriptor(name);
            if (desc == null) {
                err.warn(element, XmlErrorMessages.PROPERTY_DOES_NOT_EXIST, name, rule.getName(), knownPropertiesOf(rule));
                continue;
            }
            setRulePropertyCapture(rule, desc, element, err);
        }
    }

    private <T> void setRulePropertyCapture(Rule rule, PropertyDescriptor<T> descriptor, Element propertyElt, XmlErrorReporter err) {
        T value = parsePropertyValue(propertyElt, err, descriptor.xmlMapper());
        rule.setProperty(descriptor, value);
    }

    @Nullable
    private String knownPropertiesOf(Rule rule) {
        Set<String> set = rule.getPropertyDescriptors().stream()
                              .map(PropertyDescriptor::name)
                              .collect(Collectors.toSet());
        return formatPossibleNames(set);
    }

    /**
     * Finds out if the property element defines a property.
     *
     * @param node Property element
     *
     * @return True if this element defines a new property, false if this is just stating a value
     */
    private static boolean isPropertyDefinition(Element node) {
        return node.hasAttribute(SchemaConstants.TYPE.xmlName());
    }

    /**
     * Parses a property definition node and returns the defined property descriptor.
     *
     * @param propertyElement Property node to parse
     *
     * @return The property descriptor
     */
    private static PropertyDescriptor<?> parsePropertyDefinition(Element propertyElement) {
        XmlErrorReporter err = dummyErrorReporter();

        String typeId = SchemaConstants.TYPE.getAttributeOrThrow(propertyElement, err);

        PropertyTypeId factory = PropertyTypeId.lookupMnemonic(typeId);
        if (factory == null) {
            throw new IllegalArgumentException("No property descriptor factory for type: " + typeId);
        }

        return propertyDefCapture(propertyElement, err, factory.getBuilderUtils());
    }

    private static <T> PropertyDescriptor<T> propertyDefCapture(Element propertyElement,
                                                                XmlErrorReporter err,
                                                                BuilderAndMapper<T> factory) {

        String name = SchemaConstants.NAME.getAttributeOrThrow(propertyElement, err);
        String description = SchemaConstants.DESCRIPTION.getAttributeOrThrow(propertyElement, err);

        final PropertyBuilder<?, T> builder = factory.newBuilder(name).desc(description);

        // parse the value
        final XmlMapper<T> syntax = factory.getXmlMapper();

        final T defaultValue = parsePropertyValue(propertyElement, err, syntax);

        builder.defaultValue(defaultValue);

        // TODO support constraints like numeric range

        return builder.build();
    }

    private static <T> T parsePropertyValue(Element propertyElt, XmlErrorReporter err, XmlMapper<T> syntax) {
        @Nullable String defaultAttr = PROPERTY_VALUE.getAttributeOpt(propertyElt);
        if (defaultAttr != null) {
            Attr attrNode = PROPERTY_VALUE.getAttributeNode(propertyElt);

            // the attribute syntax is deprecated.
            err.warn(attrNode,
                     XmlErrorMessages.DEPRECATED_USE_OF_ATTRIBUTE,
                     PROPERTY_VALUE.xmlName(),
                     String.join("\nor\n", syntax.getExamples()));

            try {
                return syntax.fromString(defaultAttr);
            } catch (IllegalArgumentException e) {
                throw err.error(attrNode, e);
            } catch (UnsupportedOperationException e) {
                throw err.error(attrNode,
                                XmlErrorMessages.PROPERTY_DOESNT_SUPPORT_VALUE_ATTRIBUTE,
                                String.join("\nor\n", syntax.getExamples()));
            }

        } else {
            Element child = getSingleChildIn(propertyElt, err, syntax.getReadElementNames());
            // this will report the correct error if any
            return syntax.fromXml(child, err);
        }
    }


    @Deprecated
    @NonNull
    private static XmlErrorReporter dummyErrorReporter() {
        // TODO this is a fake instance, should be provided by context
        //  I'm only doing this to not make the change too contagious for now
        return new XmlErrorReporter() {};
    }
}
