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
 *
 * <p>This rule needs a "xpath" property value in order to function.</p>
 */
public class XPathRule extends AbstractRule {

    // TODO 7.0.0 use PropertyDescriptor<String>
    public static final StringProperty XPATH_DESCRIPTOR = StringProperty.named("xpath")
            .desc("XPath expression")
            .defaultValue("")
            .uiOrder(1.0f)
            .build();

    private static final Map<String, String> XPATH_VERSIONS;

    static {
        Map<String, String> tmp = new HashMap<>();
        tmp.put(XPATH_1_0, XPATH_1_0);
        tmp.put(XPATH_1_0_COMPATIBILITY, XPATH_1_0_COMPATIBILITY);
        tmp.put(XPATH_2_0, XPATH_2_0);
        XPATH_VERSIONS = Collections.unmodifiableMap(tmp);
    }

    // published, can't be converted
    public static final EnumeratedProperty<String> VERSION_DESCRIPTOR = EnumeratedProperty.<String>named("version")
            .desc("XPath specification version")
            .mappings(XPATH_VERSIONS)
            .defaultValue(XPATH_1_0)
            .type(String.class)
            .uiOrder(2.0f)
            .build();

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
        // Enable Type Resolution on XPath Rules by default - see issue #2048
        super.setTypeResolution(true);
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
    public void apply(List<? extends Node> nodes, RuleContext ctx) {
        for (Node node : nodes) {
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
        if (xPathRuleQueryNeedsInitialization()) {
            initXPathRuleQuery();
        }

        List<Node> nodesWithViolation = xpathRuleQuery.evaluate(node, data);
        for (Node nodeWithViolation : nodesWithViolation) {
            addViolation(data, nodeWithViolation, nodeWithViolation.getImage());
        }
    }

    /**
     * Initializes {@link #xpathRuleQuery} iff {@link #xPathRuleQueryNeedsInitialization()} is true. To select the
     * engine in which the query will be run it looks at the XPath version.
     */
    private void initXPathRuleQuery() {
        String xpath = getProperty(XPATH_DESCRIPTOR);
        String version = getProperty(VERSION_DESCRIPTOR);

        initRuleQueryBasedOnVersion(version);

        xpathRuleQuery.setXPath(xpath);
        xpathRuleQuery.setVersion(version);
        xpathRuleQuery.setProperties(getPropertiesByPropertyDescriptor());
    }

    /**
     * Checks if the {@link #xpathRuleQuery} is null and therefore requires initialization.
     *
     * @return true if {@link #xpathRuleQuery} is null
     */
    private boolean xPathRuleQueryNeedsInitialization() {
        return xpathRuleQuery == null;
    }

    private void initRuleQueryBasedOnVersion(final String version) {
        xpathRuleQuery = XPATH_1_0.equals(version) ? new JaxenXPathRuleQuery() : new SaxonXPathRuleQuery();
    }

    @Override
    public List<String> getRuleChainVisits() {
        if (xPathRuleQueryNeedsInitialization()) {
            initXPathRuleQuery();

            for (String nodeName : xpathRuleQuery.getRuleChainVisits()) {
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
