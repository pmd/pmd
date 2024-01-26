/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.Chars;

class ASTNumericLiteralTest {

    @Test
    void testParseLongBinary() {
        long literalLong = 0b0000000000000000000000000000000000000000100010001000010000010000L;
        long parsedLong = ASTNumericLiteral.parseIntegralValue(Chars.wrap("0b0000000000000000000000000000000000000000100010001000010000010000L"));
        assertEquals(literalLong, parsedLong); // in decimal: 8946704
    }

    @Test
    void testParseSmallInt() {
        int literalInt = 0x81;
        long parsedInt = ASTNumericLiteral.parseIntegralValue(Chars.wrap("0x81"));
        assertEquals((long) literalInt, parsedInt); // in decimal: 129
    }

    @Test
    void testParseLongBigNegativeBinary() {
        long literalLong = 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000_1000_1000_1000_0100_0001_0000L;
        long parsedLong = ASTNumericLiteral.parseIntegralValue(Chars.wrap("0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000_1000_1000_1000_0100_0001_0000L"));
        assertEquals(literalLong, parsedLong); // in decimal: -9223372036845829104L
    }
    
    @Test
    void malformedLiteral() {
        assertEquals(0L, ASTNumericLiteral.parseIntegralValue(Chars.wrap("0x1g")));
        assertEquals(0L, ASTNumericLiteral.parseIntegralValue(Chars.wrap("0x1_0000_0000_0000_0000L"))); // too big, 65bits
    }
}
