/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;

/**
 * @author sergey.gorbaty
 */
class VfParserTest extends AbstractVfTest {

    @Test
    void testSingleDoubleQuoteAndEL() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${!'yes'}</span>");
    }

    @Test
    void testSingleDoubleQuoteAndELFunction() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${!method}</span>");
    }

    @Test
    void testSingleDoubleQuote() {
        vf.parse("<span escape='false' attrib=\"{!call}\">${\"yes\"}</span>");
    }

    @Test
    void testAttributeNameWithDot() {
        vf.parse("<table-row keep-together.within-page=\"always\" >");
    }

    @Test
    void testAttributeNameWithUnderscore() {
        vf.parse("<table-row test_attribute=\"always\" >");
    }

    @Test
    void testAttributeNameWithColon() {
        vf.parse("<table-row test:attribute=\"always\" >");
    }

    @Test
    void testAttributeNameWithInvalidSymbol() {
        assertThrows(ParseException.class, () -> vf.parse("<table-row test&attribute=\"always\" >"));
    }

    @Test
    void testAttributeNameWithInvalidDot() {
        assertThrows(ParseException.class, () -> vf.parse("<table-row .class=\"always\" >"));
    }

    @Test
    void testAttributeNameWithInvalidDotV2() {
        assertThrows(ParseException.class, () -> vf.parse("<table-row test..attribute=\"always\" >"));
    }

    @Test
    void testAttributeNameWithInvalidDotV3() {
        assertThrows(ParseException.class, () -> vf.parse("<table-row test.attribute.=\"always\" >"));
    }

}
