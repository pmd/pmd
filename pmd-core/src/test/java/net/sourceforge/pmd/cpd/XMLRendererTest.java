/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.cpd.CpdTestUtils.CpdReportBuilder;
import net.sourceforge.pmd.lang.document.FileId;

/**
 * @author Philippe T'Seyen
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 */
class XMLRendererTest {

    private static final String ENCODING = (String) System.getProperties().get("file.encoding");
    private static final String FORM_FEED = "\u000C"; // this character is invalid in XML 1.0 documents
    private static final String FORM_FEED_ENTITY = "&#12;"; // this is also not allowed in XML 1.0 documents

    @Test
    void testWithNoDuplication() throws IOException, ParserConfigurationException, SAXException {
        CPDReportRenderer renderer = new XMLRenderer();
        StringWriter sw = new StringWriter();
        renderer.render(CpdTestUtils.makeReport(Collections.emptyList()), sw);
        String report = sw.toString();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                             .parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
        NodeList nodes = doc.getChildNodes();
        Node n = nodes.item(0);
        assertEquals("pmd-cpd", n.getNodeName());
        assertEquals(0, doc.getElementsByTagName("duplication").getLength());
    }

    @Test
    void testWithOneDuplication() throws Exception {
        CPDReportRenderer renderer = new XMLRenderer();
        CpdReportBuilder builder = new CpdReportBuilder();
        int lineCount = 6;
        FileId foo1 = CpdTestUtils.FOO_FILE_ID;
        Mark mark1 = builder.createMark("public", foo1, 1, lineCount);
        Mark mark2 = builder.createMark("stuff", foo1, 73, lineCount);
        builder.addMatch(new Match(75, mark1, mark2));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                             .parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
        NodeList dupes = doc.getElementsByTagName("duplication");
        assertEquals(1, dupes.getLength());
        Node file = dupes.item(0).getFirstChild();
        while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
            file = file.getNextSibling();
        }
        if (file != null) {
            assertEquals("1", file.getAttributes().getNamedItem("line").getNodeValue());
            assertEquals(foo1.getAbsolutePath(), file.getAttributes().getNamedItem("path").getNodeValue());
            assertEquals("6", file.getAttributes().getNamedItem("endline").getNodeValue());
            assertEquals("1", file.getAttributes().getNamedItem("column").getNodeValue());
            assertEquals("1", file.getAttributes().getNamedItem("endcolumn").getNodeValue());
            file = file.getNextSibling();
            while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
                file = file.getNextSibling();
            }
        }
        if (file != null) {
            assertEquals("73", file.getAttributes().getNamedItem("line").getNodeValue());
            assertEquals("78", file.getAttributes().getNamedItem("endline").getNodeValue());
            assertEquals("1", file.getAttributes().getNamedItem("column").getNodeValue());
            assertEquals("1", file.getAttributes().getNamedItem("endcolumn").getNodeValue());
        }
        assertEquals(1, doc.getElementsByTagName("codefragment").getLength());
        assertEquals(CpdTestUtils.generateDummyContent(lineCount), doc.getElementsByTagName("codefragment").item(0).getTextContent());
    }

    @Test
    void testRenderWithMultipleMatch() throws Exception {
        CPDReportRenderer renderer = new XMLRenderer();
        CpdReportBuilder builder = new CpdReportBuilder();
        int lineCount1 = 6;
        FileId foo1 = CpdTestUtils.FOO_FILE_ID;
        Mark mark1 = builder.createMark("public", foo1, 48, lineCount1);
        Mark mark2 = builder.createMark("void", foo1, 73, lineCount1);
        builder.addMatch(new Match(75, mark1, mark2));

        int lineCount2 = 7;
        FileId foo2 = FileId.fromPathLikeString("/var/Foo2.java");
        Mark mark3 = builder.createMark("void", foo2, 49, lineCount2);
        Mark mark4 = builder.createMark("stuff", foo2, 74, lineCount2);
        builder.addMatch(new Match(76, mark3, mark4));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                             .parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
        assertEquals(2, doc.getElementsByTagName("duplication").getLength());
        assertEquals(4, doc.getElementsByTagName("file").getLength());
    }

    @Test
    void testWithOneDuplicationWithColumns() throws Exception {
        CPDReportRenderer renderer = new XMLRenderer();
        int lineCount = 2;
        CpdReportBuilder builder = new CpdReportBuilder();
        FileId fileName = CpdTestUtils.FOO_FILE_ID;
        Mark mark1 = builder.createMark("public", fileName, 1, lineCount, 2, 3);
        Mark mark2 = builder.createMark("stuff", fileName, 24, lineCount, 4, 5);
        builder.addMatch(new Match(75, mark1, mark2));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                             .parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
        NodeList dupes = doc.getElementsByTagName("duplication");
        assertEquals(1, dupes.getLength());
        Node file = dupes.item(0).getFirstChild();
        while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
            file = file.getNextSibling();
        }
        if (file != null) {
            assertEquals("1", file.getAttributes().getNamedItem("line").getNodeValue());
            assertEquals(fileName.getAbsolutePath(), file.getAttributes().getNamedItem("path").getNodeValue());
            assertEquals("2", file.getAttributes().getNamedItem("endline").getNodeValue());
            assertEquals("2", file.getAttributes().getNamedItem("column").getNodeValue());
            assertEquals("3", file.getAttributes().getNamedItem("endcolumn").getNodeValue());
            file = file.getNextSibling();
            while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
                file = file.getNextSibling();
            }
        }
        if (file != null) {
            assertEquals("24", file.getAttributes().getNamedItem("line").getNodeValue());
            assertEquals("25", file.getAttributes().getNamedItem("endline").getNodeValue());
            assertEquals("4", file.getAttributes().getNamedItem("column").getNodeValue());
            assertEquals("5", file.getAttributes().getNamedItem("endcolumn").getNodeValue());
        }
        assertEquals(1, doc.getElementsByTagName("codefragment").getLength());
        assertEquals(CpdTestUtils.generateDummyContent(2), doc.getElementsByTagName("codefragment").item(0).getTextContent());
    }

    @Test
    void testRendererEncodedPath() throws IOException {
        CPDReportRenderer renderer = new XMLRenderer();
        CpdReportBuilder builder = new CpdReportBuilder();
        final String escapeChar = "&amp;";
        Mark mark1 = builder.createMark("public", FileId.fromPathLikeString("/var/A&oo.java"), 2, 6);
        Mark mark2 = builder.createMark("void", FileId.fromPathLikeString("/var/B&oo.java"), 17, 6);
        builder.addMatch(new Match(75, mark1, mark2));

        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();
        assertThat(report, containsString(escapeChar));
    }

    @Test
    void testFilesWithNumberOfTokens() throws IOException, ParserConfigurationException, SAXException {
        final CPDReportRenderer renderer = new XMLRenderer();
        CpdReportBuilder builder = new CpdReportBuilder();
        final FileId filename = CpdTestUtils.FOO_FILE_ID;
        final int lineCount = 2;
        final Mark mark1 = builder.createMark("public", filename, 1, lineCount, 2, 3);
        final Mark mark2 = builder.createMark("stuff", filename, 3, lineCount, 4, 5);
        builder.addMatch(new Match(75, mark1, mark2));
        builder.recordNumTokens(filename, 888);

        final CPDReport report = builder.build();

        final StringWriter writer = new StringWriter();
        renderer.render(report, writer);
        final String xmlOutput = writer.toString();
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                                   .parse(new ByteArrayInputStream(xmlOutput.getBytes(ENCODING)));
        final NodeList files = doc.getElementsByTagName("file");
        final Node file = files.item(0);
        final NamedNodeMap attributes = file.getAttributes();
        assertEquals(CpdTestUtils.FOO_FILE_ID.getAbsolutePath(), attributes.getNamedItem("path").getNodeValue());
        assertEquals("888", attributes.getNamedItem("totalNumberOfTokens").getNodeValue());
    }

    @Test
    void testGetDuplicationStartEnd() throws IOException, ParserConfigurationException, SAXException {
        final CPDReportRenderer renderer = new XMLRenderer();
        CpdReportBuilder builder = new CpdReportBuilder();
        final FileId filename = CpdTestUtils.FOO_FILE_ID;
        final int lineCount = 6;
        final Mark mark1 = builder.createMark("public", filename, 1, lineCount, 2, 3);
        final Mark mark2 = builder.createMark("stuff", filename, 73, lineCount, 4, 5);
        builder.addMatch(new Match(75, mark1, mark2));
        builder.recordNumTokens(filename, 888);

        final CPDReport report = builder.build();

        final StringWriter writer = new StringWriter();
        renderer.render(report, writer);
        final String xmlOutput = writer.toString();
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                                                   .parse(new ByteArrayInputStream(xmlOutput.getBytes(ENCODING)));
        final NodeList files = doc.getElementsByTagName("file");
        final Node dup_1 = files.item(1);
        final NamedNodeMap attrs_1 = dup_1.getAttributes();
        assertEquals("0", attrs_1.getNamedItem("begintoken").getNodeValue());
        assertEquals("1", attrs_1.getNamedItem("endtoken").getNodeValue());

        final Node dup_2 = files.item(2);
        final NamedNodeMap attrs_2 = dup_2.getAttributes();
        assertEquals("2", attrs_2.getNamedItem("begintoken").getNodeValue());
        assertEquals("3", attrs_2.getNamedItem("endtoken").getNodeValue());
    }

    @Test
    void testRendererXMLEscaping() throws IOException {
        String codefragment = "code fragment" + FORM_FEED
            + "\nline2\nline3\nno & escaping necessary in CDATA\nx=\"]]>\";";
        CPDReportRenderer renderer = new XMLRenderer();

        CpdReportBuilder builder = new CpdReportBuilder();
        FileId file1 = FileId.fromPathLikeString("file1");
        FileId file2 = FileId.fromPathLikeString("file2");
        Mark mark1 = builder.createMark("public", file1, 1, 5);
        Mark mark2 = builder.createMark("public", file2, 5, 5);
        Match match1 = new Match(75, mark1, mark2);
        builder.addMatch(match1);

        builder.setFileContent(file1, codefragment);
        StringWriter sw = new StringWriter();
        renderer.render(builder.build(), sw);
        String report = sw.toString();
        assertThat(report, not(containsString(FORM_FEED)));
        assertThat(report, not(containsString(FORM_FEED_ENTITY)));
        assertThat(report, containsString("no & escaping necessary in CDATA"));
        assertThat(report, containsString("x=\"]]]]><![CDATA[>\";"));
        assertThat(report, not(containsString("x=\"]]>\";"))); // must be escaped
    }
}
