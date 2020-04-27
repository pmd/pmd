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
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.JaxenXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.properties.EnumeratedProperty;
import net.sourceforge.pmd.properties.StringProperty;

/**
 * Rule that tries to match an XPath expression against a DOM view of an AST.
 */
public class XPathRule extends AbstractRule {

    /**
     * @deprecated Use {@link #XPathRule(XPathVersion, String)}
     */
    @Deprecated
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


    /**
     * @deprecated Use {@link #XPathRule(XPathVersion, String)}
     */
    @Deprecated
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

    // this is shared with rules forked by deepCopy, used by the XPathRuleQuery
    private DeprecatedAttrLogger attrLogger = DeprecatedAttrLogger.create(this);

    /**
     * Creates a new XPathRule without the corresponding XPath query.
     *
     * @deprecated Use {@link #XPathRule(XPathVersion, String)}
     */
    public XPathRule() {
        definePropertyDescriptor(XPATH_DESCRIPTOR);
        definePropertyDescriptor(VERSION_DESCRIPTOR);
        // Enable Type Resolution on XPath Rules by default - see issue #2048
        super.setTypeResolution(true);
    }

    /**
     * Creates a new XPathRule and associates the XPath query.
     *
     * @deprecated Use {@link #XPathRule(XPathVersion, String)}
     */
    public XPathRule(final String xPath) {
        this();
        setXPath(xPath);
    }

    /**
     * Make a new XPath rule with the given version + expression
     *
     * @param version    Version of the XPath language
     * @param expression XPath expression
     *
     * @throws NullPointerException If any of the arguments is null
     */
    public XPathRule(XPathVersion version, String expression) {
        this();
        Objects.requireNonNull(version, "XPath version is null");
        Objects.requireNonNull(expression, "XPath expression is null");
        setXPath(expression);
        setVersion(version.getXmlName());
    }


    @Override
    public Rule deepCopy() {
        XPathRule rule = (XPathRule) super.deepCopy();
        rule.attrLogger = this.attrLogger;
        return rule;
    }

    /**
     * Returns the version for this rule. Returns null if this is not
     * set or invalid.
     */
    public XPathVersion getVersion() {
        return XPathVersion.ofId(getProperty(VERSION_DESCRIPTOR));
    }

    /**
     * Returns the XPath expression that implements this rule.
     */
    public String getXPathExpression() {
        return getProperty(XPATH_DESCRIPTOR);
    }

    /**
     * @deprecated Use the constructor {@link #XPathRule(XPathVersion, String)},
     *     don't set the expression after the fact.
     */
    @Deprecated
    public void setXPath(final String xPath) {
        setProperty(XPathRule.XPATH_DESCRIPTOR, xPath);
    }

    /**
     * @deprecated Use the constructor {@link #XPathRule(XPathVersion, String)},
     *     don't set the version after the fact.
     */
    @Deprecated
    public void setVersion(final String version) {
        setProperty(XPathRule.VERSION_DESCRIPTOR, version);
    }

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
     *
     * @deprecated Use {@link #apply(List, RuleContext)}
     */
    @Deprecated
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
        String xpath = getXPathExpression();
        XPathVersion version = getVersion();

        if (version == null) {
            throw new IllegalStateException("Invalid XPath version, should have been caught by Rule::dysfunctionReason");
        }

        if (version == XPathVersion.XPATH_1_0) {
            xpathRuleQuery = new JaxenXPathRuleQuery(attrLogger);
        } else {
            xpathRuleQuery = new SaxonXPathRuleQuery(attrLogger);
        }

        xpathRuleQuery.setXPath(xpath);
        xpathRuleQuery.setVersion(version.getXmlName());
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
        if (getVersion() == null) {
            return "Invalid XPath version '" + getProperty(VERSION_DESCRIPTOR) + "'";
        } else if (StringUtils.isBlank(getXPathExpression())) {
            return "Missing XPath expression";
        }
        return null;
    }
}
