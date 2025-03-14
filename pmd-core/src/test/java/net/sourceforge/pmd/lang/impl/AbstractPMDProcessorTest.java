/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.InternalApiBridge;
import net.sourceforge.pmd.lang.LanguageProcessor;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.reporting.RuleViolation;
import net.sourceforge.pmd.util.log.PmdReporter;

abstract class AbstractPMDProcessorTest {
    protected SimpleReportListener reportListener;

    protected PmdReporter reporter;

    protected abstract int getThreads();

    protected abstract Class<? extends AbstractPMDProcessor> getExpectedImplementation();

    @Test
    void shouldUseCorrectProcessorImpl() {
        try (AbstractPMDProcessor processor = AbstractPMDProcessor.newFileProcessor(createTask(getThreads()))) {
            assertSame(getExpectedImplementation(), processor.getClass());
        }
    }

    private LanguageProcessor.AnalysisTask createTask(int threads) {
        return InternalApiBridge.createAnalysisTask(null, null, null, threads, null, null, null);
    }

    @Test
    void exceptionsShouldBeLogged() {
        try (PmdAnalysis pmd = createPmdAnalysis()) {
            pmd.addRuleSet(RuleSet.forSingleRule(new RuleThatThrowsException()));
            pmd.performAnalysis();
        }

        assertEquals(2, reportListener.files.get());
        assertEquals(2, reportListener.errors.get());
        // exceptions are reported as processing errors
        Mockito.verifyNoInteractions(reporter);
    }

    protected PmdAnalysis createPmdAnalysis() {
        PMDConfiguration configuration = new PMDConfiguration();
        configuration.setThreads(getThreads());
        configuration.setIgnoreIncrementalAnalysis(true);
        reporter = Mockito.spy(configuration.getReporter());
        configuration.setReporter(reporter);

        PmdAnalysis pmd = PmdAnalysis.create(configuration);
        LanguageVersion lv = DummyLanguageModule.getInstance().getDefaultVersion();
        pmd.files().addFile(TextFile.forCharSeq("abc", FileId.fromPathLikeString("file1-violation.dummy"), lv));
        pmd.files().addFile(TextFile.forCharSeq("DEF", FileId.fromPathLikeString("file2-foo.dummy"), lv));

        reportListener = new SimpleReportListener();
        GlobalAnalysisListener listener = GlobalAnalysisListener.tee(listOf(
                new Report.GlobalReportBuilderListener(),
                reportListener
        ));


        pmd.addListener(listener);
        return pmd;
    }

    protected static class RuleThatThrowsException extends AbstractRule {
        RuleThatThrowsException() {
            setLanguage(DummyLanguageModule.getInstance().getDefaultVersion().getLanguage());
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
            throw new RuntimeException("test exception");
        }
    }

    protected static class RuleThatThrowsError extends AbstractRule {
        RuleThatThrowsError() {
            setLanguage(DummyLanguageModule.getInstance().getDefaultVersion().getLanguage());
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
            throw new Error("test error");
        }
    }

    protected static class SimpleReportListener implements GlobalAnalysisListener {

        public AtomicInteger violations = new AtomicInteger(0);
        public AtomicInteger files = new AtomicInteger(0);
        public AtomicInteger errors = new AtomicInteger(0);

        @Override
        public FileAnalysisListener startFileAnalysis(TextFile file) {
            files.incrementAndGet();

            return new FileAnalysisListener() {
                @Override
                public void onRuleViolation(RuleViolation violation) {
                    violations.incrementAndGet();
                }

                @Override
                public void onError(Report.ProcessingError error) {
                    errors.incrementAndGet();
                }
            };
        }

        @Override
        public void close() throws Exception {

        }
    }
}
