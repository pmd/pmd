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

    /**
     * Returns an Iterator of RuleSet objects
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

    public RuleSet createRuleSet(String name, ClassLoader classLoader) throws RuleSetNotFoundException {
        if (name.indexOf(',') == -1) {
            return createRuleSet(tryToGetStreamTo(name, classLoader));
        }

        RuleSet ruleSet = new RuleSet();
        for (StringTokenizer st = new StringTokenizer(name, ","); st.hasMoreTokens();) {
            String ruleSetName = st.nextToken().trim();
            RuleSet tmpRuleSet = createRuleSet(ruleSetName, classLoader);
            ruleSet.addRuleSet(tmpRuleSet);
        }
        return ruleSet;
    }

    /**
     * Creates a ruleset.  If passed a comma-delimited string (rulesets/basic.xml,rulesets/unusedcode.xml)
     * it will parse that string and create a new ruleset for each item in the list.
     */
    public RuleSet createRuleSet(String name) throws RuleSetNotFoundException {
        return createRuleSet(name, getClass().getClassLoader());
    }

    public RuleSet createRuleSet(InputStream inputStream) {
        return createRuleSet(inputStream, getClass().getClassLoader());
    }

    public RuleSet createRuleSet(InputStream inputStream, ClassLoader classLoader) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();

            RuleSet ruleSet = new RuleSet();
            ruleSet.setName(root.getAttribute("name"));
            ruleSet.setDescription(root.getChildNodes().item(1).getFirstChild().getNodeValue());

            NodeList rules = root.getElementsByTagName("rule");
            for (int i = 0; i < rules.getLength(); i++) {
                Node ruleNode = rules.item(i);
                if (ruleNode.getAttributes().getNamedItem("ref") != null) {
                    parseExternallyDefinedRule(ruleSet, ruleNode);
                } else {
                    parseInternallyDefinedRule(ruleSet, ruleNode, classLoader);
                }
            }
            return ruleSet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't read from that source: " + e.getMessage());
        }
    }

    private void parseInternallyDefinedRule(RuleSet ruleSet, Node ruleNode, ClassLoader classLoader) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Rule rule = (Rule) Class.forName(ruleNode.getAttributes().getNamedItem("class").getNodeValue(), true, classLoader).newInstance();
        rule.setName(ruleNode.getAttributes().getNamedItem("name").getNodeValue());
        rule.setMessage(ruleNode.getAttributes().getNamedItem("message").getNodeValue());
        // get the description, priority, example and properties (if any)
        Node node = ruleNode.getFirstChild();
        while (node != null) {
            if (node.getNodeName() != null && node.getNodeName().equals("description")) {
                rule.setDescription(node.getFirstChild().getNodeValue());
            }

            if (node.getNodeName() != null && node.getNodeName().equals("priority")) {
                rule.setPriority(Integer.parseInt(node.getFirstChild().getNodeValue().trim()));
            }

            if (node.getNodeName() != null && node.getNodeName().equals("example")) {
                rule.setExample(node.getFirstChild().getNextSibling().getNodeValue());
            }

            parseProperties(node, rule);

            node = node.getNextSibling();
        }
        ruleSet.addRule(rule);
    }

    private void parseExternallyDefinedRule(RuleSet ruleSet, Node ruleNode) throws RuleSetNotFoundException {
        String referenceValue = ruleNode.getAttributes().getNamedItem("ref").getNodeValue();
        if (referenceValue.endsWith("xml")) {
            parseWithExcludes(ruleNode, referenceValue, ruleSet);
        } else {
            parseSimpleReference(referenceValue, ruleSet);
        }
    }

    private void parseSimpleReference(String referenceValue, RuleSet ruleSet) throws RuleSetNotFoundException {
        RuleSetFactory rsf = new RuleSetFactory();
        ExternalRuleID externalRuleID = new ExternalRuleID(referenceValue);
        RuleSet externalRuleSet = rsf.createRuleSet(ResourceLoader.loadResourceAsStream(externalRuleID.getFilename()));
        ruleSet.addRule(externalRuleSet.getRuleByName(externalRuleID.getRuleName()));
    }

    private void parseWithExcludes(Node ruleNode, String referenceValue, RuleSet ruleSet) throws RuleSetNotFoundException {
        NodeList excludeNodes = ruleNode.getChildNodes();
        Set excludes = new HashSet();
        for (int i=0; i<excludeNodes.getLength(); i++) {
            Node node = excludeNodes.item(i);
            if (node.getAttributes() != null) {
                excludes.add(node.getAttributes().getNamedItem("name").getNodeValue());
            }
        }
        RuleSetFactory rsf = new RuleSetFactory();
        RuleSet externalRuleSet = rsf.createRuleSet(ResourceLoader.loadResourceAsStream(referenceValue));
        for (Iterator i = externalRuleSet.getRules().iterator(); i.hasNext();) {
            Rule rule = (Rule)i.next();
            if (!excludes.contains(rule.getName())) {
                 ruleSet.addRule(rule);
            }
        }
    }

    private void parseProperties(Node node, Rule rule) {
        if (node.getNodeName().equals("properties")) {
            Node propNode = node.getFirstChild().getNextSibling();
            while (propNode != null && propNode.getAttributes() != null) {
                propNode = parseProperty(propNode, rule);
            }
        }
    }

    private Node parseProperty(Node propNode, Rule rule) {
        String propName = propNode.getAttributes().getNamedItem("name").getNodeValue();
        String propValue;
        if (propName.equals("xpath")) {
            Node xpathExprNode = propNode.getFirstChild().getNextSibling();
            propValue = xpathExprNode.getFirstChild().getNextSibling().getNodeValue();
            if (propNode.getAttributes().getNamedItem("pluginname") != null) {
                rule.addProperty("pluginname", propNode.getAttributes().getNamedItem("pluginname").getNodeValue());
            }
        } else {
            propValue = propNode.getAttributes().getNamedItem("value").getNodeValue();
        }
        rule.addProperty(propName, propValue);
        return propNode.getNextSibling().getNextSibling();
    }

    private InputStream tryToGetStreamTo(String name, ClassLoader loader) throws RuleSetNotFoundException {
        InputStream in = ResourceLoader.loadResourceAsStream(name, loader);
        if (in == null) {
            throw new RuleSetNotFoundException("Can't find resource " + name + ".  Make sure the resource is a valid file or URL or is on the CLASSPATH");
        }
        return in;
    }
}
