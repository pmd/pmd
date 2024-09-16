package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.pmd.lang.document.FileId;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CPDReportRendererDiffblueTest {
    /**
     * Method under test: {@link CPDReportRenderer#renderToString(CPDReport)}
     */
    @Test
    void testRenderToString() {
        // Arrange
        CSVRenderer csvRenderer = new CSVRenderer('A');
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();

        // Act and Assert
        assertEquals("linesAtokensAoccurrences\n",
                csvRenderer.renderToString(CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>())));
    }

    /**
     * Method under test: {@link CPDReportRenderer#renderToString(CPDReport)}
     */
    @Test
    void testRenderToString2() {
        // Arrange
        CPDReportRenderer cpdReportRenderer = mock(CPDReportRenderer.class);
        when(cpdReportRenderer.renderToString(Mockito.<CPDReport>any())).thenReturn("Render To String");
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();

        // Act
        cpdReportRenderer.renderToString(CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>()));

        // Assert
        verify(cpdReportRenderer).renderToString(isA(CPDReport.class));
    }
}
