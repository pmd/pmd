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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.function.BiConsumer;

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
import net.sourceforge.pmd.lang.DummyLanguageModule.Handler;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.processor.MonoThreadProcessor.MonothreadRunnable;
import net.sourceforge.pmd.util.datasource.DataSource;
import net.sourceforge.pmd.util.log.MessageReporter;

public class PmdRunnableTest {

    public static final String TEST_MESSAGE_SEMANTIC_ERROR = "An error occurred!";
    private static final String PARSER_REPORTS_SEMANTIC_ERROR = "1.9-semantic_error";
    private static final String THROWS_SEMANTIC_ERROR = "1.9-throws_semantic_error";
    private static final String THROWS_ASSERTION_ERROR = "1.9-throws";


    @org.junit.Rule
    public TestRule restoreSystemProperties = new RestoreSystemProperties();

    private PMDConfiguration configuration;
    private PmdRunnable pmdRunnable;
    private GlobalReportBuilderListener reportBuilder;
    private MessageReporter reporter;
    private Rule rule;

    @Before
    public void prepare() {
        DataSource dataSource = DataSource.forString("test", "test.dummy");


        rule = spy(new RuleThatThrows());
        configuration = new PMDConfiguration();
        reportBuilder = new GlobalReportBuilderListener();
        reporter = mock(MessageReporter.class);
        configuration.setReporter(reporter);
        pmdRunnable = new MonothreadRunnable(new RuleSets(RuleSet.forSingleRule(rule)),
                                             dataSource,
                                             reportBuilder,
                                             configuration);

    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByParser() {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");
        configuration.setDefaultLanguageVersion(versionWithParserThatThrowsAssertionError());

        pmdRunnable.run();
        reportBuilder.close();
        Assert.assertEquals(1, reportBuilder.getResult().getProcessingErrors().size());
    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByRule() {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");
        configuration.setDefaultLanguageVersion(DummyLanguageModule.getInstance().getDefaultVersion());

        pmdRunnable.run();
        reportBuilder.close();
        Report report = reportBuilder.getResult();
        List<ProcessingError> errors = report.getProcessingErrors();
        assertThat(errors, hasSize(1));
        assertThat(errors.get(0).getError(), instanceOf(ContextedAssertionError.class));
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByParser() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));
        configuration.setDefaultLanguageVersion(versionWithParserThatThrowsAssertionError());

        Assert.assertThrows(AssertionError.class, pmdRunnable::run);
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByRule() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));
        configuration.setDefaultLanguageVersion(DummyLanguageModule.getInstance().getDefaultVersion());

        Assert.assertThrows(AssertionError.class, pmdRunnable::run);
    }


    @Test
    public void semanticErrorShouldAbortTheRun() {
        configuration.setDefaultLanguageVersion(versionWithParserThatReportsSemanticError());

        pmdRunnable.run();

        verify(reporter, times(1)).log(eq(Level.ERROR), eq("at test.dummy :1:1: " + TEST_MESSAGE_SEMANTIC_ERROR));
        verify(rule, never()).apply(Mockito.any(), Mockito.any());

        reportBuilder.close();
        Assert.assertEquals(1, reportBuilder.getResult().getProcessingErrors().size());
    }

    @Test
    public void semanticErrorThrownShouldAbortTheRun() {
        configuration.setDefaultLanguageVersion(getVersionWithParserThatThrowsSemanticError());

        pmdRunnable.run();

        verify(reporter, times(1)).log(eq(Level.ERROR), contains(TEST_MESSAGE_SEMANTIC_ERROR));
        verify(rule, never()).apply(Mockito.any(), Mockito.any());

        reportBuilder.close();
        Assert.assertEquals(1, reportBuilder.getResult().getProcessingErrors().size());
    }

    public static void registerCustomVersions(BiConsumer<String, Handler> addVersion) {
        addVersion.accept(THROWS_ASSERTION_ERROR, new HandlerWithParserThatThrows());
        addVersion.accept(PARSER_REPORTS_SEMANTIC_ERROR, new HandlerWithParserThatReportsSemanticError());
        addVersion.accept(THROWS_SEMANTIC_ERROR, new HandlerWithParserThatThrowsSemanticError());
    }

    public static LanguageVersion versionWithParserThatThrowsAssertionError() {
        return DummyLanguageModule.getInstance().getVersion(THROWS_ASSERTION_ERROR);
    }

    public static LanguageVersion getVersionWithParserThatThrowsSemanticError() {
        return DummyLanguageModule.getInstance().getVersion(THROWS_SEMANTIC_ERROR);
    }

    public static LanguageVersion versionWithParserThatReportsSemanticError() {
        return DummyLanguageModule.getInstance().getVersion(PARSER_REPORTS_SEMANTIC_ERROR);
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

    public static class HandlerWithParserThatThrowsSemanticError extends Handler {

        @Override
        public Parser getParser() {
            return task -> {
                RootNode root = super.getParser().parse(task);
                throw task.getReporter().error(root, TEST_MESSAGE_SEMANTIC_ERROR);
            };
        }
    }

    public static class HandlerWithParserThatThrows extends Handler {

        @Override
        public Parser getParser() {
            return task -> {
                throw new AssertionError("test error while parsing");
            };
        }
    }

    public static class HandlerWithParserThatReportsSemanticError extends Handler {

        @Override
        public Parser getParser() {
            return task -> {
                RootNode root = super.getParser().parse(task);
                task.getReporter().error(root, TEST_MESSAGE_SEMANTIC_ERROR);
                return root;
            };
        }
    }
}
