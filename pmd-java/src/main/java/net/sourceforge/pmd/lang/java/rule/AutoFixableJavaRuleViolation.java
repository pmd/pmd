/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import static java.util.Objects.requireNonNull;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.autofix.AutoFixableRuleViolation;
import net.sourceforge.pmd.autofix.RuleViolationFix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

public class AutoFixableJavaRuleViolation extends JavaRuleViolation implements AutoFixableRuleViolation {

    private final Class<? extends RuleViolationFix> ruleViolationFixClass;

    public AutoFixableJavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node, String message, int beginLine, int endLine, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        super(rule, ctx, node, message, beginLine, endLine);
        this.ruleViolationFixClass = requireNonNull(ruleViolationFixClass);
    }

    public AutoFixableJavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node, String message, Class<? extends RuleViolationFix> ruleViolationFixClass) {
        super(rule, ctx, node, message);
        this.ruleViolationFixClass = requireNonNull(ruleViolationFixClass);
    }

    @Override
    public Class<? extends RuleViolationFix> getRuleViolationFix() {
        return ruleViolationFixClass;
    }
}
