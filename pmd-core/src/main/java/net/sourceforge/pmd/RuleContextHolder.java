/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.annotation.Experimental;

@Experimental
public final class RuleContextHolder {

    private RuleContextHolder() {
    }

    private static final ThreadLocal<RuleContext> THREAD_LOCAL = new ThreadLocal<>();

    public static RuleContext get() {
        return THREAD_LOCAL.get();
    }

    public static void set(RuleContext ruleContext) {
        THREAD_LOCAL.set(ruleContext);
    }

    public static void reset() {
        THREAD_LOCAL.remove();
    }
}
