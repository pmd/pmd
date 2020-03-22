/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.properties.PropertyDescriptor;

/**
 * This implementation of XPathRuleQuery provides support for RuleChain visits.
 *
 * @deprecated Internal API
 */
@Deprecated
@InternalApi
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

    @Override
    public void setXPath(final String xpath) {
        this.xpath = xpath;
    }

    @Override
    public void setVersion(String version) throws UnsupportedOperationException {
        if (!isSupportedVersion(version)) {
            throw new UnsupportedOperationException(
                    this.getClass().getSimpleName() + " does not support XPath version: " + version);
        }
        this.version = version;
    }

    /**
     * Subclasses should implement to indicate whether an XPath version is
     * supported.
     *
     * @param version
     *            The XPath version.
     * @return <code>true</code> if the XPath version is supported,
     *         <code>false</code> otherwise.
     */
    protected abstract boolean isSupportedVersion(String version);

    @Override
    public void setProperties(Map<PropertyDescriptor<?>, Object> properties) {
        this.properties = properties;
    }

    @Override
    public List<String> getRuleChainVisits() {
        return ruleChainVisits;
    }

    @Override
    public abstract List<Node> evaluate(Node node, RuleContext data);
}
