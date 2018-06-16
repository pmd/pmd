/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;

public final class ApexRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final ApexRuleViolationFactory INSTANCE = new ApexRuleViolationFactory();

    private ApexRuleViolationFactory() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
        return new ApexRuleViolation<>(rule, ruleContext, node, message);
    }

    @Override
    @SuppressWarnings("rawtypes")
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine) {
        return new ApexRuleViolation(rule, ruleContext, node, message, beginLine, endLine);
    }
}
