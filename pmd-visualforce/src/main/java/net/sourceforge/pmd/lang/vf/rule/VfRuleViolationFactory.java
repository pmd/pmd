/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.autofix.AutoFixableRuleViolation;
import net.sourceforge.pmd.autofix.RuleViolationFix;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.AutoFixableParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.vf.ast.VfNode;

public final class VfRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final RuleViolationFactory INSTANCE = new VfRuleViolationFactory();

    private VfRuleViolationFactory() {
    }

    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
        return new ParametricRuleViolation<>(rule, ruleContext, (VfNode) node, message);
    }

    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        return new AutoFixableParametricRuleViolation<>(rule, ruleContext, (VfNode) node, message, ruleViolationFixClass);
    }

    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, int beginLine, int endLine, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        AutoFixableParametricRuleViolation<VfNode> rViolation = new AutoFixableParametricRuleViolation<>(rule, ruleContext, (VfNode) node, message, ruleViolationFixClass);
        rViolation.setLines(beginLine, endLine);
        return rViolation;
    }

    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine) {
        ParametricRuleViolation<VfNode> rViolation = new ParametricRuleViolation<>(rule, ruleContext, (VfNode) node, message);
        rViolation.setLines(beginLine, endLine);
        return rViolation;
    }
}
