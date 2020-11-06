/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;
import net.sourceforge.pmd.lang.vm.ast.AbstractVmNode;

/**
 * @deprecated See {@link RuleViolationFactory}
 */
@Deprecated
@InternalApi
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
    protected RuleViolation createRuleViolation(final Rule rule, final RuleContext ruleContext, final Node node,
            final String message, final int beginLine, final int endLine) {
        final ParametricRuleViolation<AbstractVmNode> violation = new ParametricRuleViolation<>(rule, ruleContext,
                (AbstractVmNode) node, message);
        violation.setLines(beginLine, endLine);
        return violation;
    }
}
