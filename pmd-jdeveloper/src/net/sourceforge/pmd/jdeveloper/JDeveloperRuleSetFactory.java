package net.sourceforge.pmd.jdeveloper;

import net.sourceforge.pmd.ExternalRuleID;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.util.ResourceLoader;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class JDeveloperRuleSetFactory extends RuleSetFactory {

    public RuleSet createRuleSet(InputStream inputStream) {
        return this.createRuleSet(inputStream, getClass().getClassLoader());
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
                Rule rule;
                if (ruleNode.getAttributes().getNamedItem("ref") != null) {
                    ExternalRuleID externalRuleID = new ExternalRuleID(ruleNode.getAttributes().getNamedItem("ref").getNodeValue());
                    RuleSetFactory rsf = new RuleSetFactory();
                    RuleSet externalRuleSet = rsf.createRuleSet(ResourceLoader.loadResourceAsStream(externalRuleID.getFilename()));
                    rule = externalRuleSet.getRuleByName(externalRuleID.getRuleName());
                } else {
                    rule = (Rule) Class.forName(ruleNode.getAttributes().getNamedItem("class").getNodeValue(), true, classLoader).newInstance();
                    rule.setName(ruleNode.getAttributes().getNamedItem("name").getNodeValue());
                    rule.setMessage(ruleNode.getAttributes().getNamedItem("message").getNodeValue());
                }

                // get the description, priority, example and properties (if any)
                Node node = ruleNode.getFirstChild();
                while (node != null) {
                    if (node.getNodeName() != null && node.getNodeName().equals("description")) {
                        rule.setDescription(node.getFirstChild().getNodeValue());
                    } else if (node.getNodeName() != null && node.getNodeName().equals("priority")) {
                        rule.setPriority(Integer.parseInt(node.getFirstChild().getNodeValue().trim()));
                    } else if (node.getNodeName() != null && node.getNodeName().equals("example")) {
                        rule.setExample(node.getFirstChild().getNodeValue());
                    }
                    if (node.getNodeName().equals("properties")) {
                        Node propNode = node.getFirstChild().getNextSibling();
                        while (propNode != null && propNode.getAttributes() != null) {
                            String propName = propNode.getAttributes().getNamedItem("name").getNodeValue();
                            String propValue;
                            if (propName.equals("xpath")) {
                                Node xpathExprNode = propNode.getFirstChild().getNextSibling();
                                propValue = xpathExprNode.getFirstChild().getNodeValue();
                            } else {
                                propValue = propNode.getAttributes().getNamedItem("value").getNodeValue();
                            }
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
}
