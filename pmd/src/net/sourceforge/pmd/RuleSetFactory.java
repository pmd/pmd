/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

public class RuleSetFactory {

    private ClassLoader classLoader;

    /**
     * Returns an Iterator of RuleSet objects loaded from descriptions from
     * the "rulesets.properties" resource or from the "rulesets.filenames" property.
     *
     * @return an iterator on RuleSet objects
     */
    public Iterator getRegisteredRuleSets() throws RuleSetNotFoundException {
        try {
            Properties props = new Properties();
            props.load(ResourceLoader.loadResourceAsStream("rulesets/rulesets.properties"));
            String rulesetFilenames = props.getProperty("rulesets.filenames");
            List ruleSets = new ArrayList();
            for (StringTokenizer st = new StringTokenizer(rulesetFilenames, ","); st.hasMoreTokens();) {
                ruleSets.add(createRuleSet(st.nextToken()));
            }
            return ruleSets.iterator();
        } catch (IOException ioe) {
            throw new RuntimeException("Couldn't find rulesets.properties; please ensure that the rulesets directory is on the classpath.  Here's the current classpath: " + System.getProperty("java.class.path"));
        }
    }

    /**
     * Create a ruleset from a name or from a list of names
     *
     * @param name        name of rule set file loaded as a resource
     * @param classLoader the classloader used to load the ruleset and subsequent rules
     * @return the new ruleset
     * @throws RuleSetNotFoundException
     */
    public RuleSet createRuleSet(String name, ClassLoader classLoader) throws RuleSetNotFoundException {
        this.classLoader = classLoader;
        if (name.indexOf(',') == -1) {
            return createRuleSet(tryToGetStreamTo(name, classLoader));
        }
        RuleSet ruleSet = new RuleSet();
        for (StringTokenizer st = new StringTokenizer(name, ","); st.hasMoreTokens();) {
            ruleSet.addRuleSet(createRuleSet(st.nextToken().trim(), classLoader));
        }
        return ruleSet;
    }

    /**
     * Creates a ruleset.  If passed a comma-delimited string (rulesets/basic.xml,rulesets/unusedcode.xml)
     * it will parse that string and create a new ruleset for each item in the list.
     * Same as createRuleSet(name, ruleSetFactory.getClassLoader()).
     */
    public RuleSet createRuleSet(String name) throws RuleSetNotFoundException {
        return createRuleSet(name, getClass().getClassLoader());
    }

    /**
     * Create a ruleset from an inputsteam.
     * Same as createRuleSet(inputStream, ruleSetFactory.getClassLoader()).
     *
     * @param inputStream an input stream  that contains a ruleset descripion
     * @return a new ruleset
     */
    public RuleSet createRuleSet(InputStream inputStream) {
        return createRuleSet(inputStream, getClass().getClassLoader());
    }

