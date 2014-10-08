/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.rule.xpath;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This interface captures the logic needed by XPathRule to implement an
 * XPath based query on an AST Node.
 * <p>
 * Implementations of this class do not need to be thread-safe, but they will
 * be reused to query against different AST Nodes.  Therefore, internal state
 * should be maintained in a fashion consistent with reuse.  Further,
 * implementations are recommended to manage internal state that is invariant
 * over AST Nodes in a fashion which facilities high performance (e.g. caching).
 */
public interface XPathRuleQuery {

    /**
     * XPath 1.0 version.
     */
    String XPATH_1_0 = "1.0";

    /**
     * XPath 1.0 compatibility version.
     */
    String XPATH_1_0_COMPATIBILITY = "1.0 compatibility";

    /**
     * XPath 2.0 version.
     */
    String XPATH_2_0 = "2.0";

    /**
     * Set the XPath query string to be used.
     * @param xpath The XPath query string.
     */
    void setXPath(String xpath);

    /**
     * Set the XPath version to be used.
     * @param version The XPath version.
     * @throws UnsupportedOperationException if the version cannot be handled.
     */
    void setVersion(String version) throws UnsupportedOperationException;

    /**
     * Set the properties to use during the XPath query.
     */
    void setProperties(Map<PropertyDescriptor<?>, Object> properties);

    /**
     * Indicates which AST Nodes (if any) should be used with the RuleChain.
     * Use of the RuleChain will allow the query execute on a targed sub-tree
     * of the AST, instead of the entire AST from the root.  This can result
     * in great performance benefits.
     */
    List<String> getRuleChainVisits();

    /**
     * Evaluate the XPath query against the given Node.
     * @param node The Node.
     * @param data The RuleContext.
     * @return The matching Nodes.
     */
    List<Node> evaluate(Node node, RuleContext data);
}
