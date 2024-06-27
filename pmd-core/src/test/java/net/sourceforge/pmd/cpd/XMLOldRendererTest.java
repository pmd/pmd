/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import net.sourceforge.pmd.lang.document.FileId;

class XMLOldRendererTest {
    private static final String ENCODING = (String) System.getProperties().get("file.encoding");

    @Test
    void testWithNoDuplication() throws IOException, ParserConfigurationException, SAXException {
        CPDReportRenderer renderer = new XMLOldRenderer();
        StringWriter sw = new StringWriter();
        renderer.render(CpdTestUtils.makeReport(Collections.emptyList()), sw);
        String report = sw.toString();

        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<pmd-cpd/>\n",
                report,
                "no namespace expected");

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
        NodeList nodes = doc.getChildNodes();
        Node n = nodes.item(0);
        assertEquals("pmd-cpd", n.getNodeName());
        assertEquals(0, doc.getElementsByTagName("duplication").getLength());
    }

    @Test
    void testWithOneDuplication() throws Exception {
        CPDReportRenderer renderer = new XMLOldRenderer();
        CpdTestUtils.CpdReportBuilder builder = new CpdTestUtils.CpdReportBuilder();
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
}
