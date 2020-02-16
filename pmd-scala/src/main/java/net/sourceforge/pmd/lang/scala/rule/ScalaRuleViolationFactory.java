/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

/**
 * @deprecated See {@link RuleViolationFactory}
 */
@Deprecated
@InternalApi
public class ScalaRuleViolationFactory extends AbstractRuleViolationFactory {
    /**
     * The shared singleton of this RuleViolationFactory.
     */
    public static final RuleViolationFactory INSTANCE = new ScalaRuleViolationFactory();

    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
        return new ParametricRuleViolation<Node>(rule, ruleContext, node, message);
    }

    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine) {
        ParametricRuleViolation<Node> rv = new ParametricRuleViolation<>(rule, ruleContext, node, message);
        rv.setLines(beginLine, endLine);
        return rv;
    }

}
