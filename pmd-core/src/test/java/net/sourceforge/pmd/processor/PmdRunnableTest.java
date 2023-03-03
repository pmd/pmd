/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.processor;

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
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.util.ContextedAssertionError;
import net.sourceforge.pmd.util.log.MessageReporter;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class PmdRunnableTest {

    public static final String TEST_MESSAGE_SEMANTIC_ERROR = "An error occurred!";
    private static final String PARSER_REPORTS_SEMANTIC_ERROR = "1.9-semantic_error";
    private static final String THROWS_SEMANTIC_ERROR = "1.9-throws_semantic_error";
    private static final String THROWS_ASSERTION_ERROR = "1.9-throws";

    private PMDConfiguration configuration;
    private MessageReporter reporter;
    private Rule rule;


    @BeforeEach
    void prepare() {
        // reset data
        rule = spy(new RuleThatThrows());
        configuration = new PMDConfiguration(LanguageRegistry.singleton(ThrowingLanguageModule.INSTANCE));
        reporter = mock(MessageReporter.class);
        configuration.setReporter(reporter);
        // exceptions thrown on a worker thread are not thrown by the main thread,
        // so this test only makes sense with one thread
        configuration.setThreads(1);
    }


    private Report process(LanguageVersion lv) {
        configuration.setForceLanguageVersion(lv);
        configuration.setIgnoreIncrementalAnalysis(true);
        try (PmdAnalysis pmd = PmdAnalysis.create(configuration)) {
            pmd.files().addSourceFile("test.dummy", "foo");
            pmd.addRuleSet(RuleSet.forSingleRule(rule));
            return pmd.performAnalysisAndCollectReport();
        }
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

            Report report = process(ThrowingLanguageModule.INSTANCE.getDefaultVersion());

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
            assertThrows(AssertionError.class, () -> process(ThrowingLanguageModule.INSTANCE.getDefaultVersion()));
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

    private static LanguageVersion versionWithParserThatThrowsAssertionError() {
        return ThrowingLanguageModule.INSTANCE.getVersion(THROWS_ASSERTION_ERROR);
    }

    private static LanguageVersion getVersionWithParserThatThrowsSemanticError() {
        return ThrowingLanguageModule.INSTANCE.getVersion(THROWS_SEMANTIC_ERROR);
    }

    private static LanguageVersion versionWithParserThatReportsSemanticError() {
        return ThrowingLanguageModule.INSTANCE.getVersion(PARSER_REPORTS_SEMANTIC_ERROR);
    }

    private static class ThrowingLanguageModule extends SimpleLanguageModuleBase {

        static final ThrowingLanguageModule INSTANCE = new ThrowingLanguageModule();

        ThrowingLanguageModule() {
            super(LanguageMetadata.withId("foo").name("Foo").extensions("foo")
                                  .addVersion(THROWS_ASSERTION_ERROR)
                                  .addVersion(THROWS_SEMANTIC_ERROR)
                                  .addVersion(PARSER_REPORTS_SEMANTIC_ERROR)
                                  .addDefaultVersion("defalt"),
                  ThrowingLanguageModule::makeParser);
        }

        private static Parser makeParser() {
            return task -> {
                switch (task.getLanguageVersion().getVersion()) {
                case THROWS_ASSERTION_ERROR:
                    throw new AssertionError("test error while parsing");
                case PARSER_REPORTS_SEMANTIC_ERROR: {
                    RootNode root = DummyLanguageModule.readLispNode(task);
                    task.getReporter().error(root, TEST_MESSAGE_SEMANTIC_ERROR);
                    return root;
                }
                case THROWS_SEMANTIC_ERROR: {
                    RootNode root = DummyLanguageModule.readLispNode(task);
                    throw task.getReporter().error(root, TEST_MESSAGE_SEMANTIC_ERROR);
                }
                default:
                    return DummyLanguageModule.readLispNode(task);
                }
            };
        }
    }

    private static class RuleThatThrows extends AbstractRule {

        RuleThatThrows() {
            setLanguage(ThrowingLanguageModule.INSTANCE);
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
            throw new AssertionError("test");
        }
    }

}
