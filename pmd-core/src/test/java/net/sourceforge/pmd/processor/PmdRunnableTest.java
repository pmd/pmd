/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.GlobalReportBuilderListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSets;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.processor.MonoThreadProcessor.MonothreadRunnable;
import net.sourceforge.pmd.util.datasource.DataSource;

public class PmdRunnableTest {

    @org.junit.Rule
    public TestRule restoreSystemProperties = new RestoreSystemProperties();

    private LanguageVersion dummyThrows;
    private LanguageVersion dummyDefault;
    private PMDConfiguration configuration;
    private PmdRunnable pmdRunnable;
    private GlobalReportBuilderListener reportBuilder;

    @Before
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
    public void inErrorRecoveryModeErrorsShouldBeLoggedByParser() {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");
        configuration.setDefaultLanguageVersion(dummyThrows);

        pmdRunnable.run();
        reportBuilder.close();
        Assert.assertEquals(1, reportBuilder.getResult().getProcessingErrors().size());
    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByRule() {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");
        configuration.setDefaultLanguageVersion(dummyDefault);

        pmdRunnable.run();
        reportBuilder.close();
        Report report = reportBuilder.getResult();
        Assert.assertEquals(1, report.getProcessingErrors().size());
        Assert.assertSame(AssertionError.class, report.getProcessingErrors().get(0).getError().getClass());
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByParser() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));
        configuration.setDefaultLanguageVersion(dummyThrows);

        Assert.assertThrows(AssertionError.class, pmdRunnable::run);
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByRule() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));
        configuration.setDefaultLanguageVersion(dummyDefault);

        Assert.assertThrows(AssertionError.class, pmdRunnable::run);
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
