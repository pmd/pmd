/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.xml.XmlParsingHelper;

public class XmlParserTest extends BaseTreeDumpTest {

    public XmlParserTest() {
        super(new RelevantAttributePrinter(), ".xml");
    }

    @Override
    public @NonNull BaseParsingHelper<?, ?> getParser() {
        return XmlParsingHelper.XML.withResourceContext(getClass(), "testdata");
    }

    /**
     * Verifies the default parsing behavior of the XML parser.
     */
    @Test
    public void testDefaultParsing() {
        doTest("sampleXml");
    }

    @Test
    public void testNamespaces() {
        doTest("sampleNs");
    }

    @Test
    public void testBug1518() {
        doTest("bug1518");
    }


    @Test
    public void dtdIsNotLookedUp() {
        // no exception should be thrown
        XmlParsingHelper.XML.parse(
            "<!DOCTYPE struts-config PUBLIC "
                + " \"-//Apache Software Foundation//DTD Struts Configuration 1.1//EN \" "
                + " \"http://jakarta.inexistinghost.org/struts/dtds/struts-config_1_1.dtd\" >"
                + "<struts-config/>");
    }

    @Test
    public void xsdIsNotLookedUp() {
        // no exception should be thrown
        XmlParsingHelper.XML.parse(
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?> "
                + "<web-app xmlns=\"http://java.sun.com/xml/ns/javaee\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://java.sun.com/xml/ns/javaee http://java.inexisting.com/xml/ns/javaee/web-app_2_5.xsd\" "
                + "version=\"2.5\">"
                + "</web-app>");
    }


}
