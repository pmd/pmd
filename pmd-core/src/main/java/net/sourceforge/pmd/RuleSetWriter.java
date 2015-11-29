/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.rule.ImmutableLanguage;
import net.sourceforge.pmd.lang.rule.RuleReference;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.rule.properties.PropertyDescriptorWrapper;
import net.sourceforge.pmd.lang.rule.properties.factories.PropertyDescriptorUtil;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * This class represents a way to serialize a RuleSet to an XML configuration file.
 */
public class RuleSetWriter {
	
    public static final String RULESET_NS_URI = "http://pmd.sourceforge.net/ruleset/2.0.0";
    
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
		    		//ignore it, specific to one parser
		    	}
		    Transformer transformer = transformerFactory.newTransformer();
		    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		    // This is as close to pretty printing as we'll get using standard Java APIs.
		    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		    transformer.transform(new DOMSource(document), new StreamResult(outputStream));
		} catch (DOMException e) {
		    throw new RuntimeException(e);
		} catch (FactoryConfigurationError e) {
		    throw new RuntimeException(e);
		} catch (ParserConfigurationException e) {
		    throw new RuntimeException(e);
		} catch (TransformerException e) {
		    throw new RuntimeException(e);
		}
    }

    private Element createRuleSetElement(RuleSet ruleSet) {
    	Element ruleSetElement = document.createElementNS(RULESET_NS_URI, "ruleset");
    	ruleSetElement.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		ruleSetElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation", RULESET_NS_URI + " http://pmd.sourceforge.net/ruleset_2_0_0.xsd");
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

    private Element createExcludePatternElement(String excludePattern) {
    	return createTextElement("exclude-pattern", excludePattern);
    }

    private Element createIncludePatternElement(String includePattern) {
    	return createTextElement("include-pattern", includePattern);
    }
    
    private Element createRuleElement() {
    	return document.createElementNS(RULESET_NS_URI, "rule");
    }
    
    private Element createExcludeElement(String exclude) {
        Element element = document.createElementNS(RULESET_NS_URI, "exclude");
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
    	return document.createElementNS(RULESET_NS_URI, "properties");
    }
    
    private Element createRuleElement(Rule rule) {
		if (rule instanceof RuleReference) {
		    RuleReference ruleReference = (RuleReference) rule;
		    RuleSetReference ruleSetReference = ruleReference.getRuleSetReference();
		    if (ruleSetReference.isAllRules()) {
			if (!ruleSetFileNames.contains(ruleSetReference.getRuleSetFileName())) {
			    ruleSetFileNames.add(ruleSetReference.getRuleSetFileName());
			    Element ruleSetReferenceElement = createRuleSetReferenceElement(ruleSetReference);
			    return ruleSetReferenceElement;
			} else {
			    return null;
			}
		    } else {
			Language language = ruleReference.getOverriddenLanguage();
			LanguageVersion minimumLanguageVersion = ruleReference.getOverriddenMinimumLanguageVersion();
			LanguageVersion maximumLanguageVersion = ruleReference.getOverriddenMaximumLanguageVersion();
			Boolean deprecated = ruleReference.isOverriddenDeprecated();
			String name = ruleReference.getOverriddenName();
			String ref = ruleReference.getRuleSetReference().getRuleSetFileName() + "/" + ruleReference.getRule().getName();
			String message = ruleReference.getOverriddenMessage();
			String externalInfoUrl = ruleReference.getOverriddenExternalInfoUrl();
			String description = ruleReference.getOverriddenDescription();
			RulePriority priority = ruleReference.getOverriddenPriority();
			List<PropertyDescriptor<?>> propertyDescriptors = ruleReference.getOverriddenPropertyDescriptors();
			Map<PropertyDescriptor<?>, Object> propertiesByPropertyDescriptor = ruleReference.getOverriddenPropertiesByPropertyDescriptor();
			List<String> examples = ruleReference.getOverriddenExamples();
			
			return createSingleRuleElement(language, minimumLanguageVersion, maximumLanguageVersion, deprecated,
				name, null, ref, message, externalInfoUrl, null, null, null, description, priority,
				propertyDescriptors, propertiesByPropertyDescriptor, examples);
		    }
		} else {
		    return createSingleRuleElement(rule instanceof ImmutableLanguage ? null : rule.getLanguage(), 
		    	rule.getMinimumLanguageVersion(), rule.getMaximumLanguageVersion(), rule.isDeprecated(),
			    rule.getName(), rule.getSince(), null, rule.getMessage(), rule.getExternalInfoUrl(),
			    rule.getRuleClass(), rule.usesDFA(), rule.usesTypeResolution(), rule.getDescription(), 
			    rule.getPriority(), rule.getPropertyDescriptors(), rule.getPropertiesByPropertyDescriptor(),
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
	    String message, String externalInfoUrl, String clazz, Boolean dfa, Boolean typeResolution,
	    String description, RulePriority priority, List<PropertyDescriptor<?>> propertyDescriptors,
	    Map<PropertyDescriptor<?>, Object> propertiesByPropertyDescriptor, List<String> examples) {
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
		
		setIfNonNull(deprecated, 	  ruleElement, 	"deprecated");
		setIfNonNull(name, 			  ruleElement, 	"name");
		setIfNonNull(since, 		  ruleElement, 	"since");
		setIfNonNull(ref, 			  ruleElement,	"ref");
		setIfNonNull(message, 		  ruleElement, 	"message");
		setIfNonNull(clazz, 		  ruleElement, 	"class");
		setIfNonNull(externalInfoUrl, ruleElement,  "externalInfoUrl");
		setIfNonNull(dfa, 			  ruleElement,  "dfa");
		setIfNonNull(typeResolution,  ruleElement,  "typeResolution");
	
		if (description != null) {
		    Element descriptionElement = createDescriptionElement(description);
		    ruleElement.appendChild(descriptionElement);
		}
		if (priority != null) {
		    Element priorityElement = createPriorityElement(priority);
		    ruleElement.appendChild(priorityElement);
		}
		Element propertiesElement = createPropertiesElement(propertyDescriptors, propertiesByPropertyDescriptor);
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
    
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private Element createPropertiesElement(List<PropertyDescriptor<?>> propertyDescriptors,  Map<PropertyDescriptor<?>, Object> propertiesByPropertyDescriptor) {

		Element propertiesElement = null;
		if (propertyDescriptors != null) {
		    
		    for (PropertyDescriptor<?> propertyDescriptor : propertyDescriptors) {		// For each provided PropertyDescriptor
			
			if (propertyDescriptor instanceof PropertyDescriptorWrapper) {				// Any wrapper property needs to go out as a definition.
			    if (propertiesElement == null) {
			    	propertiesElement = createPropertiesElement();
			    }
			    
			    Element propertyElement = createPropertyDefinitionElementBR(((PropertyDescriptorWrapper<?>) propertyDescriptor).getPropertyDescriptor());
			    propertiesElement.appendChild(propertyElement);
			} else {			    
			    if (propertiesByPropertyDescriptor != null) {		// Otherwise, any property which has a value different than the default needs to go out as a value.
				Object defaultValue = propertyDescriptor.defaultValue();
				Object value = propertiesByPropertyDescriptor.get(propertyDescriptor);
				if (value != defaultValue && (value == null || !value.equals(defaultValue))) {
				    if (propertiesElement == null) {
				    	propertiesElement = createPropertiesElement();
				    }
				    
				    Element propertyElement = createPropertyValueElement(propertyDescriptor, value);
				    propertiesElement.appendChild(propertyElement);
					}
			    }
			}
		}
	}

	if (propertiesByPropertyDescriptor != null) {
	    // Then, for each PropertyDescriptor not explicitly provided
	    for (Map.Entry<PropertyDescriptor<?>, Object> entry : propertiesByPropertyDescriptor.entrySet()) {
		// If not explicitly given...
		PropertyDescriptor<?> propertyDescriptor = entry.getKey();
		if (!propertyDescriptors.contains(propertyDescriptor)) {
		    // Otherwise, any property which has a value different than the
		    // default needs to go out as a value.
		    Object defaultValue = propertyDescriptor.defaultValue();
		    Object value = entry.getValue();
		    if (value != defaultValue && (value == null || !value.equals(defaultValue))) {
			if (propertiesElement == null) {
			    propertiesElement = createPropertiesElement();
			}
			Element propertyElement = createPropertyValueElement(propertyDescriptor, value);
			propertiesElement.appendChild(propertyElement);
		    }
		}
	    }
	}
	return propertiesElement;
    }

    private Element createPropertyValueElement(PropertyDescriptor propertyDescriptor, Object value) {
		Element propertyElement = document.createElementNS(RULESET_NS_URI, "property");
		propertyElement.setAttribute("name", propertyDescriptor.name());
		String valueString = propertyDescriptor.asDelimitedString(value);
		if (XPathRule.XPATH_DESCRIPTOR.equals(propertyDescriptor)) {
		    Element valueElement = createCDATASectionElement("value", valueString);
		    propertyElement.appendChild(valueElement);
		} else {
		    propertyElement.setAttribute("value", valueString);
		}
	
		return propertyElement;
    }

//	private Element createPropertyDefinitionElement(PropertyDescriptor<?> propertyDescriptor) {
//		Element propertyElement = createPropertyValueElement(propertyDescriptor, propertyDescriptor.defaultValue());
//		
//		propertyElement.setAttribute("description", propertyDescriptor.description());
//		String type = PropertyDescriptorFactory.getPropertyDescriptorType(propertyDescriptor);
//		propertyElement.setAttribute("type", type);
//		
//		if (propertyDescriptor.isMultiValue()) {
//			propertyElement.setAttribute("delimiter", String.valueOf(propertyDescriptor.multiValueDelimiter()));
//		}
//		
//		if (propertyDescriptor instanceof AbstractNumericProperty) {
//			propertyElement.setAttribute("min", String.valueOf(((AbstractNumericProperty<?>) propertyDescriptor).lowerLimit()));
//			propertyElement.setAttribute("max", String.valueOf(((AbstractNumericProperty<?>) propertyDescriptor).upperLimit()));
//		}
//
//		return propertyElement;
//    }
	
	private Element createPropertyDefinitionElementBR(PropertyDescriptor<?> propertyDescriptor) {
		
		final Element propertyElement = createPropertyValueElement(propertyDescriptor, propertyDescriptor.defaultValue());
		propertyElement.setAttribute(PropertyDescriptorFields.TYPE, PropertyDescriptorUtil.typeIdFor(propertyDescriptor.type()));
		
		Map<String, String> propertyValuesById = propertyDescriptor.attributeValuesById();
		for (Map.Entry<String, String> entry : propertyValuesById.entrySet()) {
			propertyElement.setAttribute(entry.getKey(), entry.getValue());
		}
		
		return propertyElement;
    }

    private Element createTextElement(String name, String value) {
		Element element = document.createElementNS(RULESET_NS_URI, name);
		Text text = document.createTextNode(value);
		element.appendChild(text);
		return element;
    }

    private Element createCDATASectionElement(String name, String value) {
		Element element = document.createElementNS(RULESET_NS_URI, name);
		CDATASection cdataSection = document.createCDATASection(value);
		element.appendChild(cdataSection);
		return element;
    }
}
