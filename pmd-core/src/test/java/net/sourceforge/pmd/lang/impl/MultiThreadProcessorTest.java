/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.reporting.RuleContext;

class MultiThreadProcessorTest extends AbstractPMDProcessorTest {

    @Override
    protected int getThreads() {
        return 2;
    }

    @Override
    protected Class<? extends AbstractPMDProcessor> getExpectedImplementation() {
        return MultiThreadProcessor.class;
    }

    private PmdAnalysis createPmdAnalysis(final String ruleset) {
        PmdAnalysis pmd = createPmdAnalysis();
        pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource(ruleset));
        return pmd;
    }

    @Test
    void errorsShouldBeThrown() {
        // in multithreading mode, the errors are detected when closing PmdAnalysis
        Error error = assertThrows(Error.class, () -> {
            try (PmdAnalysis pmd = createPmdAnalysis()) {
                pmd.addRuleSet(RuleSet.forSingleRule(new RuleThatThrowsError()));
                pmd.performAnalysis();
            }
        });
        assertEquals("test error", error.getMessage());

        // in multithreading mode, all files are started but eventually fail
        // depending on how many tasks have been started before getting the first results
        // we might have started only one file analysis or more. But we rethrow
        // the error on the first.
        assertTrue(reportListener.files.get() >= 1);
        // we report the first error
        Mockito.verify(reporter).error(Mockito.eq("Unknown error occurred while executing a PmdRunnable: {0}"),
                Mockito.eq("java.lang.Error: test error"),
                Mockito.any(Error.class));
    }

    // TODO: Dysfunctional rules are pruned upstream of the processor.
    //
    //    @Test
    //    void testRulesDysnfunctionalLog() throws Exception {
    //        RuleSets ruleSets = setUpForTest("rulesets/MultiThreadProcessorTest/dysfunctional.xml");
    //        final SimpleRenderer renderer = new SimpleRenderer(null, null);
    //        renderer.start();
    //        processor.processFiles(ruleSets, files, listener);
    //        renderer.end();
    //
    //        final Iterator<ConfigurationError> configErrors = renderer.getReport().getConfigurationErrors().iterator();
    //        final ConfigurationError error = configErrors.next();
    //
    //        assertEquals("Dysfunctional rule message not present",
    //                DysfunctionalRule.DYSFUNCTIONAL_RULE_REASON, error.issue());
    //        assertEquals("Dysfunctional rule is wrong",
    //                DysfunctionalRule.class, error.rule().getClass());
    //        assertFalse("More configuration errors found than expected", configErrors.hasNext());
    //    }

    @Test
    void testRulesThreadSafety() throws Exception {
        try (PmdAnalysis pmd = createPmdAnalysis("rulesets/MultiThreadProcessorTest/basic.xml")) {
            pmd.performAnalysis();
        }

        // if the rule is not executed, then maybe a
        // ConcurrentModificationException happened
        assertEquals(2, NotThreadSafeRule.count.get(), "Test rule has not been executed");
        // if the violation is not reported, then the rule instances have been
        // shared between the threads
        assertEquals(1, reportListener.violations.get(), "Missing violation");
    }

    public static class NotThreadSafeRule extends AbstractRule {
        public static AtomicInteger count = new AtomicInteger(0);
        private boolean hasViolation; // this variable will be overridden
        // between the threads

        @Override
        public void apply(Node target, RuleContext ctx) {
            count.incrementAndGet();

            if (target.getTextDocument().getFileId().getOriginalPath().contains("violation")) {
                hasViolation = true;
            } else {
                letTheOtherThreadRun(10);
                hasViolation = false;
            }

            letTheOtherThreadRun(100);
            if (hasViolation) {
                ctx.addViolation(target);
            }
        }

        private void letTheOtherThreadRun(int millis) {
            try {
                Thread.yield();
                Thread.sleep(millis);
            } catch (InterruptedException e) {
                // ignored
            }
        }
    }

    public static class DysfunctionalRule extends AbstractRule {

        public static final String DYSFUNCTIONAL_RULE_REASON = "dysfunctional rule is dysfunctional";

        @Override
        public void apply(Node target, RuleContext ctx) {
            // noop
        }

        @Override
        public String dysfunctionReason() {
            return DYSFUNCTIONAL_RULE_REASON;
        }
    }

}
