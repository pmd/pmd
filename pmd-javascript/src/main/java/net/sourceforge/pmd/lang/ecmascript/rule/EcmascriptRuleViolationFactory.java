/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.autofix.AutoFixableRuleViolation;
import net.sourceforge.pmd.autofix.RuleViolationFix;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptNode;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.AutoFixableParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public final class EcmascriptRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final EcmascriptRuleViolationFactory INSTANCE = new EcmascriptRuleViolationFactory();

    private EcmascriptRuleViolationFactory() {
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
        return new ParametricRuleViolation<>(rule, ruleContext, (EcmascriptNode) node, message);
    }

    @SuppressWarnings("rawtypes")
    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        return new AutoFixableParametricRuleViolation<>(rule, ruleContext, node, message, ruleViolationFixClass);
    }

    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, int beginLine, int endLine, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        AutoFixableParametricRuleViolation<EcmascriptNode> rViolation = new AutoFixableParametricRuleViolation<>(rule, ruleContext, (EcmascriptNode) node, message, ruleViolationFixClass);
        rViolation.setLines(beginLine, endLine);
        return rViolation;
    }

    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine) {
        ParametricRuleViolation<EcmascriptNode> rViolation = new ParametricRuleViolation<>(rule, ruleContext, (EcmascriptNode) node, message);
        rViolation.setLines(beginLine, endLine);
        return rViolation;
    }
}
