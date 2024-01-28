/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.reporting.FileAnalysisListener;

/**
 * Makes package private methods available for other packages. This is only internal.
 *
 * @apiNote Internal API
 */
@InternalApi
public final class InternalApiBridge {
    private InternalApiBridge() {
        // utility class
    }

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
}
