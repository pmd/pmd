package net.sourceforge.pmd.jdeveloper;

import com.sun.org.apache.xml.internal.serialize.DOMSerializer;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.IOException;
import java.io.OutputStream;

import java.util.Enumeration;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;


/**
 * Generate an XML rule set file from a rule set
 * This class is a rewritting of the original from PMD engine
 * that doesn't support xpath properties !
 * 
 * @author Philippe Herlin
 * @version $Revision: 2434 $
 */
public class RuleSetWriterImpl {

    /**
     * Write a ruleset as an XML stream
     * @param outputStream the output writer
     * @param ruleSet the ruleset to serialize
     */
    public void write(OutputStream outputStream, RuleSet ruleSet) {
        try {
            final DocumentBuilderFactory factory = 
                DocumentBuilderFactory.newInstance();
            final DocumentBuilder documentBuilder = 
                factory.newDocumentBuilder();
            final Document doc = documentBuilder.newDocument();

            final Element ruleSetElement = getRuleSetElement(doc, ruleSet);
            doc.appendChild(ruleSetElement);

            final OutputFormat outputFormat = 
                new OutputFormat(doc, "UTF-8", true);
            outputFormat.setLineWidth(0);

            final DOMSerializer serializer = 
                new XMLSerializer(outputStream, outputFormat);
            serializer.serialize(doc);

        } catch (DOMException e) {
            System.err.println("Error during XML parsing : " + e.getMessage());
        } catch (FactoryConfigurationError e) {
            System.err.println("Error during factory configuration : " + 
                               e.getMessage());
        } catch (ParserConfigurationException e) {
            System.err.println("Error during parser configuration : " + 
                               e.getMessage());
        } catch (IOException e) {
            System.err.println("Error during file transfer : " + 
                               e.getMessage());
        }
    }

    /**
     * Create an element from a ruleset
     * @param doc the generated doc
     * @param ruleSet a ruleset
     * @return a ruleset element
     */
    private Element getRuleSetElement(Document doc, RuleSet ruleSet) {
        final Element ruleSetElement = doc.createElement("ruleset");
        ruleSetElement.setAttribute("name", ruleSet.getName());

        final Element descriptionElement = 
            getDescriptionElement(doc, ruleSet.getDescription());
        ruleSetElement.appendChild(descriptionElement);

        for (Rule rule: ruleSet.getRules()) {
            final Element ruleElement = getRuleElement(doc, rule);
            ruleSetElement.appendChild(ruleElement);
        }

        return ruleSetElement;
    }

    /**
     * Get an element from a description
     * @param doc the generated doc
     * @param description a textual description
     * @return a description element
     */
    private Element getDescriptionElement(Document doc, String description) {
        final Element descriptionElement = doc.createElement("description");
        final Text text = doc.createTextNode(description);
        descriptionElement.appendChild(text);
        return descriptionElement;
    }

    /**
     * Get an element from a rule
     * @param doc the generated doc
     * @param rule a rule
     * @return a rule element
     */
    private Element getRuleElement(Document doc, Rule rule) {
        final Element ruleElement = doc.createElement("rule");
        ruleElement.setAttribute("name", rule.getName());
        ruleElement.setAttribute("message", rule.getMessage());

        final String className = rule.getClass().getName();
        ruleElement.setAttribute("class", className);

        if (rule.usesDFA()) {
            ruleElement.setAttribute("dfa", "true");
        }
        if (rule.include()) {
            ruleElement.setAttribute("include", "true");
        }

        final Element descriptionElement = 
            getDescriptionElement(doc, rule.getDescription());
        ruleElement.appendChild(descriptionElement);

        if (rule.getExamples().size() > 0) {
            final Element exampleElement = 
                getExampleElement(doc, rule.getExamples().get(0).toString());
            ruleElement.appendChild(exampleElement);
        }

        final Element priorityElement = 
            getPriorityElement(doc, rule.getPriority());
        ruleElement.appendChild(priorityElement);

        final Element propertiesElement = 
            getPropertiesElement(doc, rule.getProperties());
        ruleElement.appendChild(propertiesElement);

        return ruleElement;
    }

    /**
     * Get an element from an example
     * @param doc the generated doc
     * @param example a rule example
     * @return an example element
     */
    private Element getExampleElement(Document doc, String example) {
        final Element exampleElement = doc.createElement("example");
        final CDATASection cdataSection = doc.createCDATASection(example);
        exampleElement.appendChild(cdataSection);
        return exampleElement;
    }

    /**
     * Get an element from a rule priority
     * @param doc the generated doc
     * @param priority a priotity
     * @return a priority element
     */
    private Element getPriorityElement(Document doc, int priority) {
        final Element priorityElement = doc.createElement("priority");
        final Text text = doc.createTextNode(String.valueOf(priority));
        priorityElement.appendChild(text);
        return priorityElement;
    }

    /**
     * Get an element from rule properties
     * @param doc the genetated doc
     * @param properties rule properties
     * @return a properties element
     */
    private Element getPropertiesElement(Document doc, Properties properties) {
        final Element propertiesElement = doc.createElement("properties");
        final Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            final Object key = keys.nextElement();
            final Element propertyElement = 
                getPropertyElement(doc, (String)key, 
                                   (String)properties.get(key));
            propertiesElement.appendChild(propertyElement);
        }
        return propertiesElement;
    }

    /**
     * Get an element from a rule property
     * @param doc the generated doc
     * @param key a property key
     * @param value a property value
     * @return a property element
     */
    private Element getPropertyElement(Document doc, String key, 
                                       String value) {
        final Element propertyElement = doc.createElement("property");
        propertyElement.setAttribute("name", key);
        if ("xpath".equals(key)) {
            final Element valueElement = doc.createElement("value");
            final CDATASection cdataSection = doc.createCDATASection(value);
            valueElement.appendChild(cdataSection);
            propertyElement.appendChild(valueElement);
        } else {
            propertyElement.setAttribute("value", value);
        }

        return propertyElement;
    }
}
