/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.AstProcessingStage;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Rule that tries to match an XPath expression against a DOM view of an AST.
 */
public final class XPathRule extends AbstractRule {

    private static final Logger LOG = Logger.getLogger(XPathRule.class.getName());

    // TODO move to XPath subpackage

    /**
     * @deprecated Use {@link #XPathRule(XPathVersion, String)}
     */
    @Deprecated
    public static final PropertyDescriptor<String> XPATH_DESCRIPTOR =
        PropertyFactory.stringProperty("xpath")
                       .desc("XPath expression")
                       .defaultValue("")
                       .build();

    /**
     * @deprecated Use {@link #XPathRule(XPathVersion, String)}
     */
    @Deprecated
    @DeprecatedUntil700
    public static final PropertyDescriptor<XPathVersion> VERSION_DESCRIPTOR =
        PropertyFactory.enumProperty("version", getXPathVersions())
                       .desc("XPath specification version")
                       .defaultValue(XPathVersion.DEFAULT)
                       .build();

    /**
     * This is initialized only once when calling {@link #evaluate(Node, RuleContext)} {@link #getTargetSelector()}.
     */
    private SaxonXPathRuleQuery xpathRuleQuery;


    // this is shared with rules forked by deepCopy, used by the XPathRuleQuery
    private DeprecatedAttrLogger attrLogger = DeprecatedAttrLogger.create(this);

    /**
     * Make a new XPath rule with the given version + expression
     *
     * @param version    Version of the XPath language
     * @param expression XPath expression
     *
     * @throws NullPointerException If any of the arguments is null
     */
    public XPathRule(XPathVersion version, String expression) {
        definePropertyDescriptor(XPATH_DESCRIPTOR);
        definePropertyDescriptor(VERSION_DESCRIPTOR);

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
        return getProperty(VERSION_DESCRIPTOR);
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
        setProperty(XPathRule.VERSION_DESCRIPTOR, XPathVersion.ofId(version));
    }


    @Override
    public void apply(Node target, RuleContext ctx) {
        evaluate(target, ctx);
    }


    /**
     * Evaluate the XPath query with the AST node. All matches are reported as violations.
     *
     * @param node The Node that to be checked.
     * @param data The RuleContext.
     *
     * @deprecated Use {@link #apply(Node, RuleContext)}
     */
    @Deprecated
    public void evaluate(final Node node, final RuleContext data) {
        if (xPathRuleQueryNeedsInitialization()) {
            initXPathRuleQuery();
        }

        List<Node> nodesWithViolation = xpathRuleQuery.evaluate(node);
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

        xpathRuleQuery = new SaxonXPathRuleQuery(xpath,
                                                 version,
                                                 getPropertiesByPropertyDescriptor(),
                                                 getLanguage().getDefaultVersion().getLanguageVersionHandler().getXPathHandler(),
                                                 attrLogger);
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
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        if (xPathRuleQueryNeedsInitialization()) {
            initXPathRuleQuery();
        }

        List<String> visits = xpathRuleQuery.getRuleChainVisits();

        logXPathRuleChainUsage(!visits.isEmpty());

        return visits.isEmpty() ? RuleTargetSelector.forRootOnly()
                                : RuleTargetSelector.forXPathNames(visits);
    }


    private void logXPathRuleChainUsage(boolean usesRuleChain) {
        if (LOG.isLoggable(Level.FINE)) {
            String message = (usesRuleChain ? "Using " : "no ")
                + "rule chain for XPath " + getProperty(XPathRule.VERSION_DESCRIPTOR)
                + " rule: " + getName() + " (" + getRuleSetName() + ")";
            LOG.fine(message);
        }
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

    @Override
    public boolean dependsOn(AstProcessingStage<?> stage) {
        // FIXME must be made language-specific
        return true;
    }

    private static Map<String, XPathVersion> getXPathVersions() {
        Map<String, XPathVersion> tmp = new HashMap<>();
        for (XPathVersion v : XPathVersion.values()) {
            tmp.put(v.getXmlName(), v);
        }
        return Collections.unmodifiableMap(tmp);
    }
}
