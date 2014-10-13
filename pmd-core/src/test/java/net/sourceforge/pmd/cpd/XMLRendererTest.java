/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Philippe T'Seyen
 * @author Romain Pelisse <belaran@gmail.com>
 * 
 */
public class XMLRendererTest {

	private final static String ENCODING = (String) System.getProperties().get("file.encoding");
	
    @Test
    public void testWithNoDuplication() {

        Renderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<Match>();
        String report = renderer.render(list.iterator());
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
            NodeList nodes = doc.getChildNodes();
            Node n = nodes.item(0);
            assertEquals("pmd-cpd", n.getNodeName());
            assertEquals(0, doc.getElementsByTagName("duplication").getLength());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testWithOneDuplication() {
        Renderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<Match>();
        Match match = new Match(75, new TokenEntry("public", "/var/Foo.java", 48), new TokenEntry("stuff", "/var/Foo.java", 73));
        match.setLineCount(6);
        match.setSourceCodeSlice("code\nfragment");
        list.add(match);
        String report = renderer.render(list.iterator());
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
            NodeList dupes = doc.getElementsByTagName("duplication");
            assertEquals(1, dupes.getLength());
            Node file = dupes.item(0).getFirstChild();
            while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
                file = file.getNextSibling();
            }
            if (file != null) {
            	assertEquals("48", file.getAttributes().getNamedItem("line").getNodeValue());
                assertEquals("/var/Foo.java", file.getAttributes().getNamedItem("path").getNodeValue());
	            file = file.getNextSibling();
	            while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
	                file = file.getNextSibling();
	            }
            }
            if (file != null) assertEquals("73", file.getAttributes().getNamedItem("line").getNodeValue());
            assertEquals(1, doc.getElementsByTagName("codefragment").getLength());
            assertEquals("code\nfragment", doc.getElementsByTagName("codefragment").item(0).getTextContent());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRenderWithMultipleMatch() {
        Renderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<Match>();
        Match match1 = new Match(75, new TokenEntry("public", "/var/Foo.java", 48), new TokenEntry("void", "/var/Foo.java", 73));
        match1.setLineCount(6);
        match1.setSourceCodeSlice("code fragment");
        Match match2 = new Match(76, new TokenEntry("void", "/var/Foo2.java", 49), new TokenEntry("stuff", "/var/Foo2.java", 74));
        match2.setLineCount(7);
        match2.setSourceCodeSlice("code fragment 2");
        list.add(match1);
        list.add(match2);
        String report = renderer.render(list.iterator());
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes(ENCODING)));
            assertEquals(2, doc.getElementsByTagName("duplication").getLength());
            assertEquals(4, doc.getElementsByTagName("file").getLength());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void testRendererEncodedPath() {
        Renderer renderer = new XMLRenderer();
        List<Match> list = new ArrayList<Match>();
        final String espaceChar = "&lt;";
        Match match1 = new Match(75, new TokenEntry("public", "/var/F" + '<' + "oo.java", 48), new TokenEntry("void", "/var/F<oo.java", 73));
        match1.setLineCount(6);
        match1.setSourceCodeSlice("code fragment");
        list.add(match1);
        String report = renderer.render(list.iterator());
        assertTrue(report.contains(espaceChar));
    } 
    
    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(XMLRendererTest.class);
    }
}

