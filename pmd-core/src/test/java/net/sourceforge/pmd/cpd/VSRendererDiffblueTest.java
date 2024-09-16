package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.pmd.lang.document.FileId;
import org.junit.jupiter.api.Test;

class VSRendererDiffblueTest {
    /**
     * Method under test: {@link VSRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender() throws IOException {
        // Arrange
        VSRenderer vsRenderer = new VSRenderer();
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        vsRenderer.render(report, writer);

        // Assert that nothing has changed
        assertEquals("", writer.toString());
    }

    /**
     * Method under test: {@link VSRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender2() throws IOException {
        // Arrange
        VSRenderer vsRenderer = new VSRenderer();

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        vsRenderer.render(report, writer);

        // Assert
        assertEquals("/var/Bar.java(2): Between lines 2 and 2\n", writer.toString());
    }

    /**
     * Method under test: {@link VSRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender3() throws IOException {
        // Arrange
        VSRenderer vsRenderer = new VSRenderer();
        FileId fileId = mock(FileId.class);
        when(fileId.getAbsolutePath()).thenReturn("Absolute Path");
        Mark first = new Mark(new TokenEntry(fileId, 2, 1));
        Match match = new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1)));

        ArrayList<Match> matches = new ArrayList<>();
        matches.add(match);
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        vsRenderer.render(report, writer);

        // Assert
        verify(fileId).getAbsolutePath();
        assertEquals("Absolute Path(2): Between lines 2 and 2\n", writer.toString());
    }
}
