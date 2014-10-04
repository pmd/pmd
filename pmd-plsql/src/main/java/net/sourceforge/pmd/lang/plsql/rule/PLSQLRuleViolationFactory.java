/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public final class PLSQLRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final RuleViolationFactory INSTANCE = new PLSQLRuleViolationFactory();

    private PLSQLRuleViolationFactory() {
    }

    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
    	return new ParametricRuleViolation<Node>(rule, ruleContext, node, message);
    }
    
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, int beginLine, int endLine) {
        ParametricRuleViolation<Node> violation = new ParametricRuleViolation<Node>(rule, ruleContext, node, message);
        violation.setLines(beginLine, endLine);
		return violation;
	}
}


