/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule;

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
import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;

public final class VmRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final RuleViolationFactory INSTANCE = new VmRuleViolationFactory();

    private VmRuleViolationFactory() {
    }

    @Override
    protected RuleViolation createRuleViolation(final Rule rule, final RuleContext ruleContext, final Node node,
            final String message) {
        return new ParametricRuleViolation<>(rule, ruleContext, (AbstractVmNode) node, message);
    }

    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        return new AutoFixableParametricRuleViolation<>(rule, ruleContext, (AbstractVmNode) node, message, ruleViolationFixClass);
    }

    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, int beginLine, int endLine, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        final AutoFixableParametricRuleViolation<AbstractVmNode> violation = new AutoFixableParametricRuleViolation<>(rule, ruleContext, (AbstractVmNode) node, message, ruleViolationFixClass);
        violation.setLines(beginLine, endLine);
        return violation;
    }

    @Override
    protected RuleViolation createRuleViolation(final Rule rule, final RuleContext ruleContext, final Node node,
            final String message, final int beginLine, final int endLine) {
        final ParametricRuleViolation<AbstractVmNode> violation = new ParametricRuleViolation<>(rule, ruleContext,
                (AbstractVmNode) node, message);
        violation.setLines(beginLine, endLine);
        return violation;
    }
}
