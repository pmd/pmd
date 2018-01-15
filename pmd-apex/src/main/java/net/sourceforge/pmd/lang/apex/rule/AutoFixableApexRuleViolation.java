/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import java.util.Objects;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.autofix.AutoFixableRuleViolation;
import net.sourceforge.pmd.autofix.RuleViolationFix;
import net.sourceforge.pmd.lang.ast.Node;

public class AutoFixableApexRuleViolation extends ApexRuleViolation implements AutoFixableRuleViolation {

    private final Class<? extends RuleViolationFix> ruleViolationFixClass;

    public AutoFixableApexRuleViolation(Rule rule, RuleContext ctx, Node node, String message, int beginLine, int endLine, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        super(rule, ctx, node, message, beginLine, endLine);
        this.ruleViolationFixClass = Objects.requireNonNull(ruleViolationFixClass);
    }

    public AutoFixableApexRuleViolation(Rule rule, RuleContext ctx, Node node, String message, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        super(rule, ctx, node, message);
        this.ruleViolationFixClass = Objects.requireNonNull(ruleViolationFixClass);
    }

    @Override
    public Class<? extends RuleViolationFix> getRuleViolationFix() {
        return ruleViolationFixClass;
    }
}
