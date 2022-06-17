/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.processor.MonoThreadProcessor.MonothreadRunnable;
import net.sourceforge.pmd.util.datasource.DataSource;

import com.github.stefanbirkner.systemlambda.SystemLambda;

public class PmdRunnableTest {

    private LanguageVersion dummyThrows;
    private LanguageVersion dummyDefault;
    private PMDConfiguration configuration;
    private PmdRunnable pmdRunnable;
    private GlobalReportBuilderListener reportBuilder;

    @BeforeEach
    public void prepare() {
        Language dummyLanguage = LanguageRegistry.findLanguageByTerseName(DummyLanguageModule.TERSE_NAME);
        dummyDefault = dummyLanguage.getDefaultVersion();
        dummyThrows = dummyLanguage.getVersion("1.9-throws");
        DataSource dataSource = DataSource.forString("test", "test.dummy");

        Rule rule = new RuleThatThrows();
        configuration = new PMDConfiguration();
        reportBuilder = new GlobalReportBuilderListener();
        pmdRunnable = new MonothreadRunnable(new RuleSets(RuleSet.forSingleRule(rule)),
                                             dataSource,
                                             reportBuilder,
                                             configuration);

    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByParser() throws Exception {
        SystemLambda.restoreSystemProperties(() -> {
            System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");
            configuration.setDefaultLanguageVersion(dummyThrows);

            pmdRunnable.run();
            reportBuilder.close();
            Assertions.assertEquals(1, reportBuilder.getResult().getProcessingErrors().size());
        });
    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByRule() throws Exception {
        SystemLambda.restoreSystemProperties(() -> {
            System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");
            configuration.setDefaultLanguageVersion(dummyDefault);

            pmdRunnable.run();
            reportBuilder.close();
            Report report = reportBuilder.getResult();
            List<ProcessingError> errors = report.getProcessingErrors();
            assertThat(errors, hasSize(1));
            assertThat(errors.get(0).getError(), instanceOf(ContextedAssertionError.class));
        });
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByParser() {
        Assertions.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));
        configuration.setDefaultLanguageVersion(dummyThrows);

        Assertions.assertThrows(AssertionError.class, pmdRunnable::run);
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByRule() {
        Assertions.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));
        configuration.setDefaultLanguageVersion(dummyDefault);

        Assertions.assertThrows(AssertionError.class, pmdRunnable::run);
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
