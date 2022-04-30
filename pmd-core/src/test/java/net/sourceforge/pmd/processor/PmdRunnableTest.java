/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;
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
import net.sourceforge.pmd.internal.util.ContextedAssertionError;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.document.TextFile;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.processor.MonoThreadProcessor.MonothreadRunnable;
import net.sourceforge.pmd.util.log.MessageReporter;

public class PmdRunnableTest {

    @org.junit.Rule
    public TestRule restoreSystemProperties = new RestoreSystemProperties();

    private LanguageVersion dummyThrows;
    private LanguageVersion dummyDefault;
    private LanguageVersion dummySemanticError;
    private PMDConfiguration configuration;
    private PmdRunnable pmdRunnable;
    private MessageReporter reporter;
    private Rule rule;


    @Before
    public void prepare() {
        Language dummyLanguage = LanguageRegistry.findLanguageByTerseName(DummyLanguageModule.TERSE_NAME);
        dummyDefault = dummyLanguage.getDefaultVersion();
        dummyThrows = dummyLanguage.getVersion("1.9-throws");
        dummySemanticError = dummyLanguage.getVersion("1.9-semantic_error");

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
    public void inErrorRecoveryModeErrorsShouldBeLoggedByParser() {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");

        Report report = process(dummyThrows);

        Assert.assertEquals(1, report.getProcessingErrors().size());
    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByRule() {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");

        Report report = process(dummyDefault);

        List<ProcessingError> errors = report.getProcessingErrors();
        assertThat(errors, hasSize(1));
        assertThat(errors.get(0).getError(), instanceOf(ContextedAssertionError.class));
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByParser() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));

        Assert.assertThrows(AssertionError.class, () -> process(dummyThrows));
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByRule() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));


        Assert.assertThrows(AssertionError.class, () -> process(dummyDefault));
    }


    @Test
    public void semanticErrorShouldAbortTheRun() {
        process(dummySemanticError);

        verify(reporter).log(eq(Level.INFO), contains("skipping rule analysis"));
        verify(rule, never()).apply(Mockito.any(), Mockito.any());
    }

    private static class RuleThatThrows extends AbstractRule {

        RuleThatThrows() {
            Language dummyLanguage = LanguageRegistry.findLanguageByTerseName(DummyLanguageModule.TERSE_NAME);
            setLanguage(dummyLanguage);
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
            throw new AssertionError("test");
        }
    }
}
