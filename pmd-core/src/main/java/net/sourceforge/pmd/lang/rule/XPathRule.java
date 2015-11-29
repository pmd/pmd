/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule;

import static net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery.XPATH_1_0;
import static net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery.XPATH_1_0_COMPATIBILITY;
import static net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery.XPATH_2_0;

import java.util.List;

import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;
import net.sourceforge.pmd.lang.rule.properties.StringProperty;
import net.sourceforge.pmd.lang.rule.xpath.JaxenXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Rule that tries to match an XPath expression against a DOM view of an AST.
 * <p/>
 * This rule needs a "xpath" property value in order to function.
 */
public class XPathRule extends AbstractRule {

	public static final StringProperty XPATH_DESCRIPTOR = new StringProperty("xpath", "XPath expression", "", 1.0f);
	public static final EnumeratedProperty<String> VERSION_DESCRIPTOR = new EnumeratedProperty<>("version",
			"XPath specification version", 
			new String[] { XPATH_1_0, XPATH_1_0_COMPATIBILITY, XPATH_2_0 },
			new String[] { XPATH_1_0, XPATH_1_0_COMPATIBILITY, XPATH_2_0 }, 
			0, 2.0f);

	private XPathRuleQuery xpathRuleQuery;

	public XPathRule() {
		definePropertyDescriptor(XPATH_DESCRIPTOR);
		definePropertyDescriptor(VERSION_DESCRIPTOR);
	}

	public XPathRule(String xPath) {
		this();
		setXPath(xPath);
	}
	
	public void setXPath(String xPath) {
		setProperty(XPathRule.XPATH_DESCRIPTOR, xPath);
	}
	
	public void setVersion(String version) {
		setProperty(XPathRule.VERSION_DESCRIPTOR, version);
	}
	
	/**
	 * Apply the rule to all nodes.
	 */
	public void apply(List<? extends Node> nodes, RuleContext ctx) {
		for (Node node : nodes) {
			evaluate(node, ctx);
		}
	}

	/**
	 * Evaluate the XPath query with the AST node.
	 * All matches are reported as violations.
	 *
	 * @param node The Node that to be checked.
	 * @param data The RuleContext.
	 */
	public void evaluate(Node node, RuleContext data) {
		init();
		List<Node> nodes = xpathRuleQuery.evaluate(node, data);
		if (nodes != null) {
			for (Node n : nodes) {
				addViolation(data, n, n.getImage());
			}
		}

	}

	@Override
	public List<String> getRuleChainVisits() {
		if (init()) {
			for (String nodeName : xpathRuleQuery.getRuleChainVisits()) {
				super.addRuleChainVisit(nodeName);
			}
		}
		return super.getRuleChainVisits();
	}

	private boolean init() {
		if (xpathRuleQuery == null) {
			String xpath = getProperty(XPATH_DESCRIPTOR);
			String version = (String) getProperty(VERSION_DESCRIPTOR);
			if (XPATH_1_0.equals(version)) {
				xpathRuleQuery = new JaxenXPathRuleQuery();
			} else {
				xpathRuleQuery = new SaxonXPathRuleQuery();
			}
			xpathRuleQuery.setXPath(xpath);
			xpathRuleQuery.setVersion(version);
			xpathRuleQuery.setProperties(this.getPropertiesByPropertyDescriptor());
			return true;
		}
		return false;
	}


	public boolean hasXPathExpression() {		
		String xPath = getProperty(XPATH_DESCRIPTOR);
		return StringUtil.isNotEmpty(xPath);
	}

	/**
	 * @see PropertySource#dysfunctionReason()
	 */
	public String dysfunctionReason() {
		return hasXPathExpression() ? null : "Missing xPath expression";
	}
}
