/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery.XPATH_1_0;
import static net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery.XPATH_1_0_COMPATIBILITY;
import static net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery.XPATH_2_0;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.JaxenXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.properties.EnumeratedProperty;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * Rule that tries to match an XPath expression against a DOM view of an AST.
 * <p>
 * <p>This rule needs a "xpath" property value in order to function.</p>
 */
public class XPathRule extends AbstractRule {

    public static final StringProperty XPATH_DESCRIPTOR = new StringProperty("xpath", "XPath expression", "", 1.0f);

    private static final Map<String, String> XPATH_VERSIONS;

    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put(XPATH_1_0, XPATH_1_0);
        tmp.put(XPATH_1_0_COMPATIBILITY, XPATH_1_0_COMPATIBILITY);
        tmp.put(XPATH_2_0, XPATH_2_0);
        XPATH_VERSIONS = Collections.unmodifiableMap(tmp);
    }

    public static final EnumeratedProperty<String> VERSION_DESCRIPTOR
            = new EnumeratedProperty<>("version",
            "XPath specification version", XPATH_VERSIONS, XPATH_1_0, String.class, 2.0f);

    /**
     * This is initialized only once when calling {@link #evaluate(Node, RuleContext)} or {@link #getRuleChainVisits()}.
     */
    private XPathRuleQuery xpathRuleQuery;

    /**
     * Creates a new XPathRule without the corresponding XPath query.
     */
    public XPathRule() {
        definePropertyDescriptor(XPATH_DESCRIPTOR);
        definePropertyDescriptor(VERSION_DESCRIPTOR);
    }

    /**
     * Creates a new XPathRule and associates the XPath query.
     */
    public XPathRule(final String xPath) {
        this();
        setXPath(xPath);
    }

    /**
     * Sets the XPath to query against the desired nodes in {@link #apply(List, RuleContext)}.
     *
     * @param xPath the XPath query
     */
    public void setXPath(final String xPath) {
        setProperty(XPathRule.XPATH_DESCRIPTOR, xPath);
    }

    public void setVersion(final String version) {
        setProperty(XPathRule.VERSION_DESCRIPTOR, version);
    }

    /**
     * Apply the rule to all nodes.
     */
    @Override
    public void apply(final List<? extends Node> nodes, final RuleContext ctx) {
        for (final Node node : nodes) {
            evaluate(node, ctx);
        }
    }

    /**
     * Evaluate the XPath query with the AST node. All matches are reported as violations.
     *
     * @param node The Node that to be checked.
     * @param data The RuleContext.
     */
    public void evaluate(final Node node, final RuleContext data) {
        initXPathRuleQuery();

        final List<Node> nodesWithViolation = xpathRuleQuery.evaluate(node, data);
        for (final Node nodeWithViolation : nodesWithViolation) {
            addViolation(data, nodeWithViolation, nodeWithViolation.getImage());
        }
    }

    /**
     * Initializes {@link #xpathRuleQuery} iff {@link #xPathRuleQueryNeedsInitialization()} is true. To select the
     * engine in which the query will be run it looks at the XPath version.
     */
    private void initXPathRuleQuery() {
        if (xPathRuleQueryNeedsInitialization()) {
            final String xpath = getProperty(XPATH_DESCRIPTOR);
            final String version = getProperty(VERSION_DESCRIPTOR);

            initRuleQueryBasedOnVersion(version);

            xpathRuleQuery.setXPath(xpath);
            xpathRuleQuery.setVersion(version);
            xpathRuleQuery.setProperties(getPropertiesByPropertyDescriptor());
        }
    }

    /**
     * Checks if the {@link #xpathRuleQuery} is null and therefore requires initialization;
     *
     * @return true if {@link #xpathRuleQuery} is null
     */
    private boolean xPathRuleQueryNeedsInitialization() {
        return xpathRuleQuery == null;
    }

    private void initRuleQueryBasedOnVersion(final String version) {
        if (XPATH_1_0.equals(version)) {
            xpathRuleQuery = new JaxenXPathRuleQuery();
        } else {
            xpathRuleQuery = new SaxonXPathRuleQuery();
        }
    }

    @Override
    public List<String> getRuleChainVisits() {
        if (xPathRuleQueryNeedsInitialization()) {
            initXPathRuleQuery();

            for (final String nodeName : xpathRuleQuery.getRuleChainVisits()) {
                super.addRuleChainVisit(nodeName);
            }
        }
        return super.getRuleChainVisits();
    }

    @Override
    public String dysfunctionReason() {
        return hasXPathExpression() ? null : "Missing xPath expression";
    }

    private boolean hasXPathExpression() {
        return StringUtils.isNotBlank(getProperty(XPATH_DESCRIPTOR));
    }
}
