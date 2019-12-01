/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm.strategies;

import java.util.HashMap;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.SaxonXPathRuleQuery;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.properties.PropertyDescriptor;

import com.beust.jcommander.Parameter;

/**
 * Drops all nodes matched by the specified XPath 2.0 expression, then exits.
 */
public class XPathStrategy extends AbstractMinimizationStrategy {
    public static class Configuration extends AbstractConfiguration {
        @Parameter(names = "--xpath-expression", description = "XPath 2.0 expression to drop matched subtrees", required = true)
        private String expression;

        public String getExpression() {
            return expression;
        }

        @Override
        public MinimizationStrategy createStrategy() {
            return new XPathStrategy(this);
        }
    }

    public static final MinimizationStrategyConfigurationFactory FACTORY = new AbstractFactory("xpath") {
        @Override
        public MinimizationStrategyConfiguration createConfiguration() {
            return new Configuration();
        }
    };

    private final XPathRuleQuery query;

    private XPathStrategy(Configuration configuration) {
        super(configuration);
        query = new SaxonXPathRuleQuery();
        query.setProperties(new HashMap<PropertyDescriptor<?>, Object>());
        query.setXPath(configuration.expression);
    }

    @Override
    public void performSinglePass(Node currentRoot) throws Exception {
        List<Node> nodesToRemove = query.evaluate(currentRoot, null);
        ops.forceRemoveNodesAndExit(nodesToRemove);
    }
}
