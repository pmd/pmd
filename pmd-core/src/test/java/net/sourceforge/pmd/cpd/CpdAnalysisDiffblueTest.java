package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;

import net.sourceforge.pmd.lang.CpdOnlyDummyLanguage;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionDiscoverer;

import net.sourceforge.pmd.lang.ast.FileAnalysisException;
import net.sourceforge.pmd.lang.ast.InternalApiBridge;

import net.sourceforge.pmd.lang.document.FileCollector;
import net.sourceforge.pmd.lang.document.TestMessageReporter;
import net.sourceforge.pmd.util.log.PmdReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CpdAnalysisDiffblueTest {
    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate() {
        // Arrange, Act and Assert
        FileCollector filesResult = CpdAnalysis.create(new CPDConfiguration()).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate2() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate3() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "foo"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate4() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), ""));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate5() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setSkipDuplicates(true);
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate6() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(mock(CPDReportRenderer.class));
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate7() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setNoSkipBlocks(true);
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate8() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setInputUri(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri());
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(2, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate9() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setInputFilePath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(2, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate10() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setIgnoreFilePath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate11() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setInputFilePath(Paths.get(System.getProperty("java.io.tmpdir"), "foo"));
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(2, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#create(CPDConfiguration)}
     */
    @Test
    void testCreate12() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setIgnoreFilePath(Paths.get(System.getProperty("java.io.tmpdir"), "foo"));
        config.addInputPath(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));

        // Act and Assert
        FileCollector filesResult = CpdAnalysis.create(config).files();
        PmdReporter reporter = filesResult.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
        assertTrue(filesResult.getCollectedFiles().isEmpty());
    }

    /**
     * Method under test: {@link CpdAnalysis#setCpdListener(CPDListener)}
     */
    @Test
    void testSetCpdListener() {
        // Arrange
        CPDConfiguration config = mock(CPDConfiguration.class);
        when(config.isIgnoreAnnotations()).thenReturn(true);
        when(config.isIgnoreIdentifierAndLiteralSequences()).thenReturn(true);
        when(config.isIgnoreIdentifiers()).thenReturn(true);
        when(config.isIgnoreLiteralSequences()).thenReturn(true);
        when(config.isIgnoreUsings()).thenReturn(true);
        when(config.isNoSkipBlocks()).thenReturn(true);
        when(config.isIgnoreLiterals()).thenReturn(true);
        when(config.getLanguageProperties(Mockito.<Language>any()))
                .thenReturn(new LanguagePropertyBundle(new CpdOnlyDummyLanguage()));
        when(config.isSkipDuplicates()).thenReturn(true);
        when(config.getUri()).thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt").toUri());
        when(config.getSourceEncoding()).thenReturn(null);
        when(config.getIgnoreFile()).thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
        when(config.getInputFile()).thenReturn(Paths.get(System.getProperty("java.io.tmpdir"), "test.txt"));
        when(config.getExcludes()).thenReturn(new ArrayList<>());
        when(config.getInputPathList()).thenReturn(new ArrayList<>());
        when(config.getCPDReportRenderer()).thenReturn(mock(CPDReportRenderer.class));
        when(config.getLanguageRegistry()).thenReturn(LanguageRegistry.CPD);
        when(config.getLanguageVersionDiscoverer()).thenReturn(new LanguageVersionDiscoverer(LanguageRegistry.CPD));
        when(config.getReporter()).thenReturn(new TestMessageReporter());
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.setCpdListener(new CPDNullListener());

        // Assert
        verify(config).getExcludes();
        verify(config, atLeast(1)).getIgnoreFile();
        verify(config, atLeast(1)).getInputFile();
        verify(config).getInputPathList();
        verify(config, atLeast(1)).getLanguageProperties(Mockito.<Language>any());
        verify(config).getLanguageRegistry();
        verify(config).getLanguageVersionDiscoverer();
        verify(config).getReporter();
        verify(config).getSourceEncoding();
        verify(config, atLeast(1)).getUri();
        verify(config).getCPDReportRenderer();
        verify(config, atLeast(1)).isIgnoreAnnotations();
        verify(config, atLeast(1)).isIgnoreIdentifierAndLiteralSequences();
        verify(config, atLeast(1)).isIgnoreIdentifiers();
        verify(config, atLeast(1)).isIgnoreLiteralSequences();
        verify(config, atLeast(1)).isIgnoreLiterals();
        verify(config, atLeast(1)).isIgnoreUsings();
        verify(config, atLeast(1)).isNoSkipBlocks();
        verify(config).isSkipDuplicates();
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis()}
     */
    @Test
    void testPerformAnalysis() {
        // Arrange
        CpdAnalysis createResult = CpdAnalysis.create(new CPDConfiguration());

        // Act
        createResult.performAnalysis();

        // Assert
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis()}
     */
    @Test
    void testPerformAnalysis2() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setMinimumTileSize(1);
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.performAnalysis();

        // Assert
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis()}
     */
    @Test
    void testPerformAnalysis3() throws IOException {
        // Arrange
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        doNothing().when(renderer).render(Mockito.<CPDReport>any(), Mockito.<Writer>any());

        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(renderer);
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.performAnalysis();

        // Assert
        verify(renderer).render(isA(CPDReport.class), isA(Writer.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis()}
     */
    @Test
    void testPerformAnalysis4() throws IOException {
        // Arrange
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        doThrow(new IOException("Running match algorithm on {} files...")).when(renderer)
                .render(Mockito.<CPDReport>any(), Mockito.<Writer>any());

        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(renderer);
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.performAnalysis();

        // Assert
        verify(renderer).render(isA(CPDReport.class), isA(Writer.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis()}
     */
    @Test
    void testPerformAnalysis5() throws IOException {
        // Arrange
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        doThrow(new FileAnalysisException()).when(renderer).render(Mockito.<CPDReport>any(), Mockito.<Writer>any());

        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(renderer);
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.performAnalysis();

        // Assert
        verify(renderer).render(isA(CPDReport.class), isA(Writer.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis()}
     */
    @Test
    void testPerformAnalysis6() throws IOException {
        // Arrange
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        doThrow(InternalApiBridge.newLexException(true, "Running match algorithm on {} files...", -1, -1,
                "An error occurred", '\u0001')).when(renderer).render(Mockito.<CPDReport>any(), Mockito.<Writer>any());

        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(renderer);
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.performAnalysis();

        // Assert
        verify(renderer).render(isA(CPDReport.class), isA(Writer.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis(Consumer)}
     */
    @Test
    void testPerformAnalysis7() {
        // Arrange
        CpdAnalysis createResult = CpdAnalysis.create(new CPDConfiguration());
        Consumer<CPDReport> consumer = mock(Consumer.class);
        doNothing().when(consumer).accept(Mockito.<CPDReport>any());

        // Act
        createResult.performAnalysis(consumer);

        // Assert
        verify(consumer).accept(isA(CPDReport.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis(Consumer)}
     */
    @Test
    void testPerformAnalysis8() {
        // Arrange
        CPDConfiguration config = new CPDConfiguration();
        config.setMinimumTileSize(1);
        CpdAnalysis createResult = CpdAnalysis.create(config);
        Consumer<CPDReport> consumer = mock(Consumer.class);
        doNothing().when(consumer).accept(Mockito.<CPDReport>any());

        // Act
        createResult.performAnalysis(consumer);

        // Assert
        verify(consumer).accept(isA(CPDReport.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis(Consumer)}
     */
    @Test
    void testPerformAnalysis9() throws IOException {
        // Arrange
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        doNothing().when(renderer).render(Mockito.<CPDReport>any(), Mockito.<Writer>any());

        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(renderer);
        CpdAnalysis createResult = CpdAnalysis.create(config);
        Consumer<CPDReport> consumer = mock(Consumer.class);
        doNothing().when(consumer).accept(Mockito.<CPDReport>any());

        // Act
        createResult.performAnalysis(consumer);

        // Assert
        verify(consumer).accept(isA(CPDReport.class));
        verify(renderer).render(isA(CPDReport.class), isA(Writer.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(0, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis(Consumer)}
     */
    @Test
    void testPerformAnalysis10() throws IOException {
        // Arrange
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        doThrow(new IOException("Running match algorithm on {} files...")).when(renderer)
                .render(Mockito.<CPDReport>any(), Mockito.<Writer>any());

        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(renderer);
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.performAnalysis(mock(Consumer.class));

        // Assert
        verify(renderer).render(isA(CPDReport.class), isA(Writer.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis(Consumer)}
     */
    @Test
    void testPerformAnalysis11() throws IOException {
        // Arrange
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        doThrow(new FileAnalysisException()).when(renderer).render(Mockito.<CPDReport>any(), Mockito.<Writer>any());

        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(renderer);
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.performAnalysis(mock(Consumer.class));

        // Assert
        verify(renderer).render(isA(CPDReport.class), isA(Writer.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
    }

    /**
     * Method under test: {@link CpdAnalysis#performAnalysis(Consumer)}
     */
    @Test
    void testPerformAnalysis12() throws IOException {
        // Arrange
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        doThrow(InternalApiBridge.newLexException(true, "Running match algorithm on {} files...", -1, -1,
                "An error occurred", '\u0001')).when(renderer).render(Mockito.<CPDReport>any(), Mockito.<Writer>any());

        CPDConfiguration config = new CPDConfiguration();
        config.setRenderer(renderer);
        CpdAnalysis createResult = CpdAnalysis.create(config);

        // Act
        createResult.performAnalysis(mock(Consumer.class));

        // Assert
        verify(renderer).render(isA(CPDReport.class), isA(Writer.class));
        PmdReporter reporter = createResult.files().getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals(1, reporter.numErrors());
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link CpdAnalysis#close()}
     *   <li>{@link CpdAnalysis#files()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() throws IOException {
        // Arrange
        CpdAnalysis createResult = CpdAnalysis.create(new CPDConfiguration());

        // Act
        createResult.close();
        FileCollector actualFilesResult = createResult.files();

        // Assert that nothing has changed
        assertTrue(actualFilesResult.getReporter() instanceof SimpleMessageReporter);
        assertTrue(actualFilesResult.getCollectedFiles().isEmpty());
    }
}
