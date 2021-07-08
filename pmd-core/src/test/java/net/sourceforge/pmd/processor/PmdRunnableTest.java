/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;

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
import net.sourceforge.pmd.util.document.TextFile;

public class PmdRunnableTest {

    @org.junit.Rule
    public TestRule restoreSystemProperties = new RestoreSystemProperties();

    private static final LanguageVersion DUMMY_THROWS;
    private static final LanguageVersion DUMMY_DEFAULT;


    static {
        Language dummyLanguage = LanguageRegistry.findLanguageByTerseName(DummyLanguageModule.TERSE_NAME);
        DUMMY_DEFAULT = dummyLanguage.getDefaultVersion();
        DUMMY_THROWS = dummyLanguage.getVersion("1.9-throws");
    }


    private Report process(LanguageVersion lv) {
        TextFile dataSource = TextFile.forCharSeq("test", "test.dummy", lv);

        Rule rule = new RuleThatThrows();
        PMDConfiguration configuration = new PMDConfiguration();
        GlobalReportBuilderListener reportBuilder = new GlobalReportBuilderListener();
        PmdRunnable pmdRunnable = new MonothreadRunnable(new RuleSets(RuleSet.forSingleRule(rule)),
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

        Report report = process(DUMMY_THROWS);

        Assert.assertEquals(1, report.getProcessingErrors().size());
    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByRule() {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");

        Report report = process(DUMMY_DEFAULT);

        List<ProcessingError> errors = report.getProcessingErrors();
        assertThat(errors, hasSize(1));
        assertThat(errors.get(0).getError(), instanceOf(ContextedAssertionError.class));
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByParser() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));

        Assert.assertThrows(AssertionError.class, () -> process(DUMMY_THROWS));
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByRule() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));


        Assert.assertThrows(AssertionError.class, () -> process(DUMMY_DEFAULT));
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
