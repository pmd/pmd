package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.util.log.PmdReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CPDConfigurationDiffblueTest {
    /**
     * Method under test: {@link CPDConfiguration#setSourceEncoding(Charset)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testSetSourceEncoding() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException
        //       at java.base/java.util.Objects.requireNonNull(Objects.java:220)
        //       at net.sourceforge.pmd.AbstractConfiguration.setSourceEncoding(AbstractConfiguration.java:75)
        //       at net.sourceforge.pmd.cpd.CPDConfiguration.setSourceEncoding(CPDConfiguration.java:90)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        // TODO: Populate arranged inputs
        CPDConfiguration cpdConfiguration = null;
        Charset sourceEncoding = null;

        // Act
        cpdConfiguration.setSourceEncoding(sourceEncoding);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test:
     * {@link CPDConfiguration#createRendererByName(String, Charset)}
     */
    @Test
    void testCreateRendererByName() throws IOException {
        // Arrange and Act
        CPDReportRenderer actualCreateRendererByNameResult = CPDConfiguration.createRendererByName(null, null);
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport makeReportResult = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter stringWriter = new StringWriter();
        actualCreateRendererByNameResult.render(makeReportResult, stringWriter);

        // Assert that nothing has changed
        assertTrue(actualCreateRendererByNameResult instanceof SimpleRenderer);
        assertEquals("", stringWriter.toString());
    }

    /**
     * Method under test:
     * {@link CPDConfiguration#createRendererByName(String, Charset)}
     */
    @Test
    void testCreateRendererByName2() throws IOException {
        // Arrange and Act
        CPDReportRenderer actualCreateRendererByNameResult = CPDConfiguration.createRendererByName("", null);
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport makeReportResult = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter stringWriter = new StringWriter();
        actualCreateRendererByNameResult.render(makeReportResult, stringWriter);

        // Assert that nothing has changed
        assertTrue(actualCreateRendererByNameResult instanceof SimpleRenderer);
        assertEquals("", stringWriter.toString());
    }

    /**
     * Method under test:
     * {@link CPDConfiguration#createRendererByName(String, Charset)}
     */
    @Test
    void testCreateRendererByName3() throws IOException {
        // Arrange and Act
        CPDReportRenderer actualCreateRendererByNameResult = CPDConfiguration.createRendererByName(null, null);
        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport makeReportResult = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter stringWriter = new StringWriter();
        actualCreateRendererByNameResult.render(makeReportResult, stringWriter);

        // Assert
        assertTrue(actualCreateRendererByNameResult instanceof SimpleRenderer);
        assertEquals("Found a 1 line (3 tokens) duplication in the following files: \n"
                + "Starting at line 2 of /var/Bar.java\n" + "\n" + "1_1_1_1_1_1_1_1_1_1_\n" + "\n", stringWriter.toString());
    }

    /**
     * Method under test: {@link CPDConfiguration#getRenderers()}
     */
    @Test
    void testGetRenderers() {
        // Arrange and Act
        Set<String> actualRenderers = CPDConfiguration.getRenderers();

        // Assert
        assertEquals(6, actualRenderers.size());
        assertTrue(actualRenderers.contains("csv"));
        assertTrue(actualRenderers.contains("csv_with_linecount_per_file"));
        assertTrue(actualRenderers.contains("vs"));
        assertTrue(actualRenderers.contains("xml"));
        assertTrue(actualRenderers.contains("xmlold"));
        assertTrue(actualRenderers.contains(CPDConfiguration.DEFAULT_RENDERER));
    }

    /**
     * Method under test: {@link CPDConfiguration#setRendererName(String)}
     */
    @Test
    void testSetRendererName() {
        // Arrange, Act and Assert
        assertThrows(IllegalArgumentException.class, () -> (new CPDConfiguration()).setRendererName("Renderer Name"));
        assertThrows(IllegalArgumentException.class, () -> (new CPDConfiguration()).setRendererName("java.lang.String"));
    }

    /**
     * Method under test: {@link CPDConfiguration#setRendererName(String)}
     */
    @Test
    void testSetRendererName2() {
        // Arrange
        CPDConfiguration cpdConfiguration = new CPDConfiguration();

        // Act
        cpdConfiguration.setRendererName(null);

        // Assert
        assertTrue(cpdConfiguration.getCPDReportRenderer() instanceof SimpleRenderer);
        assertNull(cpdConfiguration.getRendererName());
    }

    /**
     * Method under test: {@link CPDConfiguration#setRendererName(String)}
     */
    @Test
    void testSetRendererName3() {
        // Arrange
        CPDConfiguration cpdConfiguration = new CPDConfiguration();

        // Act
        cpdConfiguration.setRendererName("");

        // Assert
        assertTrue(cpdConfiguration.getCPDReportRenderer() instanceof SimpleRenderer);
        assertEquals("", cpdConfiguration.getRendererName());
    }

    /**
     * Method under test: {@link CPDConfiguration#setRendererName(String)}
     */
    @Test
    void testSetRendererName4() {
        // Arrange
        CPDConfiguration cpdConfiguration = new CPDConfiguration();
        cpdConfiguration.setRenderer(mock(CPDReportRenderer.class));

        // Act
        cpdConfiguration.setRendererName("");

        // Assert
        assertTrue(cpdConfiguration.getCPDReportRenderer() instanceof SimpleRenderer);
        assertEquals("", cpdConfiguration.getRendererName());
    }

    /**
     * Method under test: {@link CPDConfiguration#setRendererName(String)}
     */
    @Test
    void testSetRendererName5() {
        // Arrange
        CPDConfiguration cpdConfiguration = new CPDConfiguration();
        cpdConfiguration.setRenderer(mock(CPDReportRenderer.class));

        // Act
        cpdConfiguration.setRendererName("net.sourceforge.pmd.cpd.CPDReportRenderer");

        // Assert
        assertTrue(cpdConfiguration.getCPDReportRenderer() instanceof SimpleRenderer);
        assertEquals("net.sourceforge.pmd.cpd.CPDReportRenderer", cpdConfiguration.getRendererName());
    }

    /**
     * Method under test: {@link CPDConfiguration#setRendererName(String)}
     */
    @Test
    void testSetRendererName6() {
        // Arrange
        CPDConfiguration cpdConfiguration = new CPDConfiguration();
        cpdConfiguration.setRenderer(mock(CPDReportRenderer.class));

        // Act and Assert
        assertThrows(IllegalArgumentException.class,
                () -> cpdConfiguration.setRendererName("net.sourceforge.pmd.cpd.CpdAnalysis"));
    }

    /**
     * Method under test:
     * {@link CPDConfiguration#checkLanguageIsAcceptable(Language)}
     */
    @Test
    void testCheckLanguageIsAcceptable() throws UnsupportedOperationException {
        // Arrange
        CPDConfiguration cpdConfiguration = new CPDConfiguration();
        Language lang = mock(Language.class);
        when(lang.getId()).thenReturn("42");

        // Act and Assert
        assertThrows(UnsupportedOperationException.class, () -> cpdConfiguration.checkLanguageIsAcceptable(lang));
        verify(lang).getId();
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link CPDConfiguration#setHelp(boolean)}
     *   <li>{@link CPDConfiguration#setIgnoreAnnotations(boolean)}
     *   <li>{@link CPDConfiguration#setIgnoreIdentifierAndLiteralSequences(boolean)}
     *   <li>{@link CPDConfiguration#setIgnoreIdentifiers(boolean)}
     *   <li>{@link CPDConfiguration#setIgnoreLiteralSequences(boolean)}
     *   <li>{@link CPDConfiguration#setIgnoreLiterals(boolean)}
     *   <li>{@link CPDConfiguration#setIgnoreUsings(boolean)}
     *   <li>{@link CPDConfiguration#setMinimumTileSize(int)}
     *   <li>{@link CPDConfiguration#setNoSkipBlocks(boolean)}
     *   <li>{@link CPDConfiguration#setRenderer(CPDReportRenderer)}
     *   <li>{@link CPDConfiguration#setSkipBlocksPattern(String)}
     *   <li>{@link CPDConfiguration#setSkipDuplicates(boolean)}
     *   <li>{@link CPDConfiguration#setSkipLexicalErrors(boolean)}
     *   <li>{@link CPDConfiguration#getCPDReportRenderer()}
     *   <li>{@link CPDConfiguration#getMinimumTileSize()}
     *   <li>{@link CPDConfiguration#getRendererName()}
     *   <li>{@link CPDConfiguration#getSkipBlocksPattern()}
     *   <li>{@link CPDConfiguration#isHelp()}
     *   <li>{@link CPDConfiguration#isIgnoreAnnotations()}
     *   <li>{@link CPDConfiguration#isIgnoreIdentifierAndLiteralSequences()}
     *   <li>{@link CPDConfiguration#isIgnoreIdentifiers()}
     *   <li>{@link CPDConfiguration#isIgnoreLiteralSequences()}
     *   <li>{@link CPDConfiguration#isIgnoreLiterals()}
     *   <li>{@link CPDConfiguration#isIgnoreUsings()}
     *   <li>{@link CPDConfiguration#isNoSkipBlocks()}
     *   <li>{@link CPDConfiguration#isSkipDuplicates()}
     *   <li>{@link CPDConfiguration#isSkipLexicalErrors()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        CPDConfiguration cpdConfiguration = new CPDConfiguration();

        // Act
        cpdConfiguration.setHelp(true);
        cpdConfiguration.setIgnoreAnnotations(true);
        cpdConfiguration.setIgnoreIdentifierAndLiteralSequences(true);
        cpdConfiguration.setIgnoreIdentifiers(true);
        cpdConfiguration.setIgnoreLiteralSequences(true);
        cpdConfiguration.setIgnoreLiterals(true);
        cpdConfiguration.setIgnoreUsings(true);
        cpdConfiguration.setMinimumTileSize(1);
        cpdConfiguration.setNoSkipBlocks(true);
        CPDReportRenderer renderer = mock(CPDReportRenderer.class);
        cpdConfiguration.setRenderer(renderer);
        cpdConfiguration.setSkipBlocksPattern("Skip Blocks Pattern");
        cpdConfiguration.setSkipDuplicates(true);
        cpdConfiguration.setSkipLexicalErrors(true);
        CPDReportRenderer actualCPDReportRenderer = cpdConfiguration.getCPDReportRenderer();
        int actualMinimumTileSize = cpdConfiguration.getMinimumTileSize();
        String actualRendererName = cpdConfiguration.getRendererName();
        String actualSkipBlocksPattern = cpdConfiguration.getSkipBlocksPattern();
        boolean actualIsHelpResult = cpdConfiguration.isHelp();
        boolean actualIsIgnoreAnnotationsResult = cpdConfiguration.isIgnoreAnnotations();
        boolean actualIsIgnoreIdentifierAndLiteralSequencesResult = cpdConfiguration
                .isIgnoreIdentifierAndLiteralSequences();
        boolean actualIsIgnoreIdentifiersResult = cpdConfiguration.isIgnoreIdentifiers();
        boolean actualIsIgnoreLiteralSequencesResult = cpdConfiguration.isIgnoreLiteralSequences();
        boolean actualIsIgnoreLiteralsResult = cpdConfiguration.isIgnoreLiterals();
        boolean actualIsIgnoreUsingsResult = cpdConfiguration.isIgnoreUsings();
        boolean actualIsNoSkipBlocksResult = cpdConfiguration.isNoSkipBlocks();
        boolean actualIsSkipDuplicatesResult = cpdConfiguration.isSkipDuplicates();

        // Assert that nothing has changed
        assertEquals("Skip Blocks Pattern", actualSkipBlocksPattern);
        assertEquals(1, actualMinimumTileSize);
        assertTrue(actualIsHelpResult);
        assertTrue(actualIsIgnoreAnnotationsResult);
        assertTrue(actualIsIgnoreIdentifierAndLiteralSequencesResult);
        assertTrue(actualIsIgnoreIdentifiersResult);
        assertTrue(actualIsIgnoreLiteralSequencesResult);
        assertTrue(actualIsIgnoreLiteralsResult);
        assertTrue(actualIsIgnoreUsingsResult);
        assertTrue(actualIsNoSkipBlocksResult);
        assertTrue(actualIsSkipDuplicatesResult);
        assertTrue(cpdConfiguration.isSkipLexicalErrors());
        assertEquals(CPDConfiguration.DEFAULT_RENDERER, actualRendererName);
        assertSame(renderer, actualCPDReportRenderer);
    }

    /**
     * Method under test: {@link CPDConfiguration#CPDConfiguration()}
     */
    @Test
    void testNewCPDConfiguration() {
        // Arrange and Act
        CPDConfiguration actualCpdConfiguration = new CPDConfiguration();

        // Assert
        PmdReporter reporter = actualCpdConfiguration.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals("#if 0|#endif", actualCpdConfiguration.getSkipBlocksPattern());
        assertEquals("UTF-8", actualCpdConfiguration.getSourceEncoding().name());
        assertNull(actualCpdConfiguration.getUri());
        assertNull(actualCpdConfiguration.getIgnoreFile());
        assertNull(actualCpdConfiguration.getInputFile());
        assertNull(actualCpdConfiguration.getCPDReportRenderer());
        assertNull(actualCpdConfiguration.getForceLanguageVersion());
        assertNull(actualCpdConfiguration.getLanguageVersionDiscoverer().getForcedVersion());
        assertEquals(0, actualCpdConfiguration.getMinimumTileSize());
        assertEquals(0, reporter.numErrors());
        assertEquals(3, actualCpdConfiguration.getLanguageRegistry().getLanguages().size());
        assertFalse(actualCpdConfiguration.isForceLanguageVersion());
        assertFalse(actualCpdConfiguration.isHelp());
        assertFalse(actualCpdConfiguration.isIgnoreAnnotations());
        assertFalse(actualCpdConfiguration.isIgnoreIdentifierAndLiteralSequences());
        assertFalse(actualCpdConfiguration.isIgnoreIdentifiers());
        assertFalse(actualCpdConfiguration.isIgnoreLiteralSequences());
        assertFalse(actualCpdConfiguration.isIgnoreLiterals());
        assertFalse(actualCpdConfiguration.isIgnoreUsings());
        assertFalse(actualCpdConfiguration.isNoSkipBlocks());
        assertFalse(actualCpdConfiguration.isSkipDuplicates());
        assertTrue(actualCpdConfiguration.getExcludes().isEmpty());
        assertTrue(actualCpdConfiguration.getInputPathList().isEmpty());
        assertTrue(actualCpdConfiguration.getRelativizeRoots().isEmpty());
        assertTrue(actualCpdConfiguration.isFailOnError());
        assertTrue(actualCpdConfiguration.isFailOnViolation());
        assertTrue(actualCpdConfiguration.isSkipLexicalErrors());
        assertEquals(CPDConfiguration.DEFAULT_RENDERER, actualCpdConfiguration.getRendererName());
    }

    /**
     * Method under test:
     * {@link CPDConfiguration#CPDConfiguration(LanguageRegistry)}
     */
    @Test
    void testNewCPDConfiguration2() {
        // Arrange
        LanguageRegistry languageRegistry = LanguageRegistry.CPD;

        // Act
        CPDConfiguration actualCpdConfiguration = new CPDConfiguration(languageRegistry);

        // Assert
        PmdReporter reporter = actualCpdConfiguration.getReporter();
        assertTrue(reporter instanceof SimpleMessageReporter);
        assertEquals("#if 0|#endif", actualCpdConfiguration.getSkipBlocksPattern());
        assertEquals("UTF-8", actualCpdConfiguration.getSourceEncoding().name());
        assertNull(actualCpdConfiguration.getUri());
        assertNull(actualCpdConfiguration.getIgnoreFile());
        assertNull(actualCpdConfiguration.getInputFile());
        assertNull(actualCpdConfiguration.getCPDReportRenderer());
        assertNull(actualCpdConfiguration.getForceLanguageVersion());
        assertNull(actualCpdConfiguration.getLanguageVersionDiscoverer().getForcedVersion());
        assertEquals(0, actualCpdConfiguration.getMinimumTileSize());
        assertEquals(0, reporter.numErrors());
        assertFalse(actualCpdConfiguration.isForceLanguageVersion());
        assertFalse(actualCpdConfiguration.isHelp());
        assertFalse(actualCpdConfiguration.isIgnoreAnnotations());
        assertFalse(actualCpdConfiguration.isIgnoreIdentifierAndLiteralSequences());
        assertFalse(actualCpdConfiguration.isIgnoreIdentifiers());
        assertFalse(actualCpdConfiguration.isIgnoreLiteralSequences());
        assertFalse(actualCpdConfiguration.isIgnoreLiterals());
        assertFalse(actualCpdConfiguration.isIgnoreUsings());
        assertFalse(actualCpdConfiguration.isNoSkipBlocks());
        assertFalse(actualCpdConfiguration.isSkipDuplicates());
        assertTrue(actualCpdConfiguration.getExcludes().isEmpty());
        assertTrue(actualCpdConfiguration.getInputPathList().isEmpty());
        assertTrue(actualCpdConfiguration.getRelativizeRoots().isEmpty());
        assertTrue(actualCpdConfiguration.isFailOnError());
        assertTrue(actualCpdConfiguration.isFailOnViolation());
        assertTrue(actualCpdConfiguration.isSkipLexicalErrors());
        assertEquals(CPDConfiguration.DEFAULT_RENDERER, actualCpdConfiguration.getRendererName());
        LanguageRegistry expectedLanguageRegistry = languageRegistry.CPD;
        assertSame(expectedLanguageRegistry, actualCpdConfiguration.getLanguageRegistry());
    }
}
