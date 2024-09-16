package net.sourceforge.pmd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;

import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TextFile;

import net.sourceforge.pmd.lang.rule.RuleSet;
import net.sourceforge.pmd.lang.rule.RuleSetLoader;

import net.sourceforge.pmd.renderers.CSVRenderer;
import net.sourceforge.pmd.renderers.Renderer;

import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.ReportStats;
import net.sourceforge.pmd.util.log.PmdReporter;
import net.sourceforge.pmd.util.log.internal.QuietReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PmdAnalysisDiffblueTest {
    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate() {
        // Arrange and Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(new PMDConfiguration());

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate2() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate3() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.addRelativizeRoot(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate4() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(2, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate5() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.setInputUri(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri());
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(2, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate6() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.setInputFilePath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(2, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate7() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreFilePath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate8() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.addRuleSet(".xml");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate9() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.addRuleSet("Cannot resolve rule/ruleset reference '");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate10() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), ""));
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(2, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate11() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.setInputFilePath(Paths.get(System.getProperty("java.io.tmpdir"), ""));
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(2, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#create(PMDConfiguration)}
     */
    @Test
    void testCreate12() {
        // Arrange
        PMDConfiguration config = new PMDConfiguration();
        config.setIgnoreFilePath(Paths.get(System.getProperty("java.io.tmpdir"), ""));
        config.addRuleSet("UU");

        // Act
        PmdAnalysis actualCreateResult = PmdAnalysis.create(config);

        // Assert
        PmdReporter reporter = actualCreateResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(actualCreateResult.getRulesets().isEmpty());
        assertTrue(actualCreateResult.renderers().isEmpty());
        assertTrue(actualCreateResult.rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#rulesets()}
     */
    @Test
    void testRulesets() {
        // Arrange, Act and Assert
        assertTrue(PmdAnalysis.create(new PMDConfiguration()).rulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#files()}
     */
    @Test
    void testFiles() {
        // Arrange and Act
        FileCollector actualFilesResult = PmdAnalysis.create(new PMDConfiguration()).files();

        // Assert
        PmdReporter reporter = actualFilesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
        assertTrue(actualFilesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#newRuleSetLoader()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewRuleSetLoader() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;

        // Act
        RuleSetLoader actualNewRuleSetLoaderResult = pmdAnalysis.newRuleSetLoader();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#addRenderer(Renderer)}
     */
    @Test
    void testAddRenderer() {
        // Arrange
        PmdAnalysis createResult = PmdAnalysis.create(new PMDConfiguration());
        CSVRenderer renderer = new CSVRenderer();

        // Act
        createResult.addRenderer(renderer);

        // Assert
        List<Renderer> renderersResult = createResult.renderers();
        assertEquals(1, renderersResult.size());
        assertSame(renderer, renderersResult.get(0));
    }

    /**
     * Method under test: {@link PmdAnalysis#addRenderers(Collection)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testAddRenderers() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        Collection<Renderer> renderers = null;

        // Act
        pmdAnalysis.addRenderers(renderers);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#addListener(GlobalAnalysisListener)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testAddListener() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        GlobalAnalysisListener listener = null;

        // Act
        pmdAnalysis.addListener(listener);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#addListeners(Collection)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testAddListeners() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        Collection<? extends GlobalAnalysisListener> listeners = null;

        // Act
        pmdAnalysis.addListeners(listeners);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#addRuleSet(RuleSet)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testAddRuleSet() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "java.util.Collection.size()" because "that" is null
        //       at net.sourceforge.pmd.lang.rule.RuleSet.<init>(RuleSet.java:85)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        RuleSet ruleSet = null;

        // Act
        pmdAnalysis.addRuleSet(ruleSet);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#addRuleSets(Collection)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testAddRuleSets() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        Collection<RuleSet> ruleSets = null;

        // Act
        pmdAnalysis.addRuleSets(ruleSets);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#getRulesets()}
     */
    @Test
    void testGetRulesets() {
        // Arrange, Act and Assert
        assertTrue(PmdAnalysis.create(new PMDConfiguration()).getRulesets().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#getLanguageProperties(Language)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testGetLanguageProperties() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.IllegalArgumentException: Language 'dummy_cpd_only' is not registered in LanguageRegistry(dummy, dummy2)
        //       at net.sourceforge.pmd.AbstractConfiguration.checkLanguageIsRegistered(AbstractConfiguration.java:92)
        //       at net.sourceforge.pmd.PmdAnalysis.getLanguageProperties(PmdAnalysis.java:339)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        Language language = null;

        // Act
        LanguagePropertyBundle actualLanguageProperties = pmdAnalysis.getLanguageProperties(language);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#performAnalysis()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testPerformAnalysis() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;

        // Act
        pmdAnalysis.performAnalysis();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#performAnalysisAndCollectReport()}
     */
    @Test
    void testPerformAnalysisAndCollectReport() {
        // Arrange and Act
        Report actualPerformAnalysisAndCollectReportResult = PmdAnalysis.create(new PMDConfiguration())
                .performAnalysisAndCollectReport();

        // Assert
        assertTrue(actualPerformAnalysisAndCollectReportResult.getConfigurationErrors().isEmpty());
        assertTrue(actualPerformAnalysisAndCollectReportResult.getProcessingErrors().isEmpty());
        assertTrue(actualPerformAnalysisAndCollectReportResult.getSuppressedViolations().isEmpty());
        assertTrue(actualPerformAnalysisAndCollectReportResult.getViolations().isEmpty());
    }

    /**
     * Method under test: {@link PmdAnalysis#performAnalysisImpl(List)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testPerformAnalysisImpl() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        List<? extends Report.GlobalReportBuilderListener> extraListeners = null;

        // Act
        pmdAnalysis.performAnalysisImpl(extraListeners);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#performAnalysisImpl(List, List)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testPerformAnalysisImpl2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        List<? extends Report.GlobalReportBuilderListener> extraListeners = null;
        List<TextFile> textFiles = null;

        // Act
        pmdAnalysis.performAnalysisImpl(extraListeners, textFiles);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#runAndReturnStats()}
     */
    @Test
    void testRunAndReturnStats() {
        // Arrange and Act
        ReportStats actualRunAndReturnStatsResult = PmdAnalysis.create(new PMDConfiguration()).runAndReturnStats();

        // Assert
        assertEquals(0, actualRunAndReturnStatsResult.getNumErrors());
        assertEquals(0, actualRunAndReturnStatsResult.getNumViolations());
    }

    /**
     * Method under test: {@link PmdAnalysis#printErrorDetected(int)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testPrintErrorDetected() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange
        // TODO: Populate arranged inputs
        PmdAnalysis pmdAnalysis = null;
        int errors = 0;

        // Act
        pmdAnalysis.printErrorDetected(errors);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link PmdAnalysis#printErrorDetected(PmdReporter, int)}
     */
    @Test
    void testPrintErrorDetected2() {
        // Arrange
        QuietReporter reporter = mock(QuietReporter.class);
        doNothing().when(reporter).info(Mockito.<String>any(), (Object[]) any());

        // Act
        PmdAnalysis.printErrorDetected(reporter, -1);

        // Assert
        verify(reporter).info(eq(
                        "-1 errors occurred while executing PMD.\nRun in verbose mode to see a stack-trace.\nIf you think this is a bug in PMD, please report this issue at https://github.com/pmd/pmd/issues/new/choose\nIf you do so, please include a stack-trace, the code sample\n causing the issue, and details about your run configuration."),
                (Object[]) any());
    }

    /**
     * Method under test: {@link PmdAnalysis#close()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testClose() {
        try (PmdAnalysis pmdAnalysis = null) {
        }
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link PmdAnalysis#fileNameRenderer()}
     *   <li>{@link PmdAnalysis#getReporter()}
     *   <li>{@link PmdAnalysis#renderers()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        PmdAnalysis createResult = PmdAnalysis.create(new PMDConfiguration());

        // Act
        createResult.fileNameRenderer();
        PmdReporter actualReporter = createResult.getReporter();

        // Assert
        assertTrue(actualReporter instanceof SimpleMessageReporter);
        assertTrue(createResult.renderers().isEmpty());
    }
}
