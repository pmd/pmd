/*
 * User: tom
 * Date: Jul 1, 2002
 * Time: 2:22:25 PM
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
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
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

    /**
     * Creates a ruleset.  If passed a comma-delimited string (rulesets/basic.xml,rulesets/unusedcode.xml)
     * it will parse that string and create a new ruleset for each item in the list.
     */
    public RuleSet createRuleSet(String name) throws RuleSetNotFoundException {
        if (name.indexOf(',') == -1) {
           return createRuleSet(tryToGetStreamTo(name));
        }

        RuleSet ruleSet = new RuleSet();
        for (StringTokenizer st = new StringTokenizer(name, ","); st.hasMoreTokens();) {
            String ruleSetName = st.nextToken().trim();
            RuleSet tmpRuleSet = createRuleSet(ruleSetName);
            ruleSet.addRuleSet(tmpRuleSet);
        }
        return ruleSet;
    }

    public RuleSet createRuleSet(InputStream inputStream) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();

            RuleSet ruleSet = new RuleSet();
            ruleSet.setName(root.getAttribute("name"));
            ruleSet.setDescription(root.getChildNodes().item(1).getFirstChild().getNodeValue());

            NodeList rules = root.getElementsByTagName("rule");
            for (int i =0; i<rules.getLength(); i++) {
                Node ruleNode = rules.item(i);
                Rule rule;
                if (ruleNode.getAttributes().getNamedItem("ref") != null) {
                    ExternalRuleID externalRuleID = new ExternalRuleID(ruleNode.getAttributes().getNamedItem("ref").getNodeValue());
                    RuleSetFactory rsf = new RuleSetFactory();
                    RuleSet externalRuleSet = rsf.createRuleSet(ResourceLoader.loadResourceAsStream(externalRuleID.getFilename()));
                    rule = externalRuleSet.getRuleByName(externalRuleID.getRuleName());
                } else {
                    rule = (Rule)Class.forName(ruleNode.getAttributes().getNamedItem("class").getNodeValue()).newInstance();
                    rule.setName(ruleNode.getAttributes().getNamedItem("name").getNodeValue());
                    rule.setMessage(ruleNode.getAttributes().getNamedItem("message").getNodeValue());
                }

                // get the description, priority, example and properties (if any)
                Node node = ruleNode.getFirstChild();
                while (node != null) {
                    if (node.getNodeName() != null && node.getNodeName().equals("description")) {
                        rule.setDescription(node.getFirstChild().getNodeValue());
                    }

                    if (node.getNodeName() != null && node.getNodeName().equals("priority")) {
                        rule.setPriority(Integer.parseInt(node.getFirstChild().getNodeValue()));
                    }

                    if (node.getNodeName() != null && node.getNodeName().equals("example")) {
                        rule.setExample(node.getFirstChild().getNextSibling().getNodeValue());
                    }

                    if (node.getNodeName().equals("properties")) {
                        Node propNode = node.getFirstChild().getNextSibling();
                        while (propNode != null && propNode.getAttributes() != null) {
                            String propName = propNode.getAttributes().getNamedItem("name").getNodeValue();
                            String propValue = propNode.getAttributes().getNamedItem("value").getNodeValue();
                            rule.addProperty(propName, propValue);
                            propNode = propNode.getNextSibling().getNextSibling();
                        }
                    }

                    node = node.getNextSibling();
                }
                ruleSet.addRule(rule);
            }
            return ruleSet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't read from that source: " + e.getMessage());
        }
    }

    private InputStream tryToGetStreamTo(String name) throws RuleSetNotFoundException {
        InputStream in = ResourceLoader.loadResourceAsStream( name );
        if ( in == null ) {
            throw new RuleSetNotFoundException( "Can't find resource " + name +
           "Make sure the resource is valid file " +
            "or URL or is on the CLASSPATH\n" );
        }
        return in;
    }
}
