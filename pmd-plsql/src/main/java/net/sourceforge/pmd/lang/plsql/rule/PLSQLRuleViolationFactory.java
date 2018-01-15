/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.autofix.AutoFixableRuleViolation;
import net.sourceforge.pmd.autofix.RuleViolationFix;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolationFactory;
import net.sourceforge.pmd.lang.rule.AutoFixableParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public final class PLSQLRuleViolationFactory extends AbstractRuleViolationFactory {

    public static final RuleViolationFactory INSTANCE = new PLSQLRuleViolationFactory();

    private PLSQLRuleViolationFactory() {
    }

    @Override
    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message) {
        return new ParametricRuleViolation<>(rule, ruleContext, node, message);
    }

    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        return new AutoFixableParametricRuleViolation<>(rule, ruleContext, (PLSQLNode) node, message, ruleViolationFixClass);
    }

    @Override
    protected AutoFixableRuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message, int beginLine, int endLine, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        final AutoFixableParametricRuleViolation<Node> violation = new AutoFixableParametricRuleViolation<>(rule, ruleContext, node, message, ruleViolationFixClass);
        violation.setLines(beginLine, endLine);
        return violation;
    }

    protected RuleViolation createRuleViolation(Rule rule, RuleContext ruleContext, Node node, String message,
            int beginLine, int endLine) {
        ParametricRuleViolation<Node> violation = new ParametricRuleViolation<>(rule, ruleContext, node, message);
        violation.setLines(beginLine, endLine);
        return violation;
    }
}
