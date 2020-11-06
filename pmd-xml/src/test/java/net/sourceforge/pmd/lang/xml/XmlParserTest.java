/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import static net.sourceforge.pmd.lang.xml.XmlParsingHelper.XML;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.xml.ast.XmlNode;
import net.sourceforge.pmd.lang.xml.ast.XmlParser;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Unit test for the {@link XmlParser}.
 */
public class XmlParserTest {

    private static final String XML_TEST = "<?xml version=\"1.0\"?>\n" + "<!DOCTYPE rootElement\n" + "[\n"
            + "<!ELEMENT rootElement (child1,child2)>\n" + "<!ELEMENT child1 (#PCDATA)>\n"
            + "<!ATTLIST child1 test CDATA #REQUIRED>\n" + "<!ELEMENT child2 (#PCDATA)>\n" + "\n"
            + "<!ENTITY pmd \"Copyright: PMD\">\n" + "]\n" + ">\n" + "<rootElement>\n"
            + "    <!-- that's a comment -->\n" + "    <child1 test=\"1\">entity: &pmd;\n" + "    </child1>\n"
            + "    <child2>\n" + "      <![CDATA[ cdata section ]]>\n" + "    </child2>\n" + "</rootElement>";

    private static final String XML_NAMESPACE_TEST = "<?xml version=\"1.0\"?>\n"
            + "<pmd:rootElement xmlns:pmd=\"http://pmd.sf.net\">\n" + "    <!-- that's a comment -->\n"
            + "    <pmd:child1 test=\"1\">entity: &amp;\n" + "    </pmd:child1>\n" + "    <pmd:child2>\n"
            + "      <![CDATA[ cdata section ]]>\n" + "    </pmd:child2>\n" + "</pmd:rootElement>";

    private static final String XML_INVALID_WITH_DTD = "<?xml version=\"1.0\"?>\n" + "<!DOCTYPE rootElement\n" + "[\n"
            + "<!ELEMENT rootElement (child)>\n" + "<!ELEMENT child (#PCDATA)>\n" + "]\n" + ">\n" + "<rootElement>\n"
            + "  <invalidChild></invalidChild>\n" + "</rootElement>";

    /**
     * See bug #1054: XML Rules ever report a line -1 and not the line/column
     * where the error occurs
     *
     */
    @Test
    public void testLineNumbers() {
        Node document = XML.parse(XML_TEST);

        assertNode(document, "document", 2);
        assertLineNumbers(document, 1, 1, 19, 14);
        Node dtdElement = document.getChild(0);
        assertNode(dtdElement, "rootElement", 0);
        assertLineNumbers(dtdElement, 2, 1, 11, 1);
        Node rootElement = document.getChild(1);
        assertNode(rootElement, "rootElement", 7);
        assertLineNumbers(rootElement, 12, 1, 19, 14);
        assertTextNode(rootElement.getChild(0), "\\n    ");
        assertLineNumbers(rootElement.getChild(0), 12, 14, 13, 4);
        assertNode(rootElement.getChild(1), "comment", 0);
        assertLineNumbers(rootElement.getChild(1), 13, 5, 13, 29);
        assertTextNode(rootElement.getChild(2), "\\n    ");
        assertLineNumbers(rootElement.getChild(2), 13, 30, 14, 4);
        Node child1 = rootElement.getChild(3);
        assertNode(child1, "child1", 1, "test", "1");
        assertLineNumbers(child1, 14, 5, 15, 13);
        assertTextNode(child1.getChild(0), "entity: Copyright: PMD\\n    ");
        assertLineNumbers(child1.getChild(0), 14, 22, 15, 4);
        assertTextNode(rootElement.getChild(4), "\\n    ");
        assertLineNumbers(rootElement.getChild(4), 15, 14, 16, 4);
        Node child2 = rootElement.getChild(5);
        assertNode(child2, "child2", 3);
        assertLineNumbers(child2, 16, 5, 18, 13);
        assertTextNode(child2.getChild(0), "\\n      ");
        assertLineNumbers(child2.getChild(0), 16, 13, 17, 6);
        assertTextNode(child2.getChild(1), " cdata section ", "cdata-section");
        assertLineNumbers(child2.getChild(1), 17, 7, 17, 33);
        assertTextNode(child2.getChild(2), "\\n    ");
        assertLineNumbers(child2.getChild(2), 17, 34, 18, 4);
        assertTextNode(rootElement.getChild(6), "\\n");
        assertLineNumbers(rootElement.getChild(6), 18, 14, 18, 14);
    }

