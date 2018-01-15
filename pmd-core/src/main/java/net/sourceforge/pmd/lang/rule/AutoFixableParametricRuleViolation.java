/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule;

import static java.util.Objects.requireNonNull;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.autofix.AutoFixableRuleViolation;
import net.sourceforge.pmd.autofix.RuleViolationFix;
import net.sourceforge.pmd.lang.ast.Node;

public class AutoFixableParametricRuleViolation<T extends Node> extends ParametricRuleViolation<T> implements AutoFixableRuleViolation {

    private final Class<? extends RuleViolationFix> ruleViolationFixClass;

    public AutoFixableParametricRuleViolation(Rule rule, RuleContext ctx, T node, String message, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        super(rule, ctx, node, message);
        this.ruleViolationFixClass = requireNonNull(ruleViolationFixClass);
    }

    @Override
    public Class<? extends RuleViolationFix> getRuleViolationFix() {
        return ruleViolationFixClass;
    }
}
