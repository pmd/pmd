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
}
