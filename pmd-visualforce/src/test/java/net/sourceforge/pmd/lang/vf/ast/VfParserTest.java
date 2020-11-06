/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * @author sergey.gorbaty
 */
public class VfParserTest extends AbstractVfNodesTest {

    @Test
    public void testSingleDoubleQuoteAndEL() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${!'yes'}</span>");
    }

    @Test
    public void testSingleDoubleQuoteAndELFunction() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${!method}</span>");
    }

    @Test
    public void testSingleDoubleQuote() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${\"yes\"}</span>");
    }

    @Test
    public void testAttributeNameWithDot() {
        vf.parse("<table-row keep-together.within-page=\"always\" >");
    }

    @Test
    public void testAttributeNameWithUnderscore() {
        vf.parse("<table-row test_attribute=\"always\" >");
    }

    @Test
    public void testAttributeNameWithColon() {
        vf.parse("<table-row test:attribute=\"always\" >");
    }

    @Test(expected = ParseException.class)
    public void testAttributeNameWithInvalidSymbol() {
        vf.parse("<table-row test&attribute=\"always\" >");
    }

    @Test(expected = ParseException.class)
    public void testAttributeNameWithInvalidDot() {
        vf.parse("<table-row .class=\"always\" >");
    }

    @Test(expected = ParseException.class)
    public void testAttributeNameWithInvalidDotV2() {
        vf.parse("<table-row test..attribute=\"always\" >");
    }

    @Test(expected = ParseException.class)
    public void testAttributeNameWithInvalidDotV3() {
        vf.parse("<table-row test.attribute.=\"always\" >");
    }

}
