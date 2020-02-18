/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml;

import static net.sourceforge.pmd.lang.ParserOptionsTest.verifyOptionsEqualsHashcode;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.xml.rule.AbstractXmlRule;
import net.sourceforge.pmd.properties.BooleanProperty;

public class XmlParserOptionsTest {

    @Test
    public void testDefaults() {
        XmlParserOptions options = new XmlParserOptions();
        assertFalse(options.isCoalescing());
        assertTrue(options.isExpandEntityReferences());
        assertFalse(options.isIgnoringComments());
        assertFalse(options.isIgnoringElementContentWhitespace());
        assertTrue(options.isNamespaceAware());
        assertFalse(options.isValidating());
        assertFalse(options.isXincludeAware());

        MyRule rule = new MyRule();
        options = (XmlParserOptions) rule.getParserOptions();
        assertFalse(options.isCoalescing());
        assertTrue(options.isExpandEntityReferences());
        assertFalse(options.isIgnoringComments());
        assertFalse(options.isIgnoringElementContentWhitespace());
        assertTrue(options.isNamespaceAware());
        assertFalse(options.isValidating());
        assertFalse(options.isXincludeAware());
    }

    @Test
    public void testConstructor() {
        MyRule rule = new MyRule();

        rule.setProperty(XmlParserOptions.COALESCING_DESCRIPTOR, true);
        assertTrue(((XmlParserOptions) rule.getParserOptions()).isCoalescing());
        rule.setProperty(XmlParserOptions.COALESCING_DESCRIPTOR, false);
        assertFalse(((XmlParserOptions) rule.getParserOptions()).isCoalescing());

        rule.setProperty(XmlParserOptions.EXPAND_ENTITY_REFERENCES_DESCRIPTOR, true);
        assertTrue(((XmlParserOptions) rule.getParserOptions()).isExpandEntityReferences());
        rule.setProperty(XmlParserOptions.EXPAND_ENTITY_REFERENCES_DESCRIPTOR, false);
        assertFalse(((XmlParserOptions) rule.getParserOptions()).isExpandEntityReferences());

        rule.setProperty(XmlParserOptions.IGNORING_COMMENTS_DESCRIPTOR, true);
        assertTrue(((XmlParserOptions) rule.getParserOptions()).isIgnoringComments());
        rule.setProperty(XmlParserOptions.IGNORING_COMMENTS_DESCRIPTOR, false);
        assertFalse(((XmlParserOptions) rule.getParserOptions()).isIgnoringComments());

        rule.setProperty(XmlParserOptions.IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR, true);
        assertTrue(((XmlParserOptions) rule.getParserOptions()).isIgnoringElementContentWhitespace());
        rule.setProperty(XmlParserOptions.IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR, false);
        assertFalse(((XmlParserOptions) rule.getParserOptions()).isIgnoringElementContentWhitespace());

        rule.setProperty(XmlParserOptions.NAMESPACE_AWARE_DESCRIPTOR, true);
        assertTrue(((XmlParserOptions) rule.getParserOptions()).isNamespaceAware());
        rule.setProperty(XmlParserOptions.NAMESPACE_AWARE_DESCRIPTOR, false);
        assertFalse(((XmlParserOptions) rule.getParserOptions()).isNamespaceAware());

        rule.setProperty(XmlParserOptions.VALIDATING_DESCRIPTOR, true);
        assertTrue(((XmlParserOptions) rule.getParserOptions()).isValidating());
        rule.setProperty(XmlParserOptions.VALIDATING_DESCRIPTOR, false);
        assertFalse(((XmlParserOptions) rule.getParserOptions()).isValidating());

        rule.setProperty(XmlParserOptions.XINCLUDE_AWARE_DESCRIPTOR, true);
        assertTrue(((XmlParserOptions) rule.getParserOptions()).isXincludeAware());
        rule.setProperty(XmlParserOptions.XINCLUDE_AWARE_DESCRIPTOR, false);
        assertFalse(((XmlParserOptions) rule.getParserOptions()).isXincludeAware());
    }

    @Test
    public void testSetters() {
        XmlParserOptions options = new XmlParserOptions();

        options.setSuppressMarker("foo");
        assertEquals("foo", options.getSuppressMarker());
        options.setSuppressMarker(null);
        assertNull(options.getSuppressMarker());

        options.setCoalescing(true);
        assertTrue(options.isCoalescing());
        options.setCoalescing(false);
        assertFalse(options.isCoalescing());

        options.setExpandEntityReferences(true);
        assertTrue(options.isExpandEntityReferences());
        options.setExpandEntityReferences(false);
        assertFalse(options.isExpandEntityReferences());

        options.setIgnoringComments(true);
        assertTrue(options.isIgnoringComments());
        options.setIgnoringComments(false);
        assertFalse(options.isIgnoringComments());

        options.setIgnoringElementContentWhitespace(true);
        assertTrue(options.isIgnoringElementContentWhitespace());
        options.setIgnoringElementContentWhitespace(false);
        assertFalse(options.isIgnoringElementContentWhitespace());

        options.setNamespaceAware(true);
        assertTrue(options.isNamespaceAware());
        options.setNamespaceAware(false);
        assertFalse(options.isNamespaceAware());

        options.setValidating(true);
        assertTrue(options.isValidating());
        options.setValidating(false);
        assertFalse(options.isValidating());

        options.setXincludeAware(true);
        assertTrue(options.isXincludeAware());
        options.setXincludeAware(false);
        assertFalse(options.isXincludeAware());
    }

    @Test
    public void testEqualsHashcode() throws Exception {
        BooleanProperty[] properties = new BooleanProperty[] { XmlParserOptions.COALESCING_DESCRIPTOR,
            XmlParserOptions.EXPAND_ENTITY_REFERENCES_DESCRIPTOR, XmlParserOptions.IGNORING_COMMENTS_DESCRIPTOR,
            XmlParserOptions.IGNORING_ELEMENT_CONTENT_WHITESPACE_DESCRIPTOR,
            XmlParserOptions.NAMESPACE_AWARE_DESCRIPTOR, XmlParserOptions.VALIDATING_DESCRIPTOR,
            XmlParserOptions.XINCLUDE_AWARE_DESCRIPTOR, };

        for (int i = 0; i < properties.length; i++) {
            BooleanProperty property = properties[i];

            MyRule rule = new MyRule();
            rule.setProperty(property, true);
            ParserOptions options1 = rule.getParserOptions();
            rule.setProperty(property, false);
            ParserOptions options2 = rule.getParserOptions();
            rule.setProperty(property, true);
            ParserOptions options3 = rule.getParserOptions();
            rule.setProperty(property, false);
            ParserOptions options4 = rule.getParserOptions();
            verifyOptionsEqualsHashcode(options1, options2, options3, options4);
        }

        XmlParserOptions options1 = new XmlParserOptions();
        options1.setSuppressMarker("foo");
        XmlParserOptions options2 = new XmlParserOptions();
        options2.setSuppressMarker("bar");
        XmlParserOptions options3 = new XmlParserOptions();
        options3.setSuppressMarker("foo");
        XmlParserOptions options4 = new XmlParserOptions();
        options4.setSuppressMarker("bar");
        verifyOptionsEqualsHashcode(options1, options2, options3, options4);
    }

    private static final class MyRule extends AbstractXmlRule {
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(XmlParserOptionsTest.class);
    }
}
