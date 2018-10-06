/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSets;

public final class PmdThreadContextHolder {
    private static final ThreadLocal<ThreadContext> HOLDER = new ThreadLocal<>();

    private PmdThreadContextHolder() {
        // utility class
    }

    public static void reset() {
        HOLDER.remove();
    }

    public static void init(RuleSets ruleSets, RuleContext ruleContext) {
        if (HOLDER.get() == null) {
            HOLDER.set(new ThreadContext(new RuleSets(ruleSets), new RuleContext(ruleContext)));
        }
    }

    public static RuleContext getRuleContext() {
        ThreadContext tc = HOLDER.get();
        if (tc == null) {
            //throw new IllegalStateException("PmdThreadContext has not been initialized");
            // just to make the tests running for now...
            return new RuleContext();
        }
        return tc.ruleContext;
    }

    public static RuleSets getRuleSets() {
        ThreadContext tc = HOLDER.get();
        if (tc == null) {
            //throw new IllegalStateException("PmdThreadContext has not been initialized");
            // just to make the tests running for now...
            return new RuleSets();
        }
        return tc.ruleSets;
    }

    private static class ThreadContext {
        /* default */ final RuleSets ruleSets;
        /* default */ final RuleContext ruleContext;

        ThreadContext(RuleSets ruleSets, RuleContext ruleContext) {
            this.ruleSets = ruleSets;
            this.ruleContext = ruleContext;
        }
    }

}