    /**
     * Verifies the default parsing behavior of the XML parser.
     */
    @Test
    public void testDefaultParsing() {
        Node document = XML.parse(XML_TEST);

        assertNode(document, "document", 2);
        Node dtdElement = document.getChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.getChild(1);
        assertNode(rootElement, "rootElement", 7);
        assertTextNode(rootElement.getChild(0), "\\n    ");
        assertNode(rootElement.getChild(1), "comment", 0);
        assertTextNode(rootElement.getChild(2), "\\n    ");
        Node child1 = rootElement.getChild(3);
        assertNode(child1, "child1", 1, "test", "1");
        assertTextNode(child1.getChild(0), "entity: Copyright: PMD\\n    ");
        assertTextNode(rootElement.getChild(4), "\\n    ");
        Node child2 = rootElement.getChild(5);
        assertNode(child2, "child2", 3);
        assertTextNode(child2.getChild(0), "\\n      ");
        assertTextNode(child2.getChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.getChild(2), "\\n    ");
        assertTextNode(rootElement.getChild(6), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser with coalescing enabled.
     */
    @Test
    public void testParsingCoalescingEnabled() {
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setCoalescing(true);
        Node document = XML.withParserOptions(parserOptions).parse(XML_TEST);

        assertNode(document, "document", 2);
        Node dtdElement = document.getChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.getChild(1);
        assertNode(rootElement, "rootElement", 7);
        assertTextNode(rootElement.getChild(0), "\\n    ");
        assertNode(rootElement.getChild(1), "comment", 0);
        assertTextNode(rootElement.getChild(2), "\\n    ");
        Node child1 = rootElement.getChild(3);
        assertNode(child1, "child1", 1, "test", "1");
        assertTextNode(child1.getChild(0), "entity: Copyright: PMD\\n    ");
        assertTextNode(rootElement.getChild(4), "\\n    ");
        Node child2 = rootElement.getChild(5);
        assertNode(child2, "child2", 1);
        assertTextNode(child2.getChild(0), "\\n       cdata section \\n    ");
        assertTextNode(rootElement.getChild(6), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser if entities are not
     * expanded.
     */
    @Test
    public void testParsingDoNotExpandEntities() {
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setExpandEntityReferences(false);
        Node document = XML.withParserOptions(parserOptions).parse(XML_TEST);

        assertNode(document, "document", 2);
        Node dtdElement = document.getChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.getChild(1);
        assertNode(rootElement, "rootElement", 7);
        assertTextNode(rootElement.getChild(0), "\\n    ");
        assertNode(rootElement.getChild(1), "comment", 0);
        assertTextNode(rootElement.getChild(2), "\\n    ");
        Node child1 = rootElement.getChild(3);
        assertNode(child1, "child1", 3, "test", "1");
        assertTextNode(child1.getChild(0), "entity: ");
        assertNode(child1.getChild(1), "pmd", 0);
        // with java13, expandEntityReferences=false works correctly, and the
        // entity &pmd; is not expanded
        String text = child1.getChild(2).getImage();
        if ("\n    ".equals(text)) {
            // java13 and later
            assertTextNode(child1.getChild(2), "\\n    ");
        } else {
            assertTextNode(child1.getChild(2), "Copyright: PMD\\n    ");
        }
        assertTextNode(rootElement.getChild(4), "\\n    ");
        Node child2 = rootElement.getChild(5);
        assertNode(child2, "child2", 3);
        assertTextNode(child2.getChild(0), "\\n      ");
        assertTextNode(child2.getChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.getChild(2), "\\n    ");
        assertTextNode(rootElement.getChild(6), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser if ignoring comments.
     */
    @Test
    public void testParsingIgnoreComments() {
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setIgnoringComments(true);
        Node document = XML.withParserOptions(parserOptions).parse(XML_TEST);

        assertNode(document, "document", 2);
        Node dtdElement = document.getChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.getChild(1);
        assertNode(rootElement, "rootElement", 5);
        assertTextNode(rootElement.getChild(0), "\\n    \\n    ");
        Node child1 = rootElement.getChild(1);
        assertNode(child1, "child1", 1, "test", "1");
        assertTextNode(child1.getChild(0), "entity: Copyright: PMD\\n    ");
        assertTextNode(rootElement.getChild(2), "\\n    ");
        Node child2 = rootElement.getChild(3);
        assertNode(child2, "child2", 3);
        assertTextNode(child2.getChild(0), "\\n      ");
        assertTextNode(child2.getChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.getChild(2), "\\n    ");
        assertTextNode(rootElement.getChild(4), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser if ignoring whitespaces
     * in elements.
     */
    @Test
    public void testParsingIgnoreElementContentWhitespace() {
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setIgnoringElementContentWhitespace(true);
        Node document = XML.withParserOptions(parserOptions).parse(XML_TEST);

        assertNode(document, "document", 2);
        Node dtdElement = document.getChild(0);
        assertNode(dtdElement, "rootElement", 0);
        Node rootElement = document.getChild(1);
        assertNode(rootElement, "rootElement", 3);
        assertNode(rootElement.getChild(0), "comment", 0);
        Node child1 = rootElement.getChild(1);
        assertNode(child1, "child1", 1, "test", "1");
        assertTextNode(child1.getChild(0), "entity: Copyright: PMD\\n    ");
        Node child2 = rootElement.getChild(2);
        assertNode(child2, "child2", 3);
        assertTextNode(child2.getChild(0), "\\n      ");
        assertTextNode(child2.getChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.getChild(2), "\\n    ");
    }

    /**
     * Verifies the default parsing behavior of the XML parser with namespaces.
     */
    @Test
    public void testDefaultParsingNamespaces() {
        Node document = XML.parse(XML_NAMESPACE_TEST);

        assertNode(document, "document", 1);
        Node rootElement = document.getChild(0);
        assertNode(rootElement, "pmd:rootElement", 7, "xmlns:pmd", "http://pmd.sf.net");
        Assert.assertEquals("http://pmd.sf.net", ((XmlNode) rootElement).getNode().getNamespaceURI());
        Assert.assertEquals("pmd", ((XmlNode) rootElement).getNode().getPrefix());
        Assert.assertEquals("rootElement", ((XmlNode) rootElement).getNode().getLocalName());
        Assert.assertEquals("pmd:rootElement", ((XmlNode) rootElement).getNode().getNodeName());
        assertTextNode(rootElement.getChild(0), "\\n    ");
        assertNode(rootElement.getChild(1), "comment", 0);
        assertTextNode(rootElement.getChild(2), "\\n    ");
        Node child1 = rootElement.getChild(3);
        assertNode(child1, "pmd:child1", 1, "test", "1");
        assertTextNode(child1.getChild(0), "entity: &\\n    ");
        assertTextNode(rootElement.getChild(4), "\\n    ");
        Node child2 = rootElement.getChild(5);
        assertNode(child2, "pmd:child2", 3);
        assertTextNode(child2.getChild(0), "\\n      ");
        assertTextNode(child2.getChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.getChild(2), "\\n    ");
        assertTextNode(rootElement.getChild(6), "\\n");
    }

    /**
     * Verifies the default parsing behavior of the XML parser with namespaces
     * but not namespace aware.
     */
    @Test
    public void testParsingNotNamespaceAware() {
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setNamespaceAware(false);
        Node document = XML.withParserOptions(parserOptions).parse(XML_NAMESPACE_TEST);

        assertNode(document, "document", 1);
        Node rootElement = document.getChild(0);
        assertNode(rootElement, "pmd:rootElement", 7, "xmlns:pmd", "http://pmd.sf.net");
        Assert.assertNull(((XmlNode) rootElement).getNode().getNamespaceURI());
        Assert.assertNull(((XmlNode) rootElement).getNode().getPrefix());
        Assert.assertNull(((XmlNode) rootElement).getNode().getLocalName());
        Assert.assertEquals("pmd:rootElement", ((XmlNode) rootElement).getNode().getNodeName());
        assertTextNode(rootElement.getChild(0), "\\n    ");
        assertNode(rootElement.getChild(1), "comment", 0);
        assertTextNode(rootElement.getChild(2), "\\n    ");
        Node child1 = rootElement.getChild(3);
        assertNode(child1, "pmd:child1", 1, "test", "1");
        assertTextNode(child1.getChild(0), "entity: &\\n    ");
        assertTextNode(rootElement.getChild(4), "\\n    ");
        Node child2 = rootElement.getChild(5);
        assertNode(child2, "pmd:child2", 3);
        assertTextNode(child2.getChild(0), "\\n      ");
        assertTextNode(child2.getChild(1), " cdata section ", "cdata-section");
        assertTextNode(child2.getChild(2), "\\n    ");
        assertTextNode(rootElement.getChild(6), "\\n");
    }

    /**
     * Verifies the parsing behavior of the XML parser with validation on.
     *
     * @throws UnsupportedEncodingException
     *             error
     */
    @Test
    public void testParsingWithValidation() throws UnsupportedEncodingException {
        XmlParserOptions parserOptions = new XmlParserOptions();
        parserOptions.setValidating(true);
        PrintStream oldErr = System.err;
        Locale oldLocale = Locale.getDefault();
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            System.setErr(new PrintStream(bos));
            Locale.setDefault(Locale.ENGLISH);
            Node document = XML.withParserOptions(parserOptions).parse(XML_INVALID_WITH_DTD);
            Assert.assertNotNull(document);
            String output = bos.toString("UTF-8");
            Assert.assertTrue(output.contains("Element type \"invalidChild\" must be declared."));
            Assert.assertTrue(output.contains("The content of element type \"rootElement\" must match \"(child)\"."));
            Assert.assertEquals(2, document.getNumChildren());
            Assert.assertEquals("invalidChild", String.valueOf(document.getChild(1).getChild(1)));
        } finally {
            System.setErr(oldErr);
            Locale.setDefault(oldLocale);
        }
    }

    @Test
    public void testWithProcessingInstructions() {
        String xml = "<?xml version=\"1.0\"?><?mypi?><!DOCTYPE testDoc [<!ENTITY myentity \"e\">]><!--Comment--><foo abc=\"abc\"><bar>TEXT</bar><![CDATA[cdata!]]>&gt;&myentity;&lt;</foo>";
        LanguageVersionHandler xmlVersionHandler = LanguageRegistry.getLanguage(XmlLanguageModule.NAME)
                .getDefaultVersion().getLanguageVersionHandler();
        XmlParserOptions options = (XmlParserOptions) xmlVersionHandler.getDefaultParserOptions();
        options.setExpandEntityReferences(false);
        Parser parser = xmlVersionHandler.getParser(options);
        Node document = parser.parse(null, new StringReader(xml));
        Assert.assertNotNull(document);
        assertNode(document.getChild(0), "mypi", 0);
        assertLineNumbers(document.getChild(0), 1, 22, 1, 29);
    }

    @Test
    public void testBug1518() throws Exception {
        XML.parseResource("parsertests/bug1518.xml");
    }

    @Test
    public void testAutoclosingElementLength() {
        final String xml = "<elementName att1='foo' att2='bar' att3='other' />";
        assertLineNumbers(XML.parse(xml), 1, 1, 1, xml.length());
    }

    /**
     * Asserts a single node inclusive attributes.
     *
     * @param node
     *            the node
     * @param toString
     *            the to String representation to expect
     * @param childs
     *            number of childs
     * @param atts
     *            attributes - each object pair forms one attribute: first name,
     *            then value.
     */
    private void assertNode(Node node, String toString, int childs, Object... atts) {
        Assert.assertEquals(toString, String.valueOf(node));
        Assert.assertEquals(childs, node.getNumChildren());
        Iterator<Attribute> attributeIterator = ((XmlNode) node).getAttributeIterator();
        if (atts != null) {
            for (int i = 0; i < atts.length; i += 2) {
                Assert.assertTrue(attributeIterator.hasNext());
                String name = String.valueOf(atts[i]);
                Object value = atts[i + 1];
                Attribute attribute = attributeIterator.next();
                Assert.assertEquals(name, attribute.getName());
                Assert.assertEquals(value, attribute.getValue());
            }
        }
        Assert.assertFalse(attributeIterator.hasNext());
    }

    /**
     * Assert a single text node.
     *
     * @param node
     *            the node to check
     * @param text
     *            the text to expect
     */
    private void assertTextNode(Node node, String text) {
        assertTextNode(node, text, "text");
    }

    /**
     * Assert a single text node.
     *
     * @param node
     *            the node to check
     * @param text
     *            the text to expect
     * @param toString
     *            the to string representation
     */
    private void assertTextNode(Node node, String text, String toString) {
        Assert.assertEquals(toString, String.valueOf(node));
        Assert.assertEquals(0, node.getNumChildren());
        Assert.assertEquals(text, StringUtil.escapeWhitespace(node.getImage()));
        Iterator<Attribute> attributeIterator = ((XmlNode) node).getAttributeIterator();
        Assert.assertTrue(attributeIterator.hasNext());
        Attribute attribute = attributeIterator.next();
        Assert.assertEquals("Image", attribute.getName());
        Assert.assertEquals(text, StringUtil.escapeWhitespace(attribute.getValue()));
        Assert.assertFalse(attributeIterator.hasNext());
    }

    /**
     * Assert the line numbers of a node.
     *
     * @param node
     *            the node
     * @param beginLine
     *            the begin line
     * @param beginColumn
     *            the begin column
     * @param endLine
     *            the end line
     * @param endColumn
     *            the end column
     */
    private void assertLineNumbers(Node node, int beginLine, int beginColumn, int endLine, int endColumn) {
        Assert.assertEquals("begin line wrong", beginLine, node.getBeginLine());
        Assert.assertEquals("begin column wrong", beginColumn, node.getBeginColumn());
        Assert.assertEquals("end line wrong", endLine, node.getEndLine());
        Assert.assertEquals("end column wrong", endColumn, node.getEndColumn());
    }
}
