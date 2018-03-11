/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.util.fxdesigner.util.PropertyDescriptorSpec;


/**
 * Evaluates XPath expressions.
 *
 * @author Clément Fournier
 * @since 6.0.0
 */
public class XPathEvaluator {


    /**
     * Evaluates an XPath query on the compilation unit.
     *
     * @param compilationUnit AST root
     * @param languageVersion language version
     * @param xpathVersion    XPath version
     * @param xpathQuery      XPath query
     * @param properties      Properties of the rule
     *
     * @throws XPathEvaluationException if there was an error during the evaluation. The cause is preserved
     */
    public List<Node> evaluateQuery(Node compilationUnit,
                                    LanguageVersion languageVersion,
                                    String xpathVersion,
                                    String xpathQuery,
                                    List<PropertyDescriptorSpec> properties) throws XPathEvaluationException {

        if (StringUtils.isBlank(xpathQuery)) {
            return Collections.emptyList();
        }

        try {
            List<Node> results = new ArrayList<>();

            XPathRule xpathRule = new XPathRule() {
                @Override
                public void addViolation(Object data, Node node, String arg) {
                    results.add(node);
                }
            };


            xpathRule.setMessage("");
            xpathRule.setLanguage(languageVersion.getLanguage());
            xpathRule.setXPath(xpathQuery);
            xpathRule.setVersion(xpathVersion);

            properties.stream()
                      .map(PropertyDescriptorSpec::build)
                      .forEach(xpathRule::definePropertyDescriptor);

            final RuleSet ruleSet = new RuleSetFactory().createSingleRuleRuleSet(xpathRule);

            RuleSets ruleSets = new RuleSets(ruleSet);

            RuleContext ruleContext = new RuleContext();
            ruleContext.setLanguageVersion(languageVersion);

            List<Node> nodes = new ArrayList<>();
            nodes.add(compilationUnit);
            ruleSets.apply(nodes, ruleContext, xpathRule.getLanguage());

            return results;

        } catch (RuntimeException e) {
            throw new XPathEvaluationException(e);
        }
    }
}
