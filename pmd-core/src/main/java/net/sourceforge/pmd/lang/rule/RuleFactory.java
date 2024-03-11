/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.CLASS;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.DELIMITER;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.DEPRECATED;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.DESCRIPTION;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.EXAMPLE;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.EXTERNAL_INFO_URL;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.LANGUAGE;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.MAXIMUM_LANGUAGE_VERSION;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.MESSAGE;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.MINIMUM_LANGUAGE_VERSION;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.NAME;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PRIORITY;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PROPERTIES;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PROPERTY_ELT;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PROPERTY_MAX;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PROPERTY_MIN;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PROPERTY_TYPE;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.PROPERTY_VALUE;
import static net.sourceforge.pmd.util.internal.xml.SchemaConstants.SINCE;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.ERR__INVALID_LANG_VERSION;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.ERR__INVALID_LANG_VERSION_NO_NAMED_VERSION;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.ERR__MISSING_REQUIRED_ELEMENT;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.ERR__PROPERTY_DOES_NOT_EXIST;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.IGNORED__DUPLICATE_PROPERTY_SETTER;
import static net.sourceforge.pmd.util.internal.xml.XmlErrorMessages.IGNORED__PROPERTY_CHILD_HAS_PRECEDENCE;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.internal.RuleSetReference;
import net.sourceforge.pmd.properties.ConstraintViolatedException;
import net.sourceforge.pmd.properties.NumericConstraints;
import net.sourceforge.pmd.properties.PropertyBuilder;
import net.sourceforge.pmd.properties.PropertyConstraint;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySerializer;
import net.sourceforge.pmd.properties.internal.PropertyTypeId;
import net.sourceforge.pmd.properties.internal.PropertyTypeId.BuilderAndMapper;
import net.sourceforge.pmd.util.StringUtil;
import net.sourceforge.pmd.util.internal.ResourceLoader;
import net.sourceforge.pmd.util.internal.xml.PmdXmlReporter;
import net.sourceforge.pmd.util.internal.xml.SchemaConstant;
import net.sourceforge.pmd.util.internal.xml.XmlErrorMessages;
import net.sourceforge.pmd.util.internal.xml.XmlUtil;
import net.sourceforge.pmd.util.log.PmdReporter;

import com.github.oowekyala.ooxml.DomUtils;
import com.github.oowekyala.ooxml.messages.XmlException;


