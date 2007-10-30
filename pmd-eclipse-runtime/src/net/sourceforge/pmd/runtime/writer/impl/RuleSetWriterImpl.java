package net.sourceforge.pmd.runtime.writer.impl;

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
import net.sourceforge.pmd.runtime.writer.IRuleSetWriter;
import net.sourceforge.pmd.runtime.writer.WriterException;

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
 * Revision 1.5  2007/06/24 16:41:31  phherlin
 * Integrate PMD v4.0rc1
 *
 * Revision 1.4  2007/06/24 15:10:18  phherlin
 * Integrate PMD v4.0rc1
 * Prepare release 3.2.2
 *
 * Revision 1.3  2007/02/15 22:27:15  phherlin
 * Fix 1641930 Creation of ruleset.xml file causes error in Eclipse
 *
 * Revision 1.2  2006/06/20 21:01:49  phherlin
 * Enable PMD and fix error level violations
 *
 * Revision 1.1  2006/05/22 21:37:35  phherlin
 * Refactor the plug-in architecture to better support future evolutions
 *
 * Revision 1.7  2005/06/11 22:09:57  phherlin
 * Update to PMD 3.2: the symbol table attribute is no more used
 *
 * Revision 1.6  2005/05/07 13:32:06  phherlin
 * Continuing refactoring
 * Fix some PMD violations
 * Fix Bug 1144793
 * Fix Bug 1190624 (at least try)
 *
 * Revision 1.5  2005/01/31 23:39:37  phherlin
 * Upgrading to PMD 2.2
 *
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
class RuleSetWriterImpl implements IRuleSetWriter {

    /**
     * Write a ruleset as an XML stream
     * @param writer the output writer
     * @param ruleSet the ruleset to serialize
     */
    public void write(OutputStream outputStream, RuleSet ruleSet) throws WriterException {
        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            final Document doc = documentBuilder.newDocument();

            final Element ruleSetElement = getRuleSetElement(doc, ruleSet);
            doc.appendChild(ruleSetElement);

            final OutputFormat outputFormat = new OutputFormat(doc, "UTF-8", true);
            outputFormat.setLineWidth(0);

            final DOMSerializer serializer = new XMLSerializer(outputStream, outputFormat);
            serializer.serialize(doc);

        } catch (DOMException e) {
            throw new WriterException(e);
        } catch (FactoryConfigurationError e) {
            throw new WriterException(e);
        } catch (ParserConfigurationException e) {
            throw new WriterException(e);
        } catch (IOException e) {
            throw new WriterException(e);
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

        final Element descriptionElement = getDescriptionElement(doc, ruleSet.getDescription());
        ruleSetElement.appendChild(descriptionElement);

        final Iterator rules = ruleSet.getRules().iterator();
        while (rules.hasNext()) {
            final Rule rule = (Rule) rules.next();
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
//        if (rule instanceof DynamicXPathRule) {
//            className = XPathRule.class.getName();
//        }
        ruleElement.setAttribute("class", className);

        if (rule.usesDFA()) {
            ruleElement.setAttribute("dfa", "true");
        }
        if (rule.include()) {
            ruleElement.setAttribute("include", "true");
        }

        final Element descriptionElement = getDescriptionElement(doc, rule.getDescription());
        ruleElement.appendChild(descriptionElement);

        if (rule.getExamples().size() > 0) {
        	final Element exampleElement = getExampleElement(doc, rule.getExamples().get(0).toString());
        	ruleElement.appendChild(exampleElement);
        }

        final Element priorityElement = getPriorityElement(doc, rule.getPriority());
        ruleElement.appendChild(priorityElement);

        final Element propertiesElement = getPropertiesElement(doc, rule.getProperties());
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
            final Element propertyElement = getPropertyElement(doc, (String) key, (String) properties.get(key));
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
