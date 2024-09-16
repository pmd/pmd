package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

import net.sourceforge.pmd.lang.document.FileId;
import org.junit.jupiter.api.Test;

class XMLOldRendererDiffblueTest {
    /**
     * Method under test: {@link XMLOldRenderer#XMLOldRenderer()}
     */
    @Test
    void testNewXMLOldRenderer() throws IOException {
        // Arrange and Act
        XMLOldRenderer actualXmlOldRenderer = new XMLOldRenderer();
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();
        actualXmlOldRenderer.render(report, writer);

        // Assert
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<pmd-cpd/>\n", writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#XMLOldRenderer()}
     */
    @Test
    void testNewXMLOldRenderer2() throws IOException {
        // Arrange and Act
        XMLOldRenderer actualXmlOldRenderer = new XMLOldRenderer();
        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();
        actualXmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#XMLOldRenderer()}
     */
    @Test
    void testNewXMLOldRenderer3() throws IOException {
        // Arrange and Act
        XMLOldRenderer actualXmlOldRenderer = new XMLOldRenderer();
        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        Mark first2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first2, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();
        actualXmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#XMLOldRenderer()}
     */
    @Test
    void testNewXMLOldRenderer4() throws IOException {
        // Arrange and Act
        XMLOldRenderer actualXmlOldRenderer = new XMLOldRenderer();
        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 1, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();
        actualXmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"1\"\n" + "            endtoken=\"0\"\n" + "            line=\"1\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[0_0_0_0_0_0_0_0_0_0_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#XMLOldRenderer(String)}
     */
    @Test
    void testNewXMLOldRenderer5() throws IOException {
        // Arrange and Act
        XMLOldRenderer actualXmlOldRenderer = new XMLOldRenderer("UTF-8");
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();
        actualXmlOldRenderer.render(report, writer);

        // Assert
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<pmd-cpd/>\n", writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#XMLOldRenderer(String)}
     */
    @Test
    void testNewXMLOldRenderer6() throws IOException {
        // Arrange and Act
        XMLOldRenderer actualXmlOldRenderer = new XMLOldRenderer("UTF-8");
        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();
        actualXmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#XMLOldRenderer(String)}
     */
    @Test
    void testNewXMLOldRenderer7() throws IOException {
        // Arrange and Act
        XMLOldRenderer actualXmlOldRenderer = new XMLOldRenderer("UTF-8");
        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        Mark first2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first2, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();
        actualXmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#XMLOldRenderer(String)}
     */
    @Test
    void testNewXMLOldRenderer8() throws IOException {
        // Arrange and Act
        XMLOldRenderer actualXmlOldRenderer = new XMLOldRenderer("UTF-8");
        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 1, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();
        actualXmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"1\"\n" + "            endtoken=\"0\"\n" + "            line=\"1\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[0_0_0_0_0_0_0_0_0_0_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender() throws IOException {
        // Arrange
        XMLOldRenderer xmlOldRenderer = new XMLOldRenderer();
        ArrayList<Match> matches = new ArrayList<>();
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        xmlOldRenderer.render(report, writer);

        // Assert
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<pmd-cpd/>\n", writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender2() throws IOException {
        // Arrange
        XMLOldRenderer xmlOldRenderer = new XMLOldRenderer();

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        xmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender3() throws IOException {
        // Arrange
        XMLOldRenderer xmlOldRenderer = new XMLOldRenderer();

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        Mark first2 = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1));
        matches.add(new Match(3, first2, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        xmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"2\"\n" + "            endtoken=\"0\"\n" + "            line=\"2\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[1_1_1_1_1_1_1_1_1_1_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender4() throws IOException {
        // Arrange
        XMLOldRenderer xmlOldRenderer = new XMLOldRenderer();

        ArrayList<Match> matches = new ArrayList<>();
        Mark first = new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 1, 1));
        matches.add(new Match(3, first, new Mark(new TokenEntry(CpdTestUtils.BAR_FILE_ID, 2, 1))));
        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        xmlOldRenderer.render(report, writer);

        // Assert
        assertEquals(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n" + "   <duplication lines=\"1\" tokens=\"3\">\n"
                        + "      <file begintoken=\"0\"\n" + "            column=\"1\"\n" + "            endcolumn=\"1\"\n"
                        + "            endline=\"1\"\n" + "            endtoken=\"0\"\n" + "            line=\"1\"\n"
                        + "            path=\"/var/Bar.java\"/>\n" + "      <codefragment><![CDATA[0_0_0_0_0_0_0_0_0_0_\n"
                        + "]]></codefragment>\n" + "   </duplication>\n" + "</pmd-cpd>\n",
                writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender5() throws IOException {
        // Arrange
        XMLOldRenderer xmlOldRenderer = new XMLOldRenderer();

        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        numTokensPerFile.put(CpdTestUtils.BAR_FILE_ID, 1);
        ArrayList<Match> matches = new ArrayList<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        xmlOldRenderer.render(report, writer);

        // Assert
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n"
                + "   <file path=\"/var/Bar.java\" totalNumberOfTokens=\"1\"/>\n" + "</pmd-cpd>\n", writer.toString());
    }

    /**
     * Method under test: {@link XMLOldRenderer#render(CPDReport, Writer)}
     */
    @Test
    void testRender6() throws IOException {
        // Arrange
        XMLOldRenderer xmlOldRenderer = new XMLOldRenderer();

        HashMap<FileId, Integer> numTokensPerFile = new HashMap<>();
        numTokensPerFile.computeIfPresent(CpdTestUtils.BAR_FILE_ID, mock(BiFunction.class));
        numTokensPerFile.put(CpdTestUtils.BAR_FILE_ID, 1);
        ArrayList<Match> matches = new ArrayList<>();
        CPDReport report = CpdTestUtils.makeReport(matches, numTokensPerFile, new ArrayList<>());
        StringWriter writer = new StringWriter();

        // Act
        xmlOldRenderer.render(report, writer);

        // Assert
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<pmd-cpd>\n"
                + "   <file path=\"/var/Bar.java\" totalNumberOfTokens=\"1\"/>\n" + "</pmd-cpd>\n", writer.toString());
    }
}
