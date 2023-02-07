/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static net.sourceforge.pmd.lang.DummyLanguageModule.TEST_MESSAGE_SEMANTIC_ERROR;
import static net.sourceforge.pmd.lang.DummyLanguageModule.getVersionWithParserThatThrowsSemanticError;
import static net.sourceforge.pmd.lang.DummyLanguageModule.versionWithParserThatReportsSemanticError;
import static net.sourceforge.pmd.lang.DummyLanguageModule.versionWithParserThatThrowsAssertionError;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.event.Level;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.processor.MonoThreadProcessor.MonothreadRunnable;
import net.sourceforge.pmd.util.ContextedAssertionError;
import net.sourceforge.pmd.util.log.MessageReporter;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class PmdRunnableTest {

    private PMDConfiguration configuration;
    private PmdRunnable pmdRunnable;
    private MessageReporter reporter;
    private Rule rule;


    @BeforeEach
    public void prepare() {
        // reset data
        rule = spy(new RuleThatThrows());
        configuration = new PMDConfiguration();
        reporter = mock(MessageReporter.class);
        configuration.setReporter(reporter);

        // will be populated by a call to process(LanguageVersion)
        pmdRunnable = null;
    }


    private Report process(LanguageVersion lv) {
        TextFile dataSource = TextFile.forCharSeq("test", "test.dummy", lv);

        GlobalReportBuilderListener reportBuilder = new GlobalReportBuilderListener();

        pmdRunnable = new MonothreadRunnable(new RuleSets(RuleSet.forSingleRule(rule)),
                                             dataSource,
                                             reportBuilder,
                                             configuration);

        pmdRunnable.run();
        reportBuilder.close();
        return reportBuilder.getResult();
    }

    @Test
    void inErrorRecoveryModeErrorsShouldBeLoggedByParser() throws Exception {
        SystemLambda.restoreSystemProperties(() -> {
            System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");

            Report report = process(versionWithParserThatThrowsAssertionError());

            assertEquals(1, report.getProcessingErrors().size());
        });
    }

    @Test
    void inErrorRecoveryModeErrorsShouldBeLoggedByRule() throws Exception {
        SystemLambda.restoreSystemProperties(() -> {
            System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");

            Report report = process(DummyLanguageModule.getInstance().getDefaultVersion());

            List<ProcessingError> errors = report.getProcessingErrors();
            assertThat(errors, hasSize(1));
            assertThat(errors.get(0).getError(), instanceOf(ContextedAssertionError.class));
        });

    }

    @Test
    void withoutErrorRecoveryModeProcessingShouldBeAbortedByParser() throws Exception {
        SystemLambda.restoreSystemProperties(() -> {
            System.clearProperty(SystemProps.PMD_ERROR_RECOVERY);
            assertThrows(AssertionError.class, () -> process(versionWithParserThatThrowsAssertionError()));
        });
    }

    @Test
    void withoutErrorRecoveryModeProcessingShouldBeAbortedByRule() throws Exception {
        SystemLambda.restoreSystemProperties(() -> {
            System.clearProperty(SystemProps.PMD_ERROR_RECOVERY);
            assertThrows(AssertionError.class, () -> process(DummyLanguageModule.getInstance().getDefaultVersion()));
        });
    }


    @Test
    void semanticErrorShouldAbortTheRun() {
        Report report = process(versionWithParserThatReportsSemanticError());

        verify(reporter, times(1))
            .log(eq(Level.ERROR), eq("at !debug only! test.dummy:1:1: " + TEST_MESSAGE_SEMANTIC_ERROR));
        verify(rule, never()).apply(Mockito.any(), Mockito.any());

        assertEquals(1, report.getProcessingErrors().size());
    }

    @Test
    void semanticErrorThrownShouldAbortTheRun() {
        Report report = process(getVersionWithParserThatThrowsSemanticError());

        verify(reporter, times(1)).log(eq(Level.ERROR), contains(TEST_MESSAGE_SEMANTIC_ERROR));
        verify(rule, never()).apply(Mockito.any(), Mockito.any());

        assertEquals(1, report.getProcessingErrors().size());
    }

    private static class RuleThatThrows extends AbstractRule {

        RuleThatThrows() {
            Language dummyLanguage = DummyLanguageModule.getInstance();
            setLanguage(dummyLanguage);
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
            throw new AssertionError("test");
        }
    }
}
