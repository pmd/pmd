/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyDescriptorField;
import net.sourceforge.pmd.properties.PropertySource;
import net.sourceforge.pmd.properties.PropertyTypeId;
import net.sourceforge.pmd.properties.xml.XmlSyntax;

/**
 * This class represents a way to serialize a RuleSet to an XML configuration
 * file.
 */
public class RuleSetWriter {
    private static final Logger LOG = Logger.getLogger(RuleSetWriter.class.getName());

    public static final String RULESET_2_0_0_NS_URI = "http://pmd.sourceforge.net/ruleset/2.0.0";

    /**
     * @deprecated use {@link #RULESET_2_0_0_NS_URI} instead
     */
    @Deprecated // To be removed in PMD 7.0.0
    public static final String RULESET_NS_URI = RULESET_2_0_0_NS_URI;

    private final OutputStream outputStream;
    private Document document;
    private Set<String> ruleSetFileNames;

    public RuleSetWriter(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void close() {
        IOUtils.closeQuietly(outputStream);
    }

    public void write(RuleSet ruleSet) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.newDocument();
            ruleSetFileNames = new HashSet<>();

            Element ruleSetElement = createRuleSetElement(ruleSet);
            document.appendChild(ruleSetElement);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            try {
                transformerFactory.setAttribute("indent-number", 3);
            } catch (IllegalArgumentException iae) {
                // ignore it, specific to one parser
                LOG.log(Level.FINE, "Couldn't set indentation", iae);
            }
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            // This is as close to pretty printing as we'll get using standard
            // Java APIs.
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(new DOMSource(document), new StreamResult(outputStream));
        } catch (DOMException | FactoryConfigurationError | ParserConfigurationException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    private Element createRuleSetElement(RuleSet ruleSet) {
        Element ruleSetElement = document.createElementNS(RULESET_2_0_0_NS_URI, "ruleset");
        ruleSetElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        ruleSetElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation",
                RULESET_2_0_0_NS_URI + " https://pmd.sourceforge.io/ruleset_2_0_0.xsd");
        ruleSetElement.setAttribute("name", ruleSet.getName());

        Element descriptionElement = createDescriptionElement(ruleSet.getDescription());
        ruleSetElement.appendChild(descriptionElement);

        for (String excludePattern : ruleSet.getExcludePatterns()) {
            Element excludePatternElement = createExcludePatternElement(excludePattern);
            ruleSetElement.appendChild(excludePatternElement);
        }
        for (String includePattern : ruleSet.getIncludePatterns()) {
            Element includePatternElement = createIncludePatternElement(includePattern);
            ruleSetElement.appendChild(includePatternElement);
        }
        for (Rule rule : ruleSet.getRules()) {
            Element ruleElement = createRuleElement(rule);
            if (ruleElement != null) {
                ruleSetElement.appendChild(ruleElement);
            }
        }

        return ruleSetElement;
    }

    private Element createDescriptionElement(String description) {
        return createTextElement("description", description);
    }

    private Element createPropertyValueElement(String name) {
        return document.createElementNS(RULESET_2_0_0_NS_URI, name);
    }

    private Element createPropertyDefaultElement() {
        return document.createElementNS(RULESET_2_0_0_NS_URI, "default");
    }

    private Element createExcludePatternElement(String excludePattern) {
        return createTextElement("exclude-pattern", excludePattern);
    }

    private Element createIncludePatternElement(String includePattern) {
        return createTextElement("include-pattern", includePattern);
    }

    private Element createRuleElement() {
        return document.createElementNS(RULESET_2_0_0_NS_URI, "rule");
    }

    private Element createExcludeElement(String exclude) {
        Element element = document.createElementNS(RULESET_2_0_0_NS_URI, "exclude");
        element.setAttribute("name", exclude);
        return element;
    }

    private Element createExampleElement(String example) {
        return createCDATASectionElement("example", example);
    }

    private Element createPriorityElement(RulePriority priority) {
        return createTextElement("priority", String.valueOf(priority.getPriority()));
    }

    private Element createPropertiesElement() {
        return document.createElementNS(RULESET_2_0_0_NS_URI, "properties");
    }

    private Element createRuleElement(Rule rule) {
        if (rule instanceof RuleReference) {
            RuleReference ruleReference = (RuleReference) rule;
            RuleSetReference ruleSetReference = ruleReference.getRuleSetReference();
            if (ruleSetReference.isAllRules()) {
                if (!ruleSetFileNames.contains(ruleSetReference.getRuleSetFileName())) {
                    ruleSetFileNames.add(ruleSetReference.getRuleSetFileName());
                    return createRuleSetReferenceElement(ruleSetReference);
                } else {
                    return null;
                }
            } else {
                Language language = ruleReference.getOverriddenLanguage();
                LanguageVersion minimumLanguageVersion = ruleReference.getOverriddenMinimumLanguageVersion();
                LanguageVersion maximumLanguageVersion = ruleReference.getOverriddenMaximumLanguageVersion();
                Boolean deprecated = ruleReference.isOverriddenDeprecated();
                String name = ruleReference.getOverriddenName();
                String ref = ruleReference.getRuleSetReference().getRuleSetFileName() + '/'
                        + ruleReference.getRule().getName();
                String message = ruleReference.getOverriddenMessage();
                String externalInfoUrl = ruleReference.getOverriddenExternalInfoUrl();
                String description = ruleReference.getOverriddenDescription();
                RulePriority priority = ruleReference.getOverriddenPriority();
                List<String> examples = ruleReference.getOverriddenExamples();

                return createSingleRuleElement(language, minimumLanguageVersion, maximumLanguageVersion, deprecated,
                                               name, null, ref, message, externalInfoUrl, null, description, priority,
                                               ruleReference, examples);
            }
        } else {
            return createSingleRuleElement(rule instanceof ImmutableLanguage ? null : rule.getLanguage(),
                                           rule.getMinimumLanguageVersion(), rule.getMaximumLanguageVersion(), rule.isDeprecated(),
                                           rule.getName(), rule.getSince(), null, rule.getMessage(), rule.getExternalInfoUrl(),
                                           rule.getRuleClass(),
                                           rule.getDescription(),
                                           rule.getPriority(), rule,
                                           rule.getExamples());
        }
    }

