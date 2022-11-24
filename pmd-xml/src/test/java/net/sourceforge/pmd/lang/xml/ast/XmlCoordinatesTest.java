/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.CoordinatesPrinter;
import net.sourceforge.pmd.lang.ast.test.TestUtilsKt;
import net.sourceforge.pmd.lang.xml.XmlParsingHelper;

class XmlCoordinatesTest extends BaseTreeDumpTest {

    XmlCoordinatesTest() {
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
    void testLineNumbers() {
        doTest("xmlCoords");
    }

    @Test
    void testAutoclosingElementLength() {
        final String xml = "<elementName att1='foo' att2='bar' att3='other' />";
        TestUtilsKt.assertPosition(XmlParsingHelper.XML.parse(xml), 1, 1, 1, xml.length());
    }

}
