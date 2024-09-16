package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.pmd.lang.document.Chars;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.reporting.Report;
import org.junit.jupiter.api.Test;

class CPDReportDiffblueTest {
    /**
     * Method under test: {@link CPDReport#getSourceCodeSlice(Mark)}
     */
    @Test
    void testGetSourceCodeSlice() {
        // Arrange
        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport makeReportResult = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());

        // Act
        Chars actualSourceCodeSlice = makeReportResult
                .getSourceCodeSlice(new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));

        // Assert
        assertEquals(21, actualSourceCodeSlice.length());
        assertFalse(actualSourceCodeSlice.isEmpty());
    }

    /**
     * Method under test: {@link CPDReport#filterMatches(Predicate)}
     */
    @Test
    void testFilterMatches() {
        // Arrange
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();

        // Act
        CPDReport actualFilterMatchesResult = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>())
                .filterMatches(mock(Predicate.class));

        // Assert
        assertTrue(actualFilterMatchesResult.getMatches().isEmpty());
        assertTrue(actualFilterMatchesResult.getProcessingErrors().isEmpty());
        assertTrue(actualFilterMatchesResult.getNumberOfTokensPerFile().isEmpty());
    }

    /**
     * Method under test: {@link CPDReport#getDisplayName(FileId)}
     */
    @Test
    void testGetDisplayName() {
        // Arrange
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();

        // Act and Assert
        assertEquals("/var/Bar.java",
                CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>()).getDisplayName(CpdTestUtils.BAR_FILE_ID));
    }

    /**
     * Method under test: {@link CPDReport#getDisplayName(FileId)}
     */
    @Test
    void testGetDisplayName2() {
        // Arrange
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport makeReportResult = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        FileId fileId = mock(FileId.class);
        when(fileId.getAbsolutePath()).thenReturn("Absolute Path");

        // Act
        String actualDisplayName = makeReportResult.getDisplayName(fileId);

        // Assert
        verify(fileId).getAbsolutePath();
        assertEquals("Absolute Path", actualDisplayName);
    }

    /**
     * Methods under test:
     * <ul>
     *   <li>{@link CPDReport#getMatches()}
     *   <li>{@link CPDReport#getNumberOfTokensPerFile()}
     *   <li>{@link CPDReport#getProcessingErrors()}
     * </ul>
     */
    @Test
    void testGettersAndSetters() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numberOfTokensPerFile = new HashMap<>();
        CPDReport cpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile, new ArrayList<>());

        // Act
        List<Match> actualMatches = cpdReport.getMatches();
        Map<FileId, Integer> actualNumberOfTokensPerFile = cpdReport.getNumberOfTokensPerFile();
        List<Report.ProcessingError> actualProcessingErrors = cpdReport.getProcessingErrors();

        // Assert
        assertTrue(actualMatches.isEmpty());
        assertTrue(actualProcessingErrors.isEmpty());
        assertTrue(actualNumberOfTokensPerFile.isEmpty());
    }

    /**
     * Method under test:
     * {@link CPDReport#CPDReport(SourceManager, List, Map, List)}
     */
    @Test
    void testNewCPDReport() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numberOfTokensPerFile = new HashMap<>();

        // Act
        CPDReport actualCpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile, new ArrayList<>());

        // Assert
        assertTrue(actualCpdReport.getMatches().isEmpty());
        assertTrue(actualCpdReport.getProcessingErrors().isEmpty());
        assertTrue(actualCpdReport.getNumberOfTokensPerFile().isEmpty());
    }

    /**
     * Method under test:
     * {@link CPDReport#CPDReport(SourceManager, List, Map, List)}
     */
    @Test
    void testNewCPDReport2() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numberOfTokensPerFile = new HashMap<>();

        // Act
        CPDReport actualCpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile, new ArrayList<>());

        // Assert
        assertTrue(actualCpdReport.getProcessingErrors().isEmpty());
        assertTrue(actualCpdReport.getNumberOfTokensPerFile().isEmpty());
        assertEquals(matches, actualCpdReport.getMatches());
    }

    /**
     * Method under test:
     * {@link CPDReport#CPDReport(SourceManager, List, Map, List)}
     */
    @Test
    void testNewCPDReport3() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        Mark first2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first2, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numberOfTokensPerFile = new HashMap<>();

        // Act
        CPDReport actualCpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile, new ArrayList<>());

        // Assert
        assertTrue(actualCpdReport.getProcessingErrors().isEmpty());
        assertTrue(actualCpdReport.getNumberOfTokensPerFile().isEmpty());
        assertEquals(matches, actualCpdReport.getMatches());
    }

    /**
     * Method under test:
     * {@link CPDReport#CPDReport(SourceManager, List, Map, List)}
     */
    @Test
    void testNewCPDReport4() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numberOfTokensPerFile = new HashMap<>();

        ArrayList<Report.ProcessingError> processingErrors = new ArrayList<>();
        processingErrors.add(new Report.ProcessingError(new Throwable(), CpdTestUtils.BAR_FILE_ID));

        // Act
        CPDReport actualCpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile, processingErrors);

        // Assert
        assertTrue(actualCpdReport.getMatches().isEmpty());
        assertTrue(actualCpdReport.getNumberOfTokensPerFile().isEmpty());
        assertEquals(processingErrors, actualCpdReport.getProcessingErrors());
    }

    /**
     * Method under test:
     * {@link CPDReport#CPDReport(SourceManager, List, Map, List)}
     */
    @Test
    void testNewCPDReport5() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numberOfTokensPerFile = new HashMap<>();

        ArrayList<Report.ProcessingError> processingErrors = new ArrayList<>();
        processingErrors.add(new Report.ProcessingError(new Throwable(), CpdTestUtils.BAR_FILE_ID));
        processingErrors.add(new Report.ProcessingError(new Throwable(), CpdTestUtils.BAR_FILE_ID));

        // Act
        CPDReport actualCpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile, processingErrors);

        // Assert
        assertTrue(actualCpdReport.getMatches().isEmpty());
        assertTrue(actualCpdReport.getNumberOfTokensPerFile().isEmpty());
        assertEquals(processingErrors, actualCpdReport.getProcessingErrors());
    }

    /**
     * Method under test:
     * {@link CPDReport#CPDReport(SourceManager, List, Map, List)}
     */
    @Test
    void testNewCPDReport6() {
        // Arrange
        SourceManager sourceManager = new SourceManager(new ArrayList<>());

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(mock(FileId.class), 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numberOfTokensPerFile = new HashMap<>();

        // Act
        CPDReport actualCpdReport = new CPDReport(sourceManager, matches, numberOfTokensPerFile, new ArrayList<>());

        // Assert
        assertTrue(actualCpdReport.getProcessingErrors().isEmpty());
        assertTrue(actualCpdReport.getNumberOfTokensPerFile().isEmpty());
        assertEquals(matches, actualCpdReport.getMatches());
    }
}
