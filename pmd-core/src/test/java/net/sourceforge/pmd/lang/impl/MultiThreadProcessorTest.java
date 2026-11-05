/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.Dummy2LanguageModule;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.renderers.TextRenderer;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.util.log.PmdReporter;

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

    /**
     * Reproducer for #6869: when one file fails with an Error, while another file
     * is still running and later files are already done (their reports are buffered
     * by the DeterministicOutputListenerWrapper of the renderer), the original
     * Error must be thrown - not an AssertionError from closing the listeners.
     */
    @Test
    void errorShouldNotBeHiddenWhenOtherFilesAreStillRunning() {
        // one thread for each of a-error, b-slow and the c files
        PMDConfiguration configuration = createConfiguration(3);
        StringWriter output = new StringWriter();
        TextRenderer renderer = new TextRenderer();
        renderer.setWriter(output);

        Error error = assertThrows(Error.class, () -> {
            try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
                LanguageVersion lv = DummyLanguageModule.getInstance().getDefaultVersion();
                for (String name : new String[] {"a-error", "b-slow", "c1", "c2", "c3"}) {
                    pmd.files().addFile(TextFile.forCharSeq("abc", FileId.fromPathLikeString(name + ".dummy"), lv));
                }
                pmd.addRuleSet(RuleSet.forSingleRule(new ErrorOrSlowRule()));
                pmd.addRenderer(renderer);
                pmd.performAnalysis();
            }
        });

        assertEquals("test error", error.getMessage());
        // the files that were running or already done are still reported
        String report = output.toString();
        for (String name : new String[] {"b-slow", "c1", "c2", "c3"}) {
            assertTrue(report.contains(name + ".dummy"), "Missing report for " + name + ": " + report);
        }
    }

    /**
     * When each language fails with an Error, the analyses of all languages must
     * be joined anyway, so that no analysis thread keeps running.
     */
    @Test
    void allLanguageAnalysesShouldBeJoinedAfterAnError() throws InterruptedException {
        PMDConfiguration configuration = createConfiguration(2);

        Error error = assertThrows(Error.class, () -> {
            try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
                pmd.files().addFile(TextFile.forCharSeq("abc", FileId.fromPathLikeString("a-error.dummy"),
                        DummyLanguageModule.getInstance().getDefaultVersion()));
                pmd.files().addFile(TextFile.forCharSeq("abc", FileId.fromPathLikeString("a-error.dummy2"),
                        Dummy2LanguageModule.getInstance().getDefaultVersion()));
                pmd.addRuleSet(RuleSet.forSingleRule(new ErrorOrSlowRule()));
                pmd.addRuleSet(RuleSet.forSingleRule(new ErrorOrSlowDummy2Rule()));
                pmd.performAnalysis();
            }
        });

        assertEquals("test error", error.getMessage());
        // the error of the other language is not lost
        assertEquals(1, error.getSuppressed().length);
        assertNoPmdThreadAlive();
    }

    private PMDConfiguration createConfiguration(int threads) {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setThreads(threads);
        configuration.setIgnoreIncrementalAnalysis(true);
        reporter = Mockito.mock(PmdReporter.class);
        configuration.setReporter(reporter);
        return configuration;
    }

    private static void assertNoPmdThreadAlive() throws InterruptedException {
        // The pool threads exit shortly after the executor has been shut down
        List<Thread> pmdThreads = new ArrayList<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getName().startsWith("PmdThread")) {
                pmdThreads.add(thread);
            }
        }
        for (Thread thread : pmdThreads) {
            thread.join(5000);
            assertFalse(thread.isAlive(), "Analysis thread is still running: " + thread.getName());
        }
    }

    /**
     * Throws an Error for files named "a-error", busy-waits for files named
     * "b-slow" (ignoring interrupts, like a long-running analysis would) and
     * reports a violation otherwise.
     *
     * <p>Needs an accessible (non-private) no-arg constructor, because each analysis thread uses
     * a copy of the rule.
     */
    static class ErrorOrSlowRule extends AbstractRule {
        ErrorOrSlowRule() {
            this(DummyLanguageModule.getInstance());
        }

        ErrorOrSlowRule(Language language) {
            setLanguage(language);
            setName("ErrorOrSlowRule-" + language.getId());
            setMessage("violation");
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
            String fileName = target.getTextDocument().getFileId().getFileName();
            if (fileName.startsWith("a-error")) {
                // give the other threads time to start b-slow and finish the c files
                busyWait(200);
                throw new Error("test error");
            }
            if (fileName.startsWith("b-slow")) {
                busyWait(1000);
            }
            ctx.addViolation(target);
        }

        private static void busyWait(long millis) {
            long end = System.nanoTime() + millis * 1_000_000L;
            while (System.nanoTime() < end) {
                Thread.yield();
            }
        }
    }

    /** Same as {@link ErrorOrSlowRule}, for the second language. */
    static class ErrorOrSlowDummy2Rule extends ErrorOrSlowRule {
        ErrorOrSlowDummy2Rule() {
            super(Dummy2LanguageModule.getInstance());
        }
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