    private void setIfNonNull(Object value, Element target, String id) {
        if (value != null) {
            target.setAttribute(id, value.toString());
        }
    }

    private Element createSingleRuleElement(Language language, LanguageVersion minimumLanguageVersion,
                                            LanguageVersion maximumLanguageVersion, Boolean deprecated, String name, String since, String ref,
                                            String message, String externalInfoUrl, String clazz,
                                            String description, RulePriority priority, PropertySource propertySource, List<String> examples) {
        Element ruleElement = createRuleElement();
        if (language != null) {
            ruleElement.setAttribute("language", language.getTerseName());
        }
        if (minimumLanguageVersion != null) {
            ruleElement.setAttribute("minimumLanguageVersion", minimumLanguageVersion.getVersion());
        }
        if (maximumLanguageVersion != null) {
            ruleElement.setAttribute("maximumLanguageVersion", maximumLanguageVersion.getVersion());
        }

        setIfNonNull(deprecated, ruleElement, "deprecated");
        setIfNonNull(name, ruleElement, "name");
        setIfNonNull(since, ruleElement, "since");
        setIfNonNull(ref, ruleElement, "ref");
        setIfNonNull(message, ruleElement, "message");
        setIfNonNull(clazz, ruleElement, "class");
        setIfNonNull(externalInfoUrl, ruleElement, "externalInfoUrl");

        if (description != null) {
            Element descriptionElement = createDescriptionElement(description);
            ruleElement.appendChild(descriptionElement);
        }
        if (priority != null) {
            Element priorityElement = createPriorityElement(priority);
            ruleElement.appendChild(priorityElement);
        }
        Element propertiesElement = createPropertiesElement(propertySource);
        if (propertiesElement != null) {
            ruleElement.appendChild(propertiesElement);
        }
        if (examples != null) {
            for (String example : examples) {
                Element exampleElement = createExampleElement(example);
                ruleElement.appendChild(exampleElement);
            }
        }
        return ruleElement;
    }

    private Element createRuleSetReferenceElement(RuleSetReference ruleSetReference) {
        Element ruleSetReferenceElement = createRuleElement();
        ruleSetReferenceElement.setAttribute("ref", ruleSetReference.getRuleSetFileName());
        for (String exclude : ruleSetReference.getExcludes()) {
            Element excludeElement = createExcludeElement(exclude);
            ruleSetReferenceElement.appendChild(excludeElement);
        }
        return ruleSetReferenceElement;
    }

    @Nullable
    private Element createPropertiesElement(PropertySource propertySource) {

        Element propertiesElement = null;
        List<PropertyDescriptor<?>> overridden = propertySource.getOverriddenPropertyDescriptors();
        List<PropertyDescriptor<?>> defined = propertySource.getPropertyDescriptors();

        for (PropertyDescriptor<?> descriptor : defined) {
            // For each provided PropertyDescriptor

            PropertyTypeId typeId = descriptor.getTypeId();

            if (typeId == null // not defined externally
                && !overridden.contains(descriptor)) {
                continue;
            }

            if (propertiesElement == null) {
                propertiesElement = createPropertiesElement();
            }

            if (typeId != null) {
                propertiesElement.appendChild(createPropertyDefinitionElementBR(descriptor, typeId));
            } else {
                propertiesElement.appendChild(propertyElementWithValue(propertySource, descriptor));
            }
        }

        return propertiesElement;
    }

    @NonNull
    private <T> Element propertyElementWithValue(PropertySource propertySource, PropertyDescriptor<T> descriptor) {
        return createPropertyValueElement(descriptor, propertySource.getProperty(descriptor));
    }

    private <T> Element createPropertyValueElement(PropertyDescriptor<T> propertyDescriptor, T value) {
        Element element = document.createElementNS(RULESET_2_0_0_NS_URI, "property");
        PropertyDescriptorField.NAME.setOn(element, propertyDescriptor.name());

        XmlSyntax<T> xmlStrategy = propertyDescriptor.xmlStrategy();

        Element valueElt = createPropertyValueElement(xmlStrategy.getWriteElementName(value));
        xmlStrategy.toXml(valueElt, value);
        element.appendChild(valueElt);

        return element;
    }

    private <T> Element createPropertyDefinitionElementBR(PropertyDescriptor<T> propertyDescriptor, @NonNull PropertyTypeId typeId) {

        final Element element = createPropertyValueElement(propertyDescriptor, propertyDescriptor.defaultValue());

        PropertyDescriptorField.NAME.setOn(element, propertyDescriptor.name());
        PropertyDescriptorField.TYPE.setOn(element, typeId.getStringId());
        PropertyDescriptorField.DESCRIPTION.setOn(element, propertyDescriptor.description());
        // TODO support property constraints in XML
        return element;
    }

    private Element createTextElement(String name, String value) {
        Element element = document.createElementNS(RULESET_2_0_0_NS_URI, name);
        Text text = document.createTextNode(value);
        element.appendChild(text);
        return element;
    }

    private Element createCDATASectionElement(String name, String value) {
        Element element = document.createElementNS(RULESET_2_0_0_NS_URI, name);
        CDATASection cdataSection = document.createCDATASection(value);
        element.appendChild(cdataSection);
        return element;
    }
}
