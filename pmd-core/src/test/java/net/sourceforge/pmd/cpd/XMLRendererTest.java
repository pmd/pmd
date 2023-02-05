/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.cpd.renderer.CPDReportRenderer;

/**
 * @author Philippe T'Seyen
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
class XMLRendererTest {

    private static final String ENCODING = (String) System.getProperties().get("file.encoding");
    private static final String FORM_FEED = "\u000C"; // this character is invalid in XML 1.0 documents
    private static final String FORM_FEED_ENTITY = "&#12;"; // this is also not allowed in XML 1.0 documents


    @Test
    void testWithNoDuplication() throws IOException, ParserConfigurationException, SAXException {
        CPDReportRenderer renderer = new XMLRenderer();
        StringWriter sw = new StringWriter();
        renderer.render(new CPDReport(Collections.emptyList(), Collections.emptyMap()), sw);
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
        List<Match> list = new ArrayList<>();
        int lineCount = 6;
        String codeFragment = "code\nfragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 1, lineCount, codeFragment);
        Mark mark2 = createMark("stuff", "/var/Foo.java", 73, lineCount, codeFragment);
        Match match = new Match(75, mark1, mark2);

        list.add(match);
        StringWriter sw = new StringWriter();
        renderer.render(new CPDReport(list, Collections.emptyMap()), sw);
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
            assertEquals("/var/Foo.java", file.getAttributes().getNamedItem("path").getNodeValue());
            assertEquals("6", file.getAttributes().getNamedItem("endline").getNodeValue());
            assertEquals(null, file.getAttributes().getNamedItem("column"));
            assertEquals(null, file.getAttributes().getNamedItem("endcolumn"));
            file = file.getNextSibling();
            while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
                file = file.getNextSibling();
            }
        }
        if (file != null) {
            assertEquals("73", file.getAttributes().getNamedItem("line").getNodeValue());
            assertEquals("78", file.getAttributes().getNamedItem("endline").getNodeValue());
            assertEquals(null, file.getAttributes().getNamedItem("column"));
            assertEquals(null, file.getAttributes().getNamedItem("endcolumn"));
        }
        assertEquals(1, doc.getElementsByTagName("codefragment").getLength());
        assertEquals(codeFragment, doc.getElementsByTagName("codefragment").item(0).getTextContent());
    }

    @Test
    void testRenderWithMultipleMatch() throws Exception {
        CPDReportRenderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<>();
        int lineCount1 = 6;
        String codeFragment1 = "code fragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 48, lineCount1, codeFragment1);
        Mark mark2 = createMark("void", "/var/Foo.java", 73, lineCount1, codeFragment1);
        Match match1 = new Match(75, mark1, mark2);

        int lineCount2 = 7;
        String codeFragment2 = "code fragment 2";
        Mark mark3 = createMark("void", "/var/Foo2.java", 49, lineCount2, codeFragment2);
        Mark mark4 = createMark("stuff", "/var/Foo2.java", 74, lineCount2, codeFragment2);
        Match match2 = new Match(76, mark3, mark4);

        list.add(match1);
        list.add(match2);
        StringWriter sw = new StringWriter();
        renderer.render(new CPDReport(list, Collections.emptyMap()), sw);
        String report = sw.toString();

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
        assertEquals(2, doc.getElementsByTagName("duplication").getLength());
        assertEquals(4, doc.getElementsByTagName("file").getLength());
    }

    @Test
    void testWithOneDuplicationWithColumns() throws Exception {
        CPDReportRenderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<>();
        int lineCount = 6;
        String codeFragment = "code\nfragment";
        Mark mark1 = createMark("public", "/var/Foo.java", 1, lineCount, codeFragment, 2, 3);
        Mark mark2 = createMark("stuff", "/var/Foo.java", 73, lineCount, codeFragment, 4, 5);
        Match match = new Match(75, mark1, mark2);

        list.add(match);
        StringWriter sw = new StringWriter();
        renderer.render(new CPDReport(list, Collections.emptyMap()), sw);
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
            assertEquals("/var/Foo.java", file.getAttributes().getNamedItem("path").getNodeValue());
            assertEquals("6", file.getAttributes().getNamedItem("endline").getNodeValue());
            assertEquals("2", file.getAttributes().getNamedItem("column").getNodeValue());
            assertEquals("3", file.getAttributes().getNamedItem("endcolumn").getNodeValue());
            file = file.getNextSibling();
            while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
                file = file.getNextSibling();
            }
        }
        if (file != null) {
            assertEquals("73", file.getAttributes().getNamedItem("line").getNodeValue());
            assertEquals("78", file.getAttributes().getNamedItem("endline").getNodeValue());
            assertEquals("4", file.getAttributes().getNamedItem("column").getNodeValue());
            assertEquals("5", file.getAttributes().getNamedItem("endcolumn").getNodeValue());
        }
        assertEquals(1, doc.getElementsByTagName("codefragment").getLength());
        assertEquals(codeFragment, doc.getElementsByTagName("codefragment").item(0).getTextContent());
    }

    @Test
    void testRendererEncodedPath() throws IOException {
        CPDReportRenderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<>();
        final String espaceChar = "&lt;";
        Mark mark1 = createMark("public", "/var/A<oo.java" + FORM_FEED, 48, 6, "code fragment");
        Mark mark2 = createMark("void", "/var/B<oo.java", 73, 6, "code fragment");
        Match match1 = new Match(75, mark1, mark2);
        list.add(match1);

        StringWriter sw = new StringWriter();
        renderer.render(new CPDReport(list, Collections.emptyMap()), sw);
        String report = sw.toString();
        assertTrue(report.contains(espaceChar));
        assertFalse(report.contains(FORM_FEED));
        assertFalse(report.contains(FORM_FEED_ENTITY));
    }

    @Test
    void testFilesWithNumberOfTokens() throws IOException, ParserConfigurationException, SAXException {
        final CPDReportRenderer renderer = new XMLRenderer();
        final List<Match> matches = new ArrayList<>();
        final String filename = "/var/Foo.java";
        final int lineCount = 6;
        final String codeFragment = "code\nfragment";
        final Mark mark1 = createMark("public", filename, 1, lineCount, codeFragment, 2, 3);
        final Mark mark2 = createMark("stuff", filename, 73, lineCount, codeFragment, 4, 5);
        final Match match = new Match(75, mark1, mark2);
        matches.add(match);
        final Map<String, Integer> numberOfTokensPerFile = new HashMap<>();
        numberOfTokensPerFile.put(filename, 888);
        final CPDReport report = new CPDReport(matches, numberOfTokensPerFile);
        final StringWriter writer = new StringWriter();
        renderer.render(report, writer);
        final String xmlOutput = writer.toString();
        final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(xmlOutput.getBytes(ENCODING)));
        final NodeList files = doc.getElementsByTagName("file");
        final Node file = files.item(0);
        final NamedNodeMap attributes = file.getAttributes();
        assertEquals("/var/Foo.java", attributes.getNamedItem("path").getNodeValue());
        assertEquals("888", attributes.getNamedItem("totalNumberOfTokens").getNodeValue());
    }

    @Test
    void testGetDuplicationStartEnd() throws IOException, ParserConfigurationException, SAXException {
        TokenEntry.clearImages();
        final CPDReportRenderer renderer = new XMLRenderer();
        final List<Match> matches = new ArrayList<>();
        final String filename = "/var/Foo.java";
        final int lineCount = 6;
        final String codeFragment = "code\nfragment";
        final Mark mark1 = createMark("public", filename, 1, lineCount, codeFragment, 2, 3);
        final Mark mark2 = createMark("stuff", filename, 73, lineCount, codeFragment, 4, 5);
        final Match match = new Match(75, mark1, mark2);
        matches.add(match);
        final Map<String, Integer> numberOfTokensPerFile = new HashMap<>();
        numberOfTokensPerFile.put(filename, 888);
        final CPDReport report = new CPDReport(matches, numberOfTokensPerFile);
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
        String codefragment = "code fragment" + FORM_FEED + "\nline2\nline3\nno & escaping necessary in CDATA\nx=\"]]>\";";
        CPDReportRenderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<>();
        Mark mark1 = createMark("public", "file1", 1, 5, codefragment);
        Mark mark2 = createMark("public", "file2", 5, 5, codefragment);
        Match match1 = new Match(75, mark1, mark2);
        list.add(match1);

        StringWriter sw = new StringWriter();
        renderer.render(new CPDReport(list, Collections.emptyMap()), sw);
        String report = sw.toString();
        assertFalse(report.contains(FORM_FEED));
        assertFalse(report.contains(FORM_FEED_ENTITY));
        assertTrue(report.contains("no & escaping necessary in CDATA"));
        assertFalse(report.contains("x=\"]]>\";")); // must be escaped
        assertTrue(report.contains("x=\"]]]]><![CDATA[>\";"));
    }

    private Mark createMark(String image, String tokenSrcID, int beginLine, int lineCount, String code) {
        Mark result = new Mark(new TokenEntry(image, tokenSrcID, beginLine));

        result.setLineCount(lineCount);
        result.setSourceCode(new SourceCode(new SourceCode.StringCodeLoader(code)));
        return result;
    }

    private Mark createMark(String image, String tokenSrcID, int beginLine, int lineCount, String code, int beginColumn, int endColumn) {
        final TokenEntry beginToken = new TokenEntry(image, tokenSrcID, beginLine, beginColumn, beginColumn + 1);
        final TokenEntry endToken = new TokenEntry(image, tokenSrcID, beginLine + lineCount, endColumn - 1, endColumn);
        final Mark result = new Mark(beginToken);

        result.setLineCount(lineCount);
        result.setEndToken(endToken);
        result.setSourceCode(new SourceCode(new SourceCode.StringCodeLoader(code)));
        return result;
    }
}
