/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.antlr;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.AntlrBaseNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public final class AntlrRuleViolationFactory extends AbstractRuleViolationFactory {
    public static final RuleViolationFactory INSTANCE = new AntlrRuleViolationFactory();

    private AntlrRuleViolationFactory() {
    }

    @Override
    protected RuleViolation createRuleViolation(final Rule rule, final RuleContext ruleContext, final Node node,
                                                final String message) {
        return new ParametricRuleViolation<>(rule, ruleContext, (AntlrBaseNode) node, message);
    }

    @Override
    protected RuleViolation createRuleViolation(final Rule rule, final RuleContext ruleContext, final Node node,
                                                final String message, final int beginLine, final int endLine) {
        final ParametricRuleViolation<AntlrBaseNode> violation = new ParametricRuleViolation<>(rule, ruleContext,
                (AntlrBaseNode) node, message);
        violation.setLines(beginLine, endLine);
        return violation;
    }
}
