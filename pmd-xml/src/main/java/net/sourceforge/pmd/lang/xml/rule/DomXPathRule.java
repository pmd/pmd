/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.rule;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.xml.ast.XmlParser.RootXmlNode;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class DomXPathRule extends AbstractRule {

    SaxonDomXPathQuery query;

    private static final PropertyDescriptor<String> XPATH_EXPR
        = PropertyFactory.stringProperty("xpath")
                         .desc("An XPath 2.0 expression that will be evaluated against the root DOM")
                         .defaultValue("") // no default value
                         .build();


    public DomXPathRule() {
        definePropertyDescriptor(XPATH_EXPR);
    }


    public DomXPathRule(String xpath) {
        this();
        setProperty(XPATH_EXPR, xpath);
    }

    @Override
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        for (Node n : nodes) {
            RootXmlNode root = (RootXmlNode) n;
            SaxonDomXPathQuery query = getXPathQuery();
            for (Node foundNode : query.evaluate(root, this)) {
                ctx.addViolation(foundNode);
            }
        }
    }

    private SaxonDomXPathQuery getXPathQuery() {
        if (query == null) {
            query = new SaxonDomXPathQuery(getProperty(XPATH_EXPR), getPropertyDescriptors());
        }
        return query;
    }

}
