/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.CoordinatesPrinter;
import net.sourceforge.pmd.lang.xml.XmlParsingHelper;

public class XmlCoordinatesTest extends BaseTreeDumpTest {

    public XmlCoordinatesTest() {
        super(CoordinatesPrinter.INSTANCE, ".xml");
    }

    @Override
    public @NonNull BaseParsingHelper<?, ?> getParser() {
        return XmlParsingHelper.XML.withResourceContext(getClass(), "testdata");
    }

    /**
     * See bug #1054: XML Rules ever report a line -1 and not the line/column
     * where the error occurs
     */
    @Test
    public void testLineNumbers() {
        doTest("xmlCoords");
    }

    @Test
    public void testAutoclosingElementLength() {
        final String xml = "<elementName att1='foo' att2='bar' att3='other' />";
        assertLineNumbers(XmlParsingHelper.XML.parse(xml), 1, 1, 1, xml.length());
    }

    /**
     * Assert the line numbers of a node.
     *
     * @param node        the node
     * @param beginLine   the begin line
     * @param beginColumn the begin column
     * @param endLine     the end line
     * @param endColumn   the end column
     */
    private void assertLineNumbers(Node node, int beginLine, int beginColumn, int endLine, int endColumn) {
        Assert.assertEquals("begin line wrong", beginLine, node.getBeginLine());
        Assert.assertEquals("begin column wrong", beginColumn, node.getBeginColumn());
        Assert.assertEquals("end line wrong", endLine, node.getEndLine());
        Assert.assertEquals("end column wrong", endColumn, node.getEndColumn());
    }
}
