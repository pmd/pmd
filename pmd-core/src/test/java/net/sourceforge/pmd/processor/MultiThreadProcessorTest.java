/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;

class MultiThreadProcessorTest {

    private SimpleReportListener reportListener;

    PmdAnalysis setupForTest(final String ruleset) {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setThreads(2);
        PmdAnalysis pmd = PmdAnalysis.create(configuration);
        LanguageVersion lv = DummyLanguageModule.getInstance().getDefaultVersion();
        pmd.files().addFile(TextFile.forCharSeq("abc", "file1-violation.dummy", lv));
        pmd.files().addFile(TextFile.forCharSeq("DEF", "file2-foo.dummy", lv));

        reportListener = new SimpleReportListener();
        GlobalAnalysisListener listener = GlobalAnalysisListener.tee(listOf(
            new GlobalReportBuilderListener(),
            reportListener
        ));

        pmd.addListener(listener);
        pmd.addRuleSet(pmd.newRuleSetLoader().loadFromResource(ruleset));
        return pmd;
    }

    // Dysfunctional rules are pruned upstream of the processor.
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
        try (PmdAnalysis pmd = setupForTest("rulesets/MultiThreadProcessorTest/basic.xml")) {
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

            if (target.getTextDocument().getDisplayName().contains("violation")) {
                hasViolation = true;
            } else {
                letTheOtherThreadRun(10);
                hasViolation = false;
            }

            letTheOtherThreadRun(100);
            if (hasViolation) {
                addViolation(ctx, target);
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

    private static class SimpleReportListener implements GlobalAnalysisListener {

        public AtomicInteger violations = new AtomicInteger(0);
        
        @Override
        public FileAnalysisListener startFileAnalysis(TextFile file) {
            return new FileAnalysisListener() {
                @Override
                public void onRuleViolation(RuleViolation violation) {
                    violations.incrementAndGet();
                }
            };
        }

        @Override
        public void close() throws Exception {

        }
    }
}