/**
 * Builds rules from rule XML nodes.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
class RuleFactory {

    private final ResourceLoader resourceLoader;
    private final LanguageRegistry languageRegistry;

    /**
     * @param resourceLoader The resource loader to load the rule from jar
     */
    RuleFactory(ResourceLoader resourceLoader,
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

        DEPRECATED.getAttributeOpt(ruleElement).map(Boolean::parseBoolean).ifPresent(ruleReference::setDeprecated);
        NAME.getAttributeOpt(ruleElement).ifPresent(ruleReference::setName);
        MESSAGE.getAttributeOpt(ruleElement).ifPresent(ruleReference::setMessage);
        EXTERNAL_INFO_URL.getAttributeOpt(ruleElement).ifPresent(ruleReference::setExternalInfoUrl);

        for (Element node : DomUtils.children(ruleElement)) {

            if (DESCRIPTION.matchesElt(node)) {

                ruleReference.setDescription(XmlUtil.parseTextNode(node));

            } else if (EXAMPLE.matchesElt(node)) {

                ruleReference.addExample(XmlUtil.parseTextNode(node));

            } else if (PRIORITY.matchesElt(node)) {

                RulePriority priority = parsePriority(err, node);
                if (priority == null) {
                    priority = RulePriority.MEDIUM;
                }
                ruleReference.setPriority(priority);

            } else if (PROPERTIES.matchesElt(node)) {

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
            String clazz = CLASS.getNonBlankAttribute(ruleElement, err);
            rule = resourceLoader.loadRuleFromClassPath(clazz);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            Attr node = CLASS.getAttributeNode(ruleElement);
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

        SINCE.getAttributeOpt(ruleElement).ifPresent(rule::setSince);
        MESSAGE.getAttributeOpt(ruleElement).ifPresent(rule::setMessage);
        EXTERNAL_INFO_URL.getAttributeOpt(ruleElement).ifPresent(rule::setExternalInfoUrl);
        DEPRECATED.getAttributeOpt(ruleElement).map(Boolean::parseBoolean).ifPresent(rule::setDeprecated);

        for (Element node : DomUtils.children(ruleElement)) {
            if (DESCRIPTION.matchesElt(node)) {

                rule.setDescription(XmlUtil.parseTextNode(node));

            } else if (EXAMPLE.matchesElt(node)) {

                rule.addExample(XmlUtil.parseTextNode(node));

            } else if (PRIORITY.matchesElt(node)) {

                RulePriority rp = parsePriority(err, node);
                if (rp == null) {
                    rp = RulePriority.MEDIUM;
                }
                rule.setPriority(rp);

            } else if (PROPERTIES.matchesElt(node)) {

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
                             language.getId(),
                             supportedVersions
                         );
            }
            return version;
        }
        return null;
    }

    private void setLanguage(Element ruleElement, PmdXmlReporter err, Rule rule) {
        String langId = LANGUAGE.getNonBlankAttribute(ruleElement, err);
        Language lang = languageRegistry.getLanguageById(langId);
        if (lang == null) {
            Attr node = LANGUAGE.getAttributeNode(ruleElement);
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
        for (Element child : PROPERTY_ELT.getElementChildrenNamedReportOthers(propertiesNode, err)) {
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
        for (Element element : PROPERTY_ELT.getElementChildrenNamedReportOthers(propertiesElt, err)) {
            String name = NAME.getAttributeOrThrow(element, err);
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
        T value = parsePropertyValue(propertyElt, err, descriptor.serializer());
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
        return PROPERTY_TYPE.hasAttribute(node);
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

        String typeId = PROPERTY_TYPE.getAttributeOrThrow(propertyElement, err);

        PropertyTypeId factory = PropertyTypeId.lookupMnemonic(typeId);
        if (factory == null) {
            throw err.at(PROPERTY_TYPE.getAttributeNode(propertyElement))
                     .error(XmlErrorMessages.ERR__UNSUPPORTED_PROPERTY_TYPE, typeId);
        }

        return propertyDefCapture(propertyElement, err, factory.getBuilderUtils());
    }

    private static <T> PropertyDescriptor<T> propertyDefCapture(Element propertyElement,
                                                                PmdXmlReporter err,
                                                                BuilderAndMapper<T> factory) {

        String name = NAME.getNonBlankAttributeOrThrow(propertyElement, err);
        String description = DESCRIPTION.getNonBlankAttributeOrThrow(propertyElement, err);

        try {
            PropertyBuilder<?, T> builder = factory.newBuilder(name)
                                                   .desc(description);
            if (DELIMITER.hasAttribute(propertyElement)) {
                err.at(DELIMITER.getAttributeNode(propertyElement))
                    .warn(XmlErrorMessages.WARN__DELIMITER_DEPRECATED);
            }

            parseConstraints(propertyElement, factory, builder, err);
            builder.defaultValue(parsePropertyValue(propertyElement, err, factory.getXmlMapper()));
            return builder.build();

        } catch (IllegalArgumentException e) {
            // builder threw, rethrow with XML location
            throw err.at(propertyElement).error(e);
        }
    }

    private static <T> void parseConstraints(Element propertyElement, BuilderAndMapper<T> factory, PropertyBuilder<?, T> builder, PmdXmlReporter err) {
        Optional<Comparable<T>> min = parseIntoComparable(propertyElement, factory, err, PROPERTY_MIN);
        Optional<Comparable<T>> max = parseIntoComparable(propertyElement, factory, err, PROPERTY_MAX);

        if (min.isPresent() && max.isPresent()) {
            if (min.get().compareTo((T) max.get()) > 0) {
                throw err.at(PROPERTY_MIN.getAttributeNode(propertyElement))
                         .error(XmlErrorMessages.ERR__INVALID_VALUE_RANGE);
            }
            @SuppressWarnings({ "unchecked", "rawtypes" })
            PropertyConstraint<T> constraint = NumericConstraints.inRange((Comparable) min.get(), (Comparable) max.get());
            builder.require(constraint);
        } else if (min.isPresent() || max.isPresent()) {
            Comparable<T> minOrMax = min.orElse(max.orElse(null));

            @SuppressWarnings({ "unchecked", "rawtypes" })
            PropertyConstraint<T> constraint = min.isPresent() ? NumericConstraints.above((Comparable) minOrMax)
                                                               : NumericConstraints.below((Comparable) minOrMax);
            builder.require(constraint);
        }
    }

    private static <T> Optional<Comparable<T>> parseIntoComparable(Element propertyElement, BuilderAndMapper<T> factory, PmdXmlReporter err, SchemaConstant schemaConstant) {
        return schemaConstant
            .getAttributeOpt(propertyElement)
            .map(s -> tryParsePropertyValue(factory, s, err.at(schemaConstant.getAttributeNode(propertyElement))))
            .map(s -> asComparableOrThrow(s, err.at(schemaConstant.getAttributeNode(propertyElement))));
    }


    private static <T> @Nullable T tryParsePropertyValue(BuilderAndMapper<T> factory, String value, PmdReporter err) {
        try {
            return factory.getXmlMapper().fromString(value);
        } catch (IllegalArgumentException e) {
            throw err.error(e);
        }
    }

    private static <T> Comparable<T> asComparableOrThrow(T object, PmdReporter err) {
        if (object instanceof Comparable) {
            return (Comparable) object;
        }
        throw err.error("Object is not comparable");
    }

    private static <T> T parsePropertyValue(Element propertyElt, PmdXmlReporter err, PropertySerializer<T> syntax) {
        String valueAttr = PROPERTY_VALUE.getAttributeOrNull(propertyElt);
        Element valueChild = PROPERTY_VALUE.getOptChildIn(propertyElt, err);
        Attr attrNode = PROPERTY_VALUE.getAttributeNode(propertyElt);
        Node node;
        String valueStr;
        if (valueChild != null) {
            if (valueAttr != null) {
                err.at(attrNode).warn(IGNORED__PROPERTY_CHILD_HAS_PRECEDENCE);
            }
            valueStr = valueChild.getTextContent();
            node = valueChild;
        } else if (valueAttr != null) {
            valueStr = valueAttr;
            node = attrNode;
        } else {
            throw err.at(propertyElt).error(ERR__MISSING_REQUIRED_ELEMENT, PROPERTY_VALUE.xmlName());
        }

        try {
            return syntax.fromString(valueStr);
        } catch (ConstraintViolatedException e) {
            throw err.at(node).error(e, StringUtil.quoteMessageFormat(e.getMessageWithoutValue()));
        } catch (IllegalArgumentException e) {
            throw err.at(node).error(e);
        }
    }
}
