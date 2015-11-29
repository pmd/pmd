/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This implementation of XPathRuleQuery provides support for RuleChain visits.
 */
public abstract class AbstractXPathRuleQuery implements XPathRuleQuery {

    /**
     * The XPath query string.
     */
    protected String xpath;

    /**
     * The XPath version;
     */
    protected String version;

    /**
     * The properties.
     */
    protected Map<PropertyDescriptor<?>, Object> properties;

    /**
     * Subclasses can manage RuleChain visits via this list.
     */
    protected final List<String> ruleChainVisits = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    public void setXPath(String xpath) {
        this.xpath = xpath;
    }

    /**
     * {@inheritDoc}
     */
    public void setVersion(String version) throws UnsupportedOperationException {
        if (!isSupportedVersion(version)) {
            throw new UnsupportedOperationException(this.getClass().getSimpleName()
                    + " does not support XPath version: " + version);
        }
        this.version = version;
    }

    /**
     * Subclasses should implement to indicate whether an XPath version is
     * supported.
     * 
     * @param version The XPath version.
     * @return <code>true</code> if the XPath version is supported,
     *         <code>false</code> otherwise.
     */
    protected abstract boolean isSupportedVersion(String version);

    /**
     * {@inheritDoc}
     */
    public void setProperties(Map<PropertyDescriptor<?>, Object> properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getRuleChainVisits() {
        return ruleChainVisits;
    }

    /**
     * {@inheritDoc}
     */
    public abstract List<Node> evaluate(Node node, RuleContext data);
}
