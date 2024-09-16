package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import net.sourceforge.pmd.lang.document.FileId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class CSVRendererDiffblueTest {
    /**
     * Method under test: {@link CSVRenderer#CSVRenderer()}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewCSVRenderer() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CSVRenderer.lineCountPerFile
        //     CSVRenderer.separator

        // Arrange and Act
        // TODO: Populate arranged inputs
        CSVRenderer actualCsvRenderer = new CSVRenderer();

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CSVRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender() throws IOException {
        // Arrange
        CSVRenderer csvRenderer = new CSVRenderer('A');
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        csvRenderer.render(report, writer);

        // Assert
        assertEquals("linesAtokensAoccurrences\n", writer.toString());
    }

    /**
     * Method under test: {@link CSVRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender2() throws IOException {
        // Arrange
        CSVRenderer csvRenderer = new CSVRenderer(true);
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        csvRenderer.render(report, writer);

        // Assert
        assertEquals("tokens,occurrences\n", writer.toString());
    }

    /**
     * Method under test: {@link CSVRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender3() throws IOException {
        // Arrange
        CSVRenderer csvRenderer = new CSVRenderer('A');

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        csvRenderer.render(report, writer);

        // Assert
        assertEquals("linesAtokensAoccurrences\n1A3A1A2A/var/Bar.java\n", writer.toString());
    }

    /**
     * Method under test: {@link CSVRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender4() throws IOException {
        // Arrange
        CSVRenderer csvRenderer = new CSVRenderer('A');

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        Mark first2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first2, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        csvRenderer.render(report, writer);

        // Assert
        assertEquals("linesAtokensAoccurrences\n1A3A1A2A/var/Bar.java\n1A3A1A2A/var/Bar.java\n", writer.toString());
    }

    /**
     * Method under test: {@link CSVRenderer#CSVRenderer(char)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewCSVRenderer2() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CSVRenderer.lineCountPerFile
        //     CSVRenderer.separator

        // Arrange
        // TODO: Populate arranged inputs
        char separatorChar = 'a';

        // Act
        CSVRenderer actualCsvRenderer = new CSVRenderer(separatorChar);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CSVRenderer#CSVRenderer(char, boolean)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewCSVRenderer3() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CSVRenderer.lineCountPerFile
        //     CSVRenderer.separator

        // Arrange
        // TODO: Populate arranged inputs
        char separatorChar = 'a';
        boolean lineCountPerFile = false;

        // Act
        CSVRenderer actualCsvRenderer = new CSVRenderer(separatorChar, lineCountPerFile);

        // Assert
        // TODO: Add assertions on result
    }

    /**
     * Method under test: {@link CSVRenderer#CSVRenderer(boolean)}
     */
    @Test
    @Disabled("TODO: Complete this test")
    void testNewCSVRenderer4() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     CSVRenderer.lineCountPerFile
        //     CSVRenderer.separator

        // Arrange
        // TODO: Populate arranged inputs
        boolean lineCountPerFile = false;

        // Act
        CSVRenderer actualCsvRenderer = new CSVRenderer(lineCountPerFile);

        // Assert
        // TODO: Add assertions on result
    }
}
