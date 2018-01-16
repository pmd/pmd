/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.autofix.AutoFixableRuleViolation;
import net.sourceforge.pmd.autofix.RuleViolationFix;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.jsp.ast.JspNode;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.AutoFixableParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public final class JspRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final RuleViolationFactory INSTANCE = new JspRuleViolationFactory();

    private JspRuleViolationFactory() {
    }

    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
        return new ParametricRuleViolation<>(rule, ruleContext, (JspNode) node, message);
    }

    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        return new AutoFixableParametricRuleViolation<>(rule, ruleContext, (JspNode) node, message, ruleViolationFixClass);
    }

    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, int beginLine, int endLine, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        AutoFixableParametricRuleViolation<JspNode> rViolation = new AutoFixableParametricRuleViolation<>(rule, ruleContext, (JspNode) node, message, ruleViolationFixClass);
        rViolation.setLines(beginLine, endLine);
        return rViolation;
    }

    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine) {
        ParametricRuleViolation<JspNode> rViolation = new ParametricRuleViolation<>(rule, ruleContext, (JspNode) node, message);
        rViolation.setLines(beginLine, endLine);
        return rViolation;
    }
}
