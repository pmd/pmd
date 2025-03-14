/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.Map;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.rule.Rule;

/**
 * Internal API.
 *
 * <p>Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {}

    public static Rule getRule(RuleContext ruleContext) {
        return ruleContext.getRule();
    }

    /**
     * Create a new RuleContext.
     *
     * The listener must be closed by its creator.
     */
    public static RuleContext createRuleContext(FileAnalysisListener listener, Rule rule) {
        return new RuleContext(listener, rule);
    }

    public static ParametricRuleViolation createRuleViolation(Rule theRule, FileLocation location, String message, Map<String, String> additionalInfo) {
        return new ParametricRuleViolation(theRule, location, message, additionalInfo);
    }
}
