/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.rules;

import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.MAXIMUM_LANGUAGE_VERSION;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.MINIMUM_LANGUAGE_VERSION;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.NAME;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PROPERTY_TYPE;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PROPERTY_VALUE;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.ERR__INVALID_LANG_VERSION;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.ERR__INVALID_LANG_VERSION_NO_NAMED_VERSION;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.ERR__PROPERTY_DOES_NOT_EXIST;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.IGNORED__DUPLICATE_PROPERTY_SETTER;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSetReference;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyDescriptorField;
import net.sourceforge.pmd.properties.PropertyTypeId;
import net.sourceforge.pmd.properties.ValueParser;
import net.sourceforge.pmd.properties.builders.PropertyDescriptorExternalBuilder;
import net.sourceforge.pmd.util.ResourceLoader;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.internal.xml.PmdXmlReporter;
import net.sourceforge.pmd.util.internal.xml.SchemaConstant;
import net.sourceforge.pmd.util.internal.xml.SchemaConstants;
import net.sourceforge.pmd.util.internal.xml.XmlErrorMessages;
import net.sourceforge.pmd.util.internal.xml.XmlUtil;

import com.github.oowekyala.ooxml.DomUtils;
import com.github.oowekyala.ooxml.messages.XmlException;


/**
 * Builds rules from rule XML nodes.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
@InternalApi
@Deprecated
public class RuleFactory {

    private final ResourceLoader resourceLoader;
    private final LanguageRegistry languageRegistry;

    /**
     * @param resourceLoader The resource loader to load the rule from jar
     */
    public RuleFactory(ResourceLoader resourceLoader,
                       LanguageRegistry languageRegistry) {
        this.resourceLoader = resourceLoader;
        this.languageRegistry = languageRegistry;
    }

    /**
     * Decorates a referenced rule with the metadata that are overridden in the given rule element.
     *
     * <p>Declaring a property in the overriding element throws an exception (the property must exist in the referenced
     * rule).
     *
     * @param referencedRule   Referenced rule
     * @param ruleSetReference the ruleset, where the referenced rule is defined
     * @param ruleElement      Element overriding some metadata about the rule
     * @param err              Error reporter
     *
     * @return A rule reference to the referenced rule
     */
    public RuleReference decorateRule(Rule referencedRule, RuleSetReference ruleSetReference, Element ruleElement, PmdXmlReporter err) {
        RuleReference ruleReference = new RuleReference(referencedRule, ruleSetReference);

        SchemaConstants.DEPRECATED.getAttributeOpt(ruleElement).map(Boolean::parseBoolean).ifPresent(ruleReference::setDeprecated);
        SchemaConstants.NAME.getAttributeOpt(ruleElement).ifPresent(ruleReference::setName);
        SchemaConstants.MESSAGE.getAttributeOpt(ruleElement).ifPresent(ruleReference::setMessage);
        SchemaConstants.EXTERNAL_INFO_URL.getAttributeOpt(ruleElement).ifPresent(ruleReference::setExternalInfoUrl);

        for (Element node : DomUtils.children(ruleElement)) {

            if (SchemaConstants.DESCRIPTION.matchesElt(node)) {

                ruleReference.setDescription(XmlUtil.parseTextNode(node));

            } else if (SchemaConstants.EXAMPLE.matchesElt(node)) {

                ruleReference.addExample(XmlUtil.parseTextNode(node));

            } else if (SchemaConstants.PRIORITY.matchesElt(node)) {

                RulePriority priority = parsePriority(err, node);
                if (priority == null) {
                    priority = RulePriority.MEDIUM;
                }
                ruleReference.setPriority(priority);

            } else if (SchemaConstants.PROPERTIES.matchesElt(node)) {

                setPropertyValues(ruleReference, node, err);

            } else {
                err.at(node).error(
                    XmlErrorMessages.ERR__UNEXPECTED_ELEMENT_IN,
                    node.getTagName(),
                    "rule " + ruleReference.getName()
                );
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
     *
     * @throws IllegalArgumentException if the element doesn't describe a valid rule.
     */
    public Rule buildRule(Element ruleElement, PmdXmlReporter err) {

        Rule rule;
        try {
            String clazz = SchemaConstants.CLASS.getNonBlankAttribute(ruleElement, err);
            rule = resourceLoader.loadRuleFromClassPath(clazz);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Attr node = SchemaConstants.CLASS.getAttributeNode(ruleElement);
            throw err.at(node).error(e);
        }

        rule.setName(NAME.getNonBlankAttribute(ruleElement, err));
        if (rule.getLanguage() == null) {
            setLanguage(ruleElement, err, rule);
        }
        Language language = rule.getLanguage();
        assert language != null;

        rule.setMinimumLanguageVersion(getLanguageVersion(ruleElement, err, language, MINIMUM_LANGUAGE_VERSION));
        rule.setMaximumLanguageVersion(getLanguageVersion(ruleElement, err, language, MAXIMUM_LANGUAGE_VERSION));
        checkVersionsAreOrdered(ruleElement, err, rule);

        SchemaConstants.SINCE.getAttributeOpt(ruleElement).ifPresent(rule::setSince);
        SchemaConstants.MESSAGE.getAttributeOpt(ruleElement).ifPresent(rule::setMessage);
        SchemaConstants.EXTERNAL_INFO_URL.getAttributeOpt(ruleElement).ifPresent(rule::setExternalInfoUrl);
        rule.setDeprecated(SchemaConstants.DEPRECATED.getAsBooleanAttr(ruleElement, false));

        for (Element node : DomUtils.children(ruleElement)) {
            if (SchemaConstants.DESCRIPTION.matchesElt(node)) {

                rule.setDescription(XmlUtil.parseTextNode(node));

            } else if (SchemaConstants.EXAMPLE.matchesElt(node)) {

                rule.addExample(XmlUtil.parseTextNode(node));

            } else if (SchemaConstants.PRIORITY.matchesElt(node)) {

                RulePriority rp = parsePriority(err, node);
                if (rp == null) {
                    rp = RulePriority.MEDIUM;
                }
                rule.setPriority(rp);

            } else if (SchemaConstants.PROPERTIES.matchesElt(node)) {

                parsePropertiesForDefinitions(rule, node, err);
                setPropertyValues(rule, node, err);

            } else {
                throw err.at(node).error(
                    XmlErrorMessages.ERR__UNEXPECTED_ELEMENT_IN,
                    "rule " + NAME.getAttributeOrNull(ruleElement));
            }
        }

        return rule;
    }

    private void checkVersionsAreOrdered(Element ruleElement, PmdXmlReporter err, Rule rule) {
        if (rule.getMinimumLanguageVersion() != null && rule.getMaximumLanguageVersion() != null
            && rule.getMinimumLanguageVersion().compareTo(rule.getMaximumLanguageVersion()) > 0) {
            throw err.at(MINIMUM_LANGUAGE_VERSION.getAttributeNode(ruleElement))
                     .error(
                         XmlErrorMessages.ERR__INVALID_VERSION_RANGE,
                         rule.getMinimumLanguageVersion(),
                         rule.getMaximumLanguageVersion()
                     );
        }
    }


    /**
     * Parse a priority. If invalid, report it and return null.
     */
    public static @Nullable RulePriority parsePriority(PmdXmlReporter err, Element node) {
        String text = XmlUtil.parseTextNode(node);
        RulePriority rp = RulePriority.valueOfNullable(text);
        if (rp == null) {
            err.at(node).error(XmlErrorMessages.ERR__INVALID_PRIORITY_VALUE, text);
            return null;
        }
        return rp;
    }

    private LanguageVersion getLanguageVersion(Element ruleElement, PmdXmlReporter err, Language language, SchemaConstant attrName) {
        if (attrName.hasAttribute(ruleElement)) {
            String attrValue = attrName.getAttributeOrThrow(ruleElement, err);
            LanguageVersion version = language.getVersion(attrValue);
            if (version == null) {
                String supportedVersions = language.getVersions().stream()
                                                   .map(LanguageVersion::getVersion)
                                                   .filter(it -> !it.isEmpty())
                                                   .map(StringUtil::inSingleQuotes)
                                                   .collect(Collectors.joining(", "));
                String message = supportedVersions.isEmpty()
                                 ? ERR__INVALID_LANG_VERSION_NO_NAMED_VERSION
                                 : ERR__INVALID_LANG_VERSION;
                throw err.at(attrName.getAttributeNode(ruleElement))
                         .error(
                             message,
                             attrValue,
                             language.getTerseName(),
                             supportedVersions
                         );
            }
            return version;
        }
        return null;
    }

    private void setLanguage(Element ruleElement, PmdXmlReporter err, Rule rule) {
        String langId = SchemaConstants.LANGUAGE.getNonBlankAttribute(ruleElement, err);
        Language lang = languageRegistry.getLanguageById(langId);
        if (lang == null) {
            Attr node = SchemaConstants.LANGUAGE.getAttributeNode(ruleElement);
            throw err.at(node)
                     .error("Invalid language ''{0}'', possible values are {1}", langId, supportedLanguages());
        }
        rule.setLanguage(lang);
    }

    private @NonNull String supportedLanguages() {
        return languageRegistry.commaSeparatedList(l -> StringUtil.inSingleQuotes(l.getId()));
    }

    /**
     * Parses the properties node and adds property definitions to the builder. Doesn't care for value overriding, that
     * will be handled after the rule instantiation.
     *
     * @param rule           Rule builder
     * @param propertiesNode Node to parse
     * @param err            Error reporter
     */
    private void parsePropertiesForDefinitions(Rule rule, Element propertiesNode, @NonNull PmdXmlReporter err) {
        for (Element child : SchemaConstants.PROPERTY_ELT.getElementChildrenNamedReportOthers(propertiesNode, err)) {
            if (isPropertyDefinition(child)) {
                rule.definePropertyDescriptor(parsePropertyDefinition(child, err));
            }
        }
    }

    /**
     * Overrides the rule's properties with the values defined in the element.
     *
     * @param rule          The rule
     * @param propertiesElt The {@literal <properties>} element
     */
    private void setPropertyValues(Rule rule, Element propertiesElt, PmdXmlReporter err) {
        Set<String> overridden = new HashSet<>();

        XmlException exception = null;
        for (Element element : SchemaConstants.PROPERTY_ELT.getElementChildrenNamedReportOthers(propertiesElt, err)) {
            String name = SchemaConstants.NAME.getAttributeOrThrow(element, err);
            if (!overridden.add(name)) {
                err.at(element).warn(IGNORED__DUPLICATE_PROPERTY_SETTER, name);
                continue;
            }

            PropertyDescriptor<?> desc = rule.getPropertyDescriptor(name);
            if (desc == null) {
                // todo just warn and ignore
                throw err.at(element).error(ERR__PROPERTY_DOES_NOT_EXIST, name, rule.getName());
            }
            try {
                setRulePropertyCapture(rule, desc, element, err);
            } catch (XmlException e) {
                if (exception == null) {
                    exception = e;
                } else {
                    exception.addSuppressed(e);
                }
            }
        }
        if (exception != null) {
            throw exception;
        }
    }

    private <T> void setRulePropertyCapture(Rule rule, PropertyDescriptor<T> descriptor, Element propertyElt, PmdXmlReporter err) {
        T value = parsePropertyValue(propertyElt, err, descriptor::valueFrom);
        rule.setProperty(descriptor, value);
    }

    /**
     * Finds out if the property element defines a property.
     *
     * @param node Property element
     *
     * @return True if this element defines a new property, false if this is just stating a value
     */
    private static boolean isPropertyDefinition(Element node) {
        return SchemaConstants.PROPERTY_TYPE.hasAttribute(node);
    }

    /**
     * Parses a property definition node and returns the defined property descriptor.
     *
     * @param propertyElement Property node to parse
     * @param err             Error reporter
     *
     * @return The property descriptor
     */
    private static PropertyDescriptor<?> parsePropertyDefinition(Element propertyElement, PmdXmlReporter err) {

        String typeId = SchemaConstants.PROPERTY_TYPE.getAttributeOrThrow(propertyElement, err);

        PropertyDescriptorExternalBuilder<?> pdFactory = PropertyTypeId.factoryFor(typeId);
        if (pdFactory == null) {
            throw err.at(PROPERTY_TYPE.getAttributeNode(propertyElement))
                     .error(
                         XmlErrorMessages.ERR__UNSUPPORTED_PROPERTY_TYPE,
                         typeId
                     );
        }

        return propertyDefCapture(propertyElement, err, pdFactory);
    }

    private static <T> PropertyDescriptor<T> propertyDefCapture(Element propertyElement,
                                                                PmdXmlReporter err,
                                                                PropertyDescriptorExternalBuilder<T> factory) {
        // TODO support constraints like numeric range

        String name = SchemaConstants.NAME.getNonBlankAttributeOrThrow(propertyElement, err);
        String description = SchemaConstants.DESCRIPTION.getNonBlankAttributeOrThrow(propertyElement, err);

        Map<PropertyDescriptorField, String> values = new HashMap<>();
        values.put(PropertyDescriptorField.NAME, name);
        values.put(PropertyDescriptorField.DESCRIPTION, description);
        String defaultValue = parsePropertyValue(propertyElement, err, s -> s);
        values.put(PropertyDescriptorField.DEFAULT_VALUE, defaultValue);

        // populate remaining fields
        for (Node attrNode : DomUtils.asList(propertyElement.getAttributes())) {
            Attr attr = (Attr) attrNode;
            PropertyDescriptorField field = PropertyDescriptorField.getConstant(attr.getName());
            if (field == PropertyDescriptorField.NAME
                || field == PropertyDescriptorField.DEFAULT_VALUE
                || field == PropertyDescriptorField.DESCRIPTION) {
                continue;
            }
            if (field == null) {
                err.at(attr).warn(XmlErrorMessages.IGNORED__UNEXPECTED_ATTRIBUTE_IN, propertyElement.getLocalName());
                continue;
            }
            values.put(field, attr.getValue());
        }

        try {
            return factory.build(values);
        } catch (IllegalArgumentException e) {
            // builder threw, rethrow with XML location
            throw err.at(propertyElement).error(e);
        }
    }

    private static <T> T parsePropertyValue(Element propertyElt, PmdXmlReporter err, ValueParser<T> parser) {
        @Nullable String defaultAttr = PROPERTY_VALUE.getAttributeOrNull(propertyElt);
        if (defaultAttr != null) {
            Attr attrNode = PROPERTY_VALUE.getAttributeNode(propertyElt);

            try {
                return parser.valueOf(defaultAttr);
            } catch (IllegalArgumentException e) {
                throw err.at(attrNode).error(e);
            }

        } else {
            Element child = PROPERTY_VALUE.getSingleChildIn(propertyElt, err);
            String text = XmlUtil.parseTextNode(child);
            try {
                return parser.valueOf(text);
            } catch (IllegalArgumentException e) {
                throw err.at(child).error(e);
            }
        }
    }
}
