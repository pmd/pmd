/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.PmdXPathException;
import net.sourceforge.pmd.lang.rule.xpath.XPathVersion;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;


/**
 * Rule that tries to match an XPath expression against a DOM view of an AST.
 */
public final class XPathRule extends AbstractRule {

    private static final Logger LOG = LoggerFactory.getLogger(XPathRule.class);

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
     * This is initialized only once when calling {@link #apply(Node, RuleContext)} or {@link #getTargetSelector()}.
     */
    private SaxonXPathRuleQuery xpathRuleQuery;


    // this is shared with rules forked by deepCopy, used by the XPathRuleQuery
    private DeprecatedAttrLogger attrLogger = DeprecatedAttrLogger.create(this);


    /**
     * @deprecated This is now only used by the ruleset loader. When
     *     we have syntactic sugar for XPath rules in the XML, we won't
     *     need this anymore.
     */
    @Deprecated
    public XPathRule() {
        definePropertyDescriptor(XPATH_DESCRIPTOR);
        definePropertyDescriptor(VERSION_DESCRIPTOR);
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

        setProperty(XPathRule.XPATH_DESCRIPTOR, expression);
        setProperty(XPathRule.VERSION_DESCRIPTOR, XPathVersion.ofId(version.getXmlName()));
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


    @Override
    public void apply(Node target, RuleContext ctx) {
        SaxonXPathRuleQuery query = getQueryMaybeInitialize();

        List<Node> nodesWithViolation;
        try {
            nodesWithViolation = query.evaluate(target);
        } catch (PmdXPathException e) {
            throw addExceptionContext(e);
        }

        for (Node nodeWithViolation : nodesWithViolation) {
            addViolation(ctx, nodeWithViolation, nodeWithViolation.getImage());
        }
    }

    private ContextedRuntimeException addExceptionContext(PmdXPathException e) {
        return e.addRuleName(getName());
    }

    @Override
    public void initialize(LanguageProcessor languageProcessor) {
        String xpath = getXPathExpression();
        XPathVersion version = getVersion();

        if (version == null) {
            throw new IllegalStateException("Invalid XPath version, should have been caught by Rule::dysfunctionReason");
        }

        try {
            xpathRuleQuery = new SaxonXPathRuleQuery(xpath,
                                                     version,
                                                     getPropertiesByPropertyDescriptor(),
                                                     languageProcessor.services().getXPathHandler(),
                                                     attrLogger);
        } catch (PmdXPathException e) {
            throw addExceptionContext(e);
        }
    }

    private SaxonXPathRuleQuery getQueryMaybeInitialize() throws PmdXPathException {
        if (xpathRuleQuery == null) {
            throw new IllegalStateException("Not initialized");
        }
        return xpathRuleQuery;
    }


    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {

        List<String> visits = getQueryMaybeInitialize().getRuleChainVisits();

        logXPathRuleChainUsage(!visits.isEmpty());

        return visits.isEmpty() ? RuleTargetSelector.forRootOnly()
                                : RuleTargetSelector.forXPathNames(visits);
    }


    private void logXPathRuleChainUsage(boolean usesRuleChain) {
        LOG.debug("{} rule chain for XPath {} rule: {} ({})",
                usesRuleChain ? "Using" : "no",
                getProperty(XPathRule.VERSION_DESCRIPTOR),
                getName(),
                getRuleSetName());
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

    private static Map<String, XPathVersion> getXPathVersions() {
        Map<String, XPathVersion> tmp = new HashMap<>();
        for (XPathVersion v : XPathVersion.values()) {
            tmp.put(v.getXmlName(), v);
        }
        return Collections.unmodifiableMap(tmp);
    }
}
