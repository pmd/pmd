/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.Match;
import net.sourceforge.pmd.cpd.Renderer;
import net.sourceforge.pmd.cpd.TokenEntry;
import net.sourceforge.pmd.cpd.XMLRenderer;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philippe T'Seyen
 */
public class XMLRendererTest extends TestCase {
    public void test_no_dupes() {
        Renderer renderer = new XMLRenderer();
        List list = new ArrayList();
        String report = renderer.render(list.iterator());
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes()));
            NodeList nodes = doc.getChildNodes();
            Node n = nodes.item(0);
            assertEquals("pmd-cpd", n.getNodeName());
            assertEquals(0, doc.getElementsByTagName("duplication").getLength());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void test_one_dupe() {
        Renderer renderer = new XMLRenderer();
        List list = new ArrayList();
        Match match = new Match(75, new TokenEntry("public", "/var/Foo.java", 48), new TokenEntry("stuff", "/var/Foo.java", 73));
        match.setLineCount(6);
        match.setSourceCodeSlice("code fragment");
        list.add(match);
        String report = renderer.render(list.iterator());
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes()));
            NodeList dupes = doc.getElementsByTagName("duplication");
            assertEquals(1, dupes.getLength());
            Node file = dupes.item(0).getFirstChild();
            while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
                file = file.getNextSibling();
            }
            assertEquals("48", file.getAttributes().getNamedItem("line").getNodeValue());
            assertEquals("/var/Foo.java", file.getAttributes().getNamedItem("path").getNodeValue());

            file = file.getNextSibling();
            while (file != null && file.getNodeType() != Node.ELEMENT_NODE) {
                file = file.getNextSibling();
            }
            assertEquals("73", file.getAttributes().getNamedItem("line").getNodeValue());

            assertEquals(1, doc.getElementsByTagName("codefragment").getLength());
            Node actualCode = doc.getElementsByTagName("codefragment").item(0).getFirstChild().getNextSibling();
            assertEquals("\ncode fragment\n", actualCode.getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    public void testRender_MultipleMatch() {
        Renderer renderer = new XMLRenderer();
        List list = new ArrayList();
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
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(report.getBytes()));
            assertEquals(2, doc.getElementsByTagName("duplication").getLength());
            assertEquals(4, doc.getElementsByTagName("file").getLength());
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
}

