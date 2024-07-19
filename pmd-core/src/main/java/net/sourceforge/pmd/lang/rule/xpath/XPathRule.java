/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.lang.rule.xpath.internal.DeprecatedAttrLogger;
import net.sourceforge.pmd.lang.rule.xpath.internal.SaxonXPathRuleQuery;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.IteratorUtil;
import net.sourceforge.pmd.util.internal.ResourceLoader;


/**
 * Rule that tries to match an XPath expression against a DOM view of an AST.
 */
public final class XPathRule extends AbstractRule {

    private static final Logger LOG = LoggerFactory.getLogger(XPathRule.class);

    private static final PropertyDescriptor<String> XPATH_DESCRIPTOR =
        PropertyFactory.stringProperty("xpath")
                       .desc("XPath expression")
                       .defaultValue("")
                       .build();

    /**
     * This is initialized only once when calling {@link #apply(Node, RuleContext)} or {@link #getTargetSelector()}.
     */
    private SaxonXPathRuleQuery xpathRuleQuery;


    // this is shared with rules forked by deepCopy, used by the XPathRuleQuery
    private DeprecatedAttrLogger attrLogger = DeprecatedAttrLogger.create(this);


    /**
     * This is only used by the ruleset loader.
     * @see ResourceLoader#loadRuleFromClassPath(String)
     */
    XPathRule() {
        definePropertyDescriptor(XPATH_DESCRIPTOR);
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
    }


    @Override
    public Rule deepCopy() {
        XPathRule rule = (XPathRule) super.deepCopy();
        rule.attrLogger = this.attrLogger;
        return rule;
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
            // see Deprecate getImage/@Image #4787 https://github.com/pmd/pmd/issues/4787
            String messageArg = nodeWithViolation.getImage();
            // Nodes might already have been refactored to not use getImage anymore.
            // Therefore, try several other common names
            if (messageArg == null) {
                messageArg = getFirstMessageArgFromNode(nodeWithViolation, "Name", "SimpleName", "MethodName", "Value");
            }
            ctx.addViolation(nodeWithViolation, messageArg);
        }
    }

    private String getFirstMessageArgFromNode(Node node, String... attributeNames) {
        List<String> nameList = Arrays.asList(attributeNames);
        return IteratorUtil.toStream(node.getXPathAttributesIterator())
                .filter(a -> nameList.contains(a.getName()))
                .findFirst()
                .map(Attribute::getStringValue)
                .orElse(null);
    }

    private ContextedRuntimeException addExceptionContext(PmdXPathException e) {
        return e.addRuleName(getName());
    }

    @Override
    public void initialize(LanguageProcessor languageProcessor) {
        String xpath = getXPathExpression();
        XPathVersion version = XPathVersion.DEFAULT;

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
        LOG.debug("{} rule chain for XPath rule: {} ({})",
                usesRuleChain ? "Using" : "no",
                getName(),
                getRuleSetName());
    }


    @Override
    public String dysfunctionReason() {
        if (StringUtils.isBlank(getXPathExpression())) {
            return "Missing XPath expression";
        }
        return null;
    }
}
