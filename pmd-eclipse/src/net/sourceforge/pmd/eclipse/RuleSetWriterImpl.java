package net.sourceforge.pmd.eclipse;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.serialize.DOMSerializer;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
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
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.4  2005/01/16 22:53:03  phherlin
 * Upgrade to PMD 2.1: take into account new rules attributes symboltable and dfa
 *
 * Revision 1.3  2003/12/18 23:58:37  phherlin
 * Fixing malformed UTF-8 characters in generated xml files
 *
 * Revision 1.2  2003/11/30 22:57:43  phherlin
 * Merging from eclipse-v2 development branch
 *
 * Revision 1.1.2.1  2003/11/07 14:31:48  phherlin
 * Reverse to identation usage, remove dummy text nodes
 *
 * Revision 1.1  2003/10/16 22:26:37  phherlin
 * Fix bug #810858.
 * Complete refactoring of rule set generation. Using a DOM tree and the Xerces 2 serializer.
 *
 * 
 * -- Renaming to RuleSetWriterImpl --
 * 
 * Revision 1.2  2003/10/14 21:26:32  phherlin
 * Upgrading to PMD 1.2.2
 *
 * Revision 1.1  2003/06/30 20:16:06  phherlin
 * Redesigning plugin configuration
 *
 *
 */
public class RuleSetWriterImpl implements RuleSetWriter {
    private static Log log = LogFactory.getLog("net.sourceforge.pmd.eclipse.RuleSetWriterImpl");

    /**
     * Write a ruleset as an XML stream
     * @param writer the output writer
     * @param ruleSet the ruleset to serialize
     */
    public void write(OutputStream outputStream, RuleSet ruleSet) throws PMDEclipseException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            Document doc = documentBuilder.newDocument();

            Element ruleSetElement = getRuleSetElement(doc, ruleSet);
            doc.appendChild(ruleSetElement);

            OutputFormat outputFormat = new OutputFormat(doc, "UTF-8", true);
            outputFormat.setLineWidth(0);
            DOMSerializer serializer = new XMLSerializer(outputStream, outputFormat);
            serializer.serialize(doc);

        } catch (DOMException e) {
            throw new PMDEclipseException(e);
        } catch (FactoryConfigurationError e) {
            throw new PMDEclipseException(e);
        } catch (ParserConfigurationException e) {
            throw new PMDEclipseException(e);
        } catch (IOException e) {
            throw new PMDEclipseException(e);
        }
    }

    /**
     * Create an element from a ruleset
     * @param doc the generated doc
     * @param ruleSet a ruleset
     * @return a ruleset element
     */
    private Element getRuleSetElement(Document doc, RuleSet ruleSet) {
        Element ruleSetElement = doc.createElement("ruleset");
        ruleSetElement.setAttribute("name", ruleSet.getName());
        ruleSetElement.setAttribute("include", ruleSet.include() ? "true" : "false");

        Element descriptionElement = getDescriptionElement(doc, ruleSet.getDescription());
        ruleSetElement.appendChild(descriptionElement);

        Iterator rules = ruleSet.getRules().iterator();
        while (rules.hasNext()) {
            Rule rule = (Rule) rules.next();
            log.debug("Serializing rule " + rule.getName());
            Element ruleElement = getRuleElement(doc, rule);
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
        Element descriptionElement = doc.createElement("description");
        Text text = doc.createTextNode(description);
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
        Element ruleElement = doc.createElement("rule");
        ruleElement.setAttribute("name", rule.getName());
        ruleElement.setAttribute("message", rule.getMessage());
        ruleElement.setAttribute("class", rule.getClass().getName());
        if (rule.usesSymbolTable()) {
            ruleElement.setAttribute("symboltable", "true");
        }
        if (rule.usesDFA()) {
            ruleElement.setAttribute("dfa", "true");
        }
        if (rule.include()) {
            ruleElement.setAttribute("include", "true");
        }

        Element descriptionElement = getDescriptionElement(doc, rule.getDescription());
        ruleElement.appendChild(descriptionElement);

        Element exampleElement = getExampleElement(doc, rule.getExample());
        ruleElement.appendChild(exampleElement);

        Element priorityElement = getPriorityElement(doc, rule.getPriority());
        ruleElement.appendChild(priorityElement);

        Element propertiesElement = getPropertiesElement(doc, rule.getProperties());
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
        Element exampleElement = doc.createElement("example");
        CDATASection cdataSection = doc.createCDATASection(example);
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
        Element priorityElement = doc.createElement("priority");
        Text text = doc.createTextNode(String.valueOf(priority));
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
        Element propertiesElement = doc.createElement("properties");
        Enumeration keys = properties.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Element propertyElement = getPropertyElement(doc, (String) key, (String) properties.get(key));
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
    private Element getPropertyElement(Document doc, String key, String value) {
        Element propertyElement = doc.createElement("property");
        propertyElement.setAttribute("name", key);
        if (key.equals("xpath")) {
            Element valueElement = doc.createElement("value");
            CDATASection cdataSection = doc.createCDATASection(value);
            valueElement.appendChild(cdataSection);
            propertyElement.appendChild(valueElement);
        } else {
            propertyElement.setAttribute("value", value);
        }

        return propertyElement;
    }

}
