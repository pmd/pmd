package net.sourceforge.pmd;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * This class represents a way to serialize a RuleSet to an XML configuration file.
 */
public class RuleSetWriter {
	private final OutputStream outputStream;
	private Document document;
	private Set<String> ruleSetFileNames;

	public RuleSetWriter(OutputStream outputStream) {
		this.outputStream = outputStream;
	}

	public void close() throws IOException {
		outputStream.flush();
		outputStream.close();
	}

	public void write(RuleSet ruleSet) {
		try {
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			this.document = documentBuilder.newDocument();
			this.ruleSetFileNames = new HashSet<String>();

			Element ruleSetElement = createRuleSetElement(ruleSet);
			document.appendChild(ruleSetElement);

			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			// This is as close to pretty printing as we'll get using standard Java APIs.
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "3");
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
		Element ruleSetElement = document.createElement("ruleset");
		ruleSetElement.setAttribute("xmlns", "http://pmd.sf.net/ruleset/1.0.0");
		ruleSetElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:schemaLocation",
				"http://pmd.sf.net/ruleset/1.0.0 http://pmd.sf.net/ruleset_xml_schema.xsd");
		ruleSetElement.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:noNamespaceSchemaLocation",
				"http://pmd.sf.net/ruleset_xml_schema.xsd");
		ruleSetElement.setAttribute("name", ruleSet.getName());

		if (ruleSet.getLanguage() != null) {
			ruleSetElement.setAttribute("language", ruleSet.getLanguage().getName());
		}

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

	private Element createRuleElement(Rule rule) {
		if (rule instanceof RuleReference) {
			RuleReference ruleReference = (RuleReference)rule;
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
				String name = ruleReference.getOverriddenName();
				String ref = ruleReference.getRuleSetReference().getRuleSetFileName() + "/" + ruleReference.getName();
				String message = ruleReference.getOverriddenMessage();
				String externalInfoUrl = ruleReference.getOverriddenExternalInfoUrl();
				String description = ruleReference.getOverriddenDescription();
				Integer priority = ruleReference.getOverriddenPriority();
				Properties properties = ruleReference.getOverriddenProperties();
				List<String> examples = ruleReference.getOverriddenExamples();
				return createSingleRuleElement(name, null, ref, message, externalInfoUrl, null, null, null,
						description, priority, properties, examples);
			}
		} else {
			return createSingleRuleElement(rule.getName(), rule.getSince(), null, rule.getMessage(),
					rule.getExternalInfoUrl(), rule.getRuleClass(), rule.usesDFA(), rule.usesTypeResolution(),
					rule.getDescription(), rule.getPriority(), rule.getProperties(), rule.getExamples());
		}
	}

	private Element createSingleRuleElement(String name, String since, String ref, String message,
			String externalInfoUrl, String clazz, Boolean dfa, Boolean typeResolution, String description,
			Integer priority, Properties properties, List<String> examples) {
		Element ruleElement = document.createElement("rule");
		if (name != null) {
			ruleElement.setAttribute("name", name);
		}
		if (since != null) {
			ruleElement.setAttribute("since", since);
		}
		if (ref != null) {
			ruleElement.setAttribute("ref", ref);
		}
		if (message != null) {
			ruleElement.setAttribute("message", message);
		}
		if (externalInfoUrl != null) {
			ruleElement.setAttribute("externalInfoUrl", externalInfoUrl);
		}
		if (clazz != null) {
			ruleElement.setAttribute("class", clazz);
		}
		if (dfa != null) {
			ruleElement.setAttribute("dfa", dfa.toString());
		}
		if (typeResolution != null) {
			ruleElement.setAttribute("typeResolution", typeResolution.toString());
		}

		if (description != null) {
			Element descriptionElement = createDescriptionElement(description);
			ruleElement.appendChild(descriptionElement);
		}
		if (priority != null) {
			Element priorityElement = createPriorityElement(priority);
			ruleElement.appendChild(priorityElement);
		}
		if (properties != null) {
			Element propertiesElement = createPropertiesElement(properties);
			if (propertiesElement != null) {
				ruleElement.appendChild(propertiesElement);
			}
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
		Element ruleSetReferenceElement = document.createElement("rule");
		ruleSetReferenceElement.setAttribute("ref", ruleSetReference.getRuleSetFileName());
		for (String exclude : ruleSetReference.getExcludes()) {
			Element excludeElement = createExcludeElement(exclude);
			ruleSetReferenceElement.appendChild(excludeElement);
		}
		return ruleSetReferenceElement;
	}

	private Element createExcludeElement(String exclude) {
		return createTextElement("exclude", exclude);
	}

	private Element createExampleElement(String example) {
		return createCDATASectionElement("example", example);
	}

	private Element createPriorityElement(Integer priority) {
		return createTextElement("priority", priority.toString());
	}

	private Element createPropertiesElement(Properties properties) {
		if (properties != null && !properties.isEmpty()) {
			Element propertiesElement = document.createElement("properties");
			for (Map.Entry<Object, Object> entry : properties.entrySet()) {
				Element propertyElement = createPropertyElement(properties, (String)entry.getKey(),
						(String)entry.getValue());
				if (propertyElement != null) {
					propertiesElement.appendChild(propertyElement);
				}
			}
			return propertiesElement;
		} else {
			return null;
		}
	}

	private Element createPropertyElement(Properties properties, String key, String value) {
		Element propertyElement = document.createElement("property");
		propertyElement.setAttribute("name", key);
		if ("xpath".equals(key)) {
			if (properties.containsKey("pluginname")) {
				propertyElement.setAttribute("pluginname", properties.getProperty("pluginname"));
			}
			Element valueElement = createCDATASectionElement("value", value);
			propertyElement.appendChild(valueElement);
		} else if ("pluginname".equals(key)) {
			if (properties.containsKey("xpath")) {
				return null;
			} else {
				propertyElement.setAttribute("value", value);
			}
		} else {
			propertyElement.setAttribute("value", value);
		}

		return propertyElement;
	}

	private Element createTextElement(String name, String value) {
		Element element = document.createElement(name);
		Text text = document.createTextNode(value);
		element.appendChild(text);
		return element;
	}

	private Element createCDATASectionElement(String name, String value) {
		Element element = document.createElement(name);
		CDATASection cdataSection = document.createCDATASection(value);
		element.appendChild(cdataSection);
		return element;
	}
}
