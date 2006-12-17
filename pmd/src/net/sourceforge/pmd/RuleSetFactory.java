/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import net.sourceforge.pmd.util.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import net.sourceforge.pmd.rules.XPathRule;

// Note that ruleset parsing may fail on JDK 1.6 beta
// due to this bug - http://www.netbeans.org/issues/show_bug.cgi?id=63257

public class RuleSetFactory {

    private static class OverrideParser {
        private Element ruleElement;

        public OverrideParser(Element ruleElement) {
            this.ruleElement = ruleElement;
        }

        public void overrideAsNecessary(Rule rule) {
            if (ruleElement.hasAttribute("name")) {
                rule.setName(ruleElement.getAttribute("name"));
            }
            if (ruleElement.hasAttribute("message")) {
                rule.setMessage(ruleElement.getAttribute("message"));
            }
            if (ruleElement.hasAttribute("externalInfoUrl")) {
                rule.setExternalInfoUrl(ruleElement.getAttribute("externalInfoUrl"));
            }
            for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
                Node node = ruleElement.getChildNodes().item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getNodeName().equals("description")) {
                        rule.setDescription(parseTextNode(node));
                    } else if (node.getNodeName().equals("example")) {
                        rule.setExample(parseTextNode(node));
                    } else if (node.getNodeName().equals("priority")) {
                        rule.setPriority(Integer.parseInt(parseTextNode(node)));
                    } else if (node.getNodeName().equals("properties")) {
                        Properties p = new Properties();
                        parsePropertiesNode(p, node);
                        rule.addProperties(p);
                    }
                }
            }
        }
    }

    private int minPriority = Rule.LOWEST_PRIORITY;

    public void setMinimumPriority(int minPriority) {
        this.minPriority = minPriority;
    }

    /**
     * Returns an Iterator of RuleSet objects loaded from descriptions from the
     * "rulesets.properties" resource.
     *
     * @return an iterator of RuleSet objects
     */
    public Iterator getRegisteredRuleSets() throws RuleSetNotFoundException {
        try {
            Properties props = new Properties();
            props.load(ResourceLoader.loadResourceAsStream("rulesets/rulesets.properties"));
            String rulesetFilenames = props.getProperty("rulesets.filenames");
            return createRuleSets(rulesetFilenames).getRuleSetsIterator();
        } catch (IOException ioe) {
            throw new RuntimeException("Couldn't find rulesets.properties; please ensure that the rulesets directory is on the classpath.  Here's the current classpath: "
                    + System.getProperty("java.class.path"));
        }
    }

    /**
     * Create a RuleSets from a list of names.
     *
     * @param ruleSetFileNames comma-separated list of rule set files.
     * @param classLoader the classloader to load the rulesets
     * @throws RuleSetNotFoundException
     */
    public RuleSets createRuleSets(String ruleSetFileNames, ClassLoader classLoader)
            throws RuleSetNotFoundException {
        RuleSets ruleSets = new RuleSets();

        for (StringTokenizer st = new StringTokenizer(ruleSetFileNames, ","); st
                .hasMoreTokens();) {
            RuleSet ruleSet = createSingleRuleSet(st.nextToken().trim(), classLoader);
            ruleSets.addRuleSet(ruleSet);
        }

        return ruleSets;
    }

    /**
     * Create a RuleSets from a list of names, using the classloader of this class.
     *
     * @param ruleSetFileNames comma-separated list of rule set files.
     * @throws RuleSetNotFoundException
     */
    public RuleSets createRuleSets(String ruleSetFileNames)
            throws RuleSetNotFoundException {
        return createRuleSets(ruleSetFileNames, getClass().getClassLoader());
    }

    /**
     * Create a ruleset from a name or from a list of names
     *
     * @param name        name of rule set file loaded as a resource
     * @param classLoader the classloader used to load the ruleset and subsequent rules
     * @return the new ruleset
     * @throws RuleSetNotFoundException
     * @deprecated Use createRuleSets instead, because this method puts all rules in one
     *             single RuleSet object, and thus removes name and language of the
     *             originating rule set files.
     */
    public RuleSet createRuleSet(String name, ClassLoader classLoader) throws RuleSetNotFoundException {
        RuleSets ruleSets = createRuleSets(name, classLoader);
        RuleSet result = new RuleSet();
        RuleSet[] allRuleSets = ruleSets.getAllRuleSets();
        for (int i = 0; i < allRuleSets.length; i++) {
            result.addRuleSet(allRuleSets[i]);
        }
        return result;
    }

    /**
     * Create a ruleset from a name
     *
     * @param ruleSetFileName name of rule set file loaded as a resource
     * @param classLoader the classloader used to load the ruleset and subsequent rules
     * @return the new ruleset
     * @throws RuleSetNotFoundException
     */
    private RuleSet createSingleRuleSet(String ruleSetFileName, ClassLoader classLoader)
            throws RuleSetNotFoundException {
        return createRuleSet(tryToGetStreamTo(ruleSetFileName, classLoader), classLoader);
    }

    /**
     * Create a ruleset from a name
     *
     * @param ruleSetFileName name of rule set file loaded as a resource
     * @return the new ruleset
     * @throws RuleSetNotFoundException
     */
    public RuleSet createSingleRuleSet(String ruleSetFileName)
            throws RuleSetNotFoundException {
        return createRuleSet(tryToGetStreamTo(ruleSetFileName, getClass()
                .getClassLoader()));
    }

    /**
     * Create a ruleset from an inputsteam. Same as createRuleSet(inputStream,
     * ruleSetFactory.getClassLoader()).
     *
     * @param inputStream an input stream that contains a ruleset descripion
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
    private RuleSet createRuleSet(InputStream inputStream, ClassLoader classLoader) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();

            RuleSet ruleSet = new RuleSet();
            ruleSet.setName(root.getAttribute("name"));
            ruleSet.setLanguage(Language.getByName(root.getAttribute("language")));

            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getNodeName().equals("description")) {
                        ruleSet.setDescription(parseTextNode(node));
                    } else if (node.getNodeName().equals("rule")) {
                        parseRuleNode(ruleSet, node, classLoader);
                    }
                }
            }

            return ruleSet;
        } catch (ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
            throw new RuntimeException("Couldn't find that class " + cnfe.getMessage());
        } catch (InstantiationException ie) {
            ie.printStackTrace();
            throw new RuntimeException("Couldn't find that class " + ie.getMessage());
        } catch (IllegalAccessException iae) {
            iae.printStackTrace();
            throw new RuntimeException("Couldn't find that class " + iae.getMessage());
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
            throw new RuntimeException("Couldn't find that class " + pce.getMessage());
        } catch (RuleSetNotFoundException rsnfe) {
            rsnfe.printStackTrace();
            throw new RuntimeException("Couldn't find that class " + rsnfe.getMessage());
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException("Couldn't find that class " + ioe.getMessage());
        } catch (SAXException se) {
            se.printStackTrace();
            throw new RuntimeException("Couldn't find that class " + se.getMessage());
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
    private InputStream tryToGetStreamTo(String name, ClassLoader loader)
            throws RuleSetNotFoundException {
        InputStream in = ResourceLoader.loadResourceAsStream(name, loader);
        if (in == null) {
            throw new RuleSetNotFoundException("Can't find resource "
                    + name
                    + ".  Make sure the resource is a valid file or URL or is on the CLASSPATH.  Here's the current classpath: "
                    + System.getProperty("java.class.path"));
        }
        return in;
    }

    /**
     * Parse a rule node
     *
     * @param ruleSet  the ruleset being constructed
     * @param ruleNode must be a rule element node
     */
    private void parseRuleNode(RuleSet ruleSet, Node ruleNode, ClassLoader classLoader)
            throws ClassNotFoundException, InstantiationException,
            IllegalAccessException, RuleSetNotFoundException {
        Element ruleElement = (Element) ruleNode;
        String ref = ruleElement.getAttribute("ref");
        if (ref.trim().length() == 0) {
            parseInternallyDefinedRuleNode(ruleSet, ruleNode, classLoader);
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
    private void parseInternallyDefinedRuleNode(RuleSet ruleSet, Node ruleNode, ClassLoader classLoader)
            throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Element ruleElement = (Element) ruleNode;

        String attribute = ruleElement.getAttribute("class");
        Class c;
        if ((Language.JAVA.equals(ruleSet.getLanguage()) || ruleSet.getLanguage() == null) &&
                attribute.equals("net.sourceforge.pmd.rules.XPathRule")) {
            String xpath = null;
            for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
                Node node = ruleElement.getChildNodes().item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    if (node.getNodeName().equals("properties")) {
                        Properties p = new Properties();
                        parsePropertiesNode(p, node);
                        xpath = p.getProperty("xpath");
                    }
                }
            }
            c = XPathRule.loadClass(classLoader, xpath, ruleElement.getAttribute("name"));
        } else {
            c = classLoader.loadClass(attribute);
        }
        Rule rule = (Rule) c.newInstance();

        rule.setName(ruleElement.getAttribute("name"));
        rule.setMessage(ruleElement.getAttribute("message"));
        rule.setRuleSetName(ruleSet.getName());
        rule.setExternalInfoUrl(ruleElement.getAttribute("externalInfoUrl"));

        if (ruleElement.hasAttribute("dfa")
                && ruleElement.getAttribute("dfa").equals("true")) {
            rule.setUsesDFA();
        }

        if (ruleElement.hasAttribute("typeResolution")
                && ruleElement.getAttribute("typeResolution").equals("true")) {
            rule.setUsesTypeResolution();
        }

        for (int i = 0; i < ruleElement.getChildNodes().getLength(); i++) {
            Node node = ruleElement.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("description")) {
                    rule.setDescription(parseTextNode(node));
                } else if (node.getNodeName().equals("example")) {
                    rule.setExample(parseTextNode(node));
                } else if (node.getNodeName().equals("priority")) {
                    rule.setPriority(new Integer(parseTextNode(node).trim()).intValue());
                } else if (node.getNodeName().equals("properties")) {
                    Properties p = new Properties();
                    parsePropertiesNode(p, node);
                    for (Iterator j = p.keySet().iterator(); j.hasNext();) {
                        String key = (String) j.next();
                        rule.addProperty(key, p.getProperty(key));
                    }
                }
            }
        }
        if (rule.getPriority() <= minPriority) {
            ruleSet.addRule(rule);
        }
    }

    /**
     * Process a reference to a rule
     *
     * @param ruleSet  the ruleset being constructucted
     * @param ruleNode must be a rule element node
     */
    private void parseExternallyDefinedRuleNode(RuleSet ruleSet, Node ruleNode)
            throws RuleSetNotFoundException {
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
    private void parseRuleNodeWithSimpleReference(RuleSet ruleSet, Node ruleNode,
                                                  String ref) throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory();

        ExternalRuleID externalRuleID = new ExternalRuleID(ref);
        RuleSet externalRuleSet = rsf.createRuleSet(ResourceLoader
                .loadResourceAsStream(externalRuleID.getFilename()));
        Rule externalRule = externalRuleSet.getRuleByName(externalRuleID.getRuleName());
        if (externalRule == null) {
            throw new IllegalArgumentException("Unable to find rule "
                    + externalRuleID.getRuleName()
                    + "; perhaps the rule name is mispelled?");
        }

        OverrideParser p = new OverrideParser((Element) ruleNode);
        p.overrideAsNecessary(externalRule);

        if (externalRule.getPriority() <= minPriority) {
            ruleSet.addRule(externalRule);
        }
    }

    /**
     * Parse a reference rule node with excludes
     *
     * @param ruleSet     the ruleset being constructed
     * @param ruleElement must be a rule element
     * @param ref         the ruleset reference
     */
    private void parseRuleNodeWithExclude(RuleSet ruleSet, Element ruleElement, String ref)
            throws RuleSetNotFoundException {
        NodeList excludeNodes = ruleElement.getChildNodes();
        Set excludes = new HashSet();
        for (int i = 0; i < excludeNodes.getLength(); i++) {
            if ((excludeNodes.item(i).getNodeType() == Node.ELEMENT_NODE)
                    && (excludeNodes.item(i).getNodeName().equals("exclude"))) {
                Element excludeElement = (Element) excludeNodes.item(i);
                excludes.add(excludeElement.getAttribute("name"));
            }
        }

        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet externalRuleSet = rsf.createRuleSet(ResourceLoader
                .loadResourceAsStream(ref));
        for (Iterator i = externalRuleSet.getRules().iterator(); i.hasNext();) {
            Rule rule = (Rule) i.next();
            if (!excludes.contains(rule.getName()) && rule.getPriority() <= minPriority) {
                ruleSet.addRule(rule);
            }
        }
    }

    private static String parseTextNode(Node exampleNode) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < exampleNode.getChildNodes().getLength(); i++) {
            Node node = exampleNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.CDATA_SECTION_NODE
                    || node.getNodeType() == Node.TEXT_NODE) {
                buffer.append(node.getNodeValue());
            }
        }
        return buffer.toString();
    }

    /**
     * Parse a properties node
     *
     * @param propertiesNode must be a properties element node
     */
    private static void parsePropertiesNode(Properties p, Node propertiesNode) {
        for (int i = 0; i < propertiesNode.getChildNodes().getLength(); i++) {
            Node node = propertiesNode.getChildNodes().item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE
                    && node.getNodeName().equals("property")) {
                parsePropertyNode(p, node);
            }
        }
    }

    /**
     * Parse a property node
     *
     * @param propertyNode must be a property element node
     */
    private static void parsePropertyNode(Properties p, Node propertyNode) {
        Element propertyElement = (Element) propertyNode;
        String name = propertyElement.getAttribute("name");
        String value = propertyElement.getAttribute("value");
        // TODO String desc = propertyElement.getAttribute("description");
        if (value.trim().length() == 0) {
            for (int i = 0; i < propertyNode.getChildNodes().getLength(); i++) {
                Node node = propertyNode.getChildNodes().item(i);
                if ((node.getNodeType() == Node.ELEMENT_NODE)
                        && node.getNodeName().equals("value")) {
                    value = parseTextNode(node);
                }
            }
        }
        if (propertyElement.hasAttribute("pluginname")) {
            p.setProperty("pluginname", propertyElement.getAttributeNode("pluginname")
                    .getNodeValue());
        }
        p.setProperty(name, value);
    }
}
