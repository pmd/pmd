package net.sourceforge.pmd.lang.rule.xpath;

import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This is a Saxon based XPathRule query.
 */
public class SaxonXPathRuleQuery extends AbstractXPathRuleQuery {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportedVersion(String version) {
	return "1.0 compatibility".equals(version) || "2.0".equals(version);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Node> evaluate(Node node, RuleContext data) {
	// TODO Implement
	return null;
    }

}