    /**
     * Create a ruleset from an input stream with a specified class loader
     *
     * @param inputStream an input stream that contains a ruleset descripion
     * @param classLoader a class loader used to load rule classes
     * @return a new ruleset
     */
    public RuleSet createRuleSet(InputStream inputStream, ClassLoader classLoader) {
        try {
            this.classLoader = classLoader;
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();

            RuleSet ruleSet = new RuleSet();
            ruleSet.setName(root.getAttribute("name"));

            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getNodeName().equals("description")) {
                        parseDescriptionNode(ruleSet, node);
                    } else if (node.getNodeName().equals("rule")) {
                        parseRuleNode(ruleSet, node);
                    }
                }
            }

            return ruleSet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't read from that source: " + e.getMessage());
        }
    }

    /**
     * Try to load a resource with the specified class loader
     *
     * @param name   a resource name (contains a ruleset description)
     * @param loader a class loader used to load that rule set description
     * @return an inputstream to that resource
     * @throws RuleSetNotFoundException
     */
    private InputStream tryToGetStreamTo(String name, ClassLoader loader) throws RuleSetNotFoundException {
        InputStream in = ResourceLoader.loadResourceAsStream(name, loader);
        if (in == null) {
            throw new RuleSetNotFoundException("Can't find resource " + name + ".  Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: " + System.getProperty("java.class.path"));
        }
        return in;
    }

    /**
     * Parse a ruleset description node
     *
     * @param ruleSet         the ruleset being constructed
     * @param descriptionNode must be a description element node
     */
    private void parseDescriptionNode(RuleSet ruleSet, Node descriptionNode) {
        NodeList nodeList = descriptionNode.getChildNodes();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                buffer.append(node.getNodeValue());
            } else if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
                buffer.append(node.getNodeValue());
            }
        }
        ruleSet.setDescription(buffer.toString());
    }

    /**
     * Parse a rule node
     *
     * @param ruleSet     the ruleset being constructed
     * @param ruleElement must be a rule element node
     */
    private void parseRuleNode(RuleSet ruleSet, Node ruleNode) throws ClassNotFoundException, InstantiationException, IllegalAccessException, RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;
        String ref = ruleElement.getAttribute("ref");
        if (ref.trim().length() == 0) {
            parseInternallyDefinedRuleNode(ruleSet, ruleNode, true);
        } else {
            parseExternallyDefinedRuleNode(ruleSet, ruleNode);
        }
    }

    /**
     * Process a rule definition node
     *
     * @param ruleSet  the ruleset being constructed
     * @param ruleNode must be a rule element node
     */
    private Rule parseInternallyDefinedRuleNode(RuleSet ruleSet, Node ruleNode, boolean addRule) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Element ruleElement = (Element) ruleNode;

        Rule rule = (Rule)classLoader.loadClass(ruleElement.getAttribute("class")).newInstance();

        rule.setName(ruleElement.getAttribute("name"));
        rule.setMessage(ruleElement.getAttribute("message"));
        rule.setRuleSetName(ruleSet.getName());

        if (ruleElement.hasAttribute("symboltable") && ruleElement.getAttribute("symboltable").equals("true")) {
            rule.setUsesSymbolTable();
        }

        if (ruleElement.hasAttribute("dfa") && ruleElement.getAttribute("dfa").equals("true")) {
            rule.setUsesDFA();
        }

        NodeList nodeList = ruleElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("description")) {
                    rule.setDescription(parseTextNode(node));
                } else if (node.getNodeName().equals("example")) {
                    rule.setExample(parseTextNode(node));
                } else if (node.getNodeName().equals("priority")) {
                    parsePriorityNode(rule, node);
                } else if (node.getNodeName().equals("properties")) {
                    parsePropertiesNode(rule, node);
                }
            }
        }
        if (addRule) {
            ruleSet.addRule(rule);
        }
        return rule;
    }

    /**
     * Process a reference to a rule
     *
     * @param ruleSet  the ruleset being constructucted
     * @param ruleNode must be a ruke element node
     */
    private void parseExternallyDefinedRuleNode(RuleSet ruleSet, Node ruleNode) throws RuleSetNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        Element ruleElement = (Element) ruleNode;
        String ref = ruleElement.getAttribute("ref");
        if (ref.endsWith("xml")) {
            parseRuleNodeWithExclude(ruleSet, ruleElement, ref);
        } else {
            parseRuleNodeWithSimpleReference(ruleSet, ruleNode, ref);
        }
    }

    /**
     * Parse a rule node with a simple reference
     *
     * @param ruleSet the ruleset being constructed
     * @param ref     a reference to a rule
     */
    private void parseRuleNodeWithSimpleReference(RuleSet ruleSet, Node ruleNode, String ref) throws RuleSetNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        RuleSetFactory rsf = new RuleSetFactory();
        ExternalRuleID externalRuleID = new ExternalRuleID(ref);
        RuleSet externalRuleSet = rsf.createRuleSet(ResourceLoader.loadResourceAsStream(externalRuleID.getFilename()));
        Rule externalRule = externalRuleSet.getRuleByName(externalRuleID.getRuleName());
        Rule overrideRule = parseInternallyDefinedRuleNode(ruleSet, ruleNode, false);
        if (overrideRule.getName() != null) {
            externalRule.setName(overrideRule.getName());
        }
        if (overrideRule.getMessage() != null) {
            externalRule.setMessage(overrideRule.getMessage());
        }
        if (overrideRule.getDescription() != null) {
            externalRule.setDescription(overrideRule.getDescription());
        }
        if (overrideRule.getExample() != null) {
            externalRule.setExample(overrideRule.getExample());
        }
        if (overrideRule.getPriority() != 0) {
            externalRule.setPriority(overrideRule.getPriority());
        }
        if (overrideRule.getProperties() != null) {
            externalRule.addProperties(overrideRule.getProperties());
        }
        ruleSet.addRule(externalRule);
    }

    /**
     * Parse a reference rule node with excludes
     *
     * @param ruleSet     the ruleset being constructed
     * @param ruleElement must be a rule element
     * @param ref         the ruleset reference
     */
    private void parseRuleNodeWithExclude(RuleSet ruleSet, Element ruleElement, String ref) throws RuleSetNotFoundException {
        NodeList excludeNodes = ruleElement.getChildNodes();
        Set excludes = new HashSet();
        for (int i = 0; i < excludeNodes.getLength(); i++) {
            Node node = excludeNodes.item(i);
            if ((node.getNodeType() == Node.ELEMENT_NODE) && (node.getNodeName().equals("exclude"))) {
                Element excludeElement = (Element) node;
                excludes.add(excludeElement.getAttribute("name"));
            }
        }

        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet externalRuleSet = rsf.createRuleSet(ResourceLoader.loadResourceAsStream(ref));
        for (Iterator i = externalRuleSet.getRules().iterator(); i.hasNext();) {
            Rule rule = (Rule) i.next();
            if (!excludes.contains(rule.getName())) {
                ruleSet.addRule(rule);
            }
        }
    }

    private String parseTextNode(Node exampleNode) {
        StringBuffer buffer = new StringBuffer();
        NodeList nodeList = exampleNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.CDATA_SECTION_NODE || node.getNodeType() == Node.TEXT_NODE) {
                buffer.append(node.getNodeValue());
            }
        }
        return buffer.toString();
    }

    /**
     * Parse a value node
     *
     * @param valueNode must be a value element node
     */
    private String parseValueNode(Node valueNode) {
        StringBuffer buffer = new StringBuffer();
        NodeList nodeList = valueNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeType() == Node.CDATA_SECTION_NODE || nodeList.item(i).getNodeType() == Node.TEXT_NODE) {
                buffer.append(nodeList.item(i).getNodeValue());
            }
        }
        return buffer.toString();
    }


    /**
     * Parse a priority node
     *
     * @param rule         the rule being constructed
     * @param priorityNode must be a priority element
     */
    private void parsePriorityNode(Rule rule, Node priorityNode) {
        StringBuffer buffer = new StringBuffer();
        NodeList nodeList = priorityNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.TEXT_NODE) {
                buffer.append(node.getNodeValue());
            }
        }
        rule.setPriority(new Integer(buffer.toString().trim()).intValue());
    }

    /**
     * Parse a properties node
     *
     * @param rule           the rule being constructed
     * @param propertiesNode must be a properties element node
     */
    private void parsePropertiesNode(Rule rule, Node propertiesNode) {
        NodeList nodeList = propertiesNode.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("property")) {
                parsePropertyNode(rule, node);
            }
        }
    }

    /**
     * Parse a property node
     *
     * @param rule         the rule being constructed
     * @param propertyNode must be a property element node
     */
    private void parsePropertyNode(Rule rule, Node propertyNode) {
        Element propertyElement = (Element) propertyNode;
        String name = propertyElement.getAttribute("name");
        String value = propertyElement.getAttribute("value");
        if (value.trim().length() == 0) {
            NodeList nodeList = propertyNode.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if ((node.getNodeType() == Node.ELEMENT_NODE) && node.getNodeName().equals("value")) {
                    value = parseValueNode(node);
                }
            }
        }
        if (propertyElement.hasAttribute("pluginname")) {
            rule.addProperty("pluginname", propertyElement.getAttributeNode("pluginname").getNodeValue());
        }
        rule.addProperty(name, value);
    }

}
