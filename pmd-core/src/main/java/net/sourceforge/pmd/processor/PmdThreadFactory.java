/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.processor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSetFactory;

public class PmdThreadFactory implements ThreadFactory {

    private final RuleSetFactory ruleSetFactory;
    private final RuleContext ctx;
    private final AtomicInteger counter = new AtomicInteger();

    public PmdThreadFactory(RuleSetFactory ruleSetFactory, RuleContext ctx) {
        this.ruleSetFactory = ruleSetFactory;
        this.ctx = ctx;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = PmdRunnable.createThread(counter.incrementAndGet(), r, ruleSetFactory, ctx);
        return t;
    }

}
