/*
 * User: tom
 * Date: Jul 1, 2002
 * Time: 2:22:25 PM
 */
package net.sourceforge.pmd;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import java.io.InputStream;
import java.io.Reader;
import java.util.Enumeration;

public class RuleSetFactory {

    public RuleSet createRuleSet(InputStream inputStream) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(inputStream);
            Element root = doc.getDocumentElement();

            RuleSet ruleSet = new RuleSet();
            ruleSet.setName(root.getAttribute("name"));

            NodeList rules = root.getElementsByTagName("rule");
            for (int i =0; i<rules.getLength(); i++) {
                Node ruleNode = (Node)rules.item(i);
                Rule rule = (Rule)Class.forName(ruleNode.getAttributes().getNamedItem("class").getNodeValue()).newInstance();
                rule.setName(ruleNode.getAttributes().getNamedItem("name").getNodeValue());
                rule.setMessage(ruleNode.getAttributes().getNamedItem("message").getNodeValue());
                ruleSet.addRule(rule);
            }

            return ruleSet;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Couldn't read from that source: " + e.getMessage());
        }
    }
}
