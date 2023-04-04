/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;


import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ConstantExpressionsTests extends BaseParserTest {

    private Executable isConst(String expr, Object value) {
        return () -> {
            ASTExpression e = parseExpr(expr);

            assertEquals(value, e.getConstValue(), "Constant value of '" + expr + "'");

        };
    }

    private Executable notConst(String expr) {
        return isConst(expr, null);
    }

    @Test
    void testUnaries() {
        assertAll(
            isConst("+1", 1),
            isConst("+1L", 1L),
            isConst("-1L", -1L),
            isConst("-1D", -1D),
            isConst("-4f", -4f),
            isConst("~1", ~1),
            isConst("+ 'c'", (int) 'c'),
            isConst("- 'c'", -(int) 'c'),
            isConst("! true", false),
            isConst("! false", true),
            isConst("!!! false", true),

            // those are type errors
            notConst("- true"),
            notConst("~ true"),
            notConst("~ 1.0"),
            notConst("+ \"\"")
        );
    }


    @Test
    void testCasts() {
        assertAll(
            isConst("+1", 1),
            isConst("(long) +1", 1L),
            isConst("(long) 'c'", (long) 'c'),
            isConst("(byte) 'c'", (byte) 'c'),
            isConst("(char) 1", (char) 1),
            isConst("(float) 1d", 1f),
            isConst("(double) 2f", 2d),
            isConst("(int) 2f", 2),
            isConst("(short) 2f", (short) 2f),
            isConst("(long) 2f", 2L),

            // those are type errors
            notConst("(Object) 1"),
            notConst("(String) null"),
            notConst("(char) null"),
            notConst("(int) new Integer(0)")
        );
    }


    @Test
    void testShortcutBoolean() {
        assertAll(
            isConst("true && true", true),
            isConst("true && false", false),
            isConst("false && true", false),
            isConst("false && false", false),

            isConst("true || true", true),
            isConst("true || false", true),
            isConst("false || true", true),
            isConst("false || false", false),

            isConst("false || (false ? false: true)", true),

            // those are type errors
            notConst("1 && true"),
            notConst("true && 1"),
            notConst("true && \"\""),
            notConst("\"\" && true"),

            notConst("1 || true"),
            notConst("true || 1"),
            notConst("true || \"\""),
            notConst("\"\" || true")
        );
    }


    @Test
    strictfp void testBitwise() {
        assertAll(

            isConst("~1 ^ ~8", ~1 ^ ~8),
            isConst("~1 ^ ~8L", ~1 ^ ~8L),
            isConst("1L ^ 0", 1L),
            isConst("true ^ true", false),
            isConst("true ^ false", true),
            isConst("false ^ true", true),
            isConst("false ^ false", false),

            isConst("1 & 1", 1),
            isConst("1 & 0L", 0L),
            isConst("1L & 0", 0L),
            isConst("true & true", true),
            isConst("true & false", false),
            isConst("false & true", false),
            isConst("false & false", false),

            isConst("1 | 1", 1),
            isConst("12L | 0", 12L),
            isConst("9L | 0", 9L),
            isConst("true | true", true),
            isConst("true | false", true),
            isConst("false | true", true),
            isConst("false | false", false),


            notConst("false | \"\""),
            notConst("1 | 1.0"),
            notConst("1 & 1.0f"),
            notConst("false | 0"),
            notConst("false ^ 0"),
            notConst("false & 0")

        );
    }

    @Test
    void testShifts() {
        assertAll(
            isConst("1 << 2", 1 << 2),
            isConst("1 << 2L", 1 << 2L),
            isConst("1L << 2L", 1L << 2L),
            isConst("1L << 2", 1L << 2),

            isConst("8 >> 2", 8 >> 2),
            isConst("8 >> 2L", 8 >> 2L),
            isConst("8L >> 2L", 8L >> 2L),
            isConst("8L >> 2", 8L >> 2),

            isConst("8 >>> 2", 8 >>> 2),
            isConst("8 >>> 2L", 8 >>> 2L),
            isConst("8L >>> 2L", 8L >>> 2L),
            isConst("8L >>> 2", 8L >>> 2),

            // those are type errors
            notConst("1 << 2d"),
            notConst("1 >> 2d"),
            notConst("1 >>> 2d"),
            notConst("1d << 2"),
            notConst("1d >> 2"),
            notConst("1d >>> 2")
        );
    }



    @Test
    void testAdditives() {
        assertAll(
            isConst("1 + 2", 3),
            isConst("1 + ~2", 1 + ~2),
            isConst("1 - 2", -1),
            isConst("1 - -2d", 3d),
            isConst("1 - 2 - 1f", -2f),
            isConst("1 - (2 - 1L)", 0L),

            isConst("1 + -2d", -1d),
            isConst("1 - (2 + 1f)", -2f),
            isConst("1 + (2 - 1L)", 2L),

            // concat
            isConst("1 + \"xx\"", "1xx"),
            isConst("\"xx\" + 1", "xx1"),
            isConst("1 + 2 + \"xx\"", "3xx"),
            isConst("1 + (2 + \"xx\")", "12xx"),
            isConst("1 + \"xx\" + 2", "1xx2"),
            isConst("1 + (\"xx\" + 2)", "1xx2"),

            // those are type errors
            notConst("1+true"),
            notConst("1-true"),
            notConst("1-\"\"")
        );
    }


    @Test
    strictfp void testMult() {
        assertAll(
            isConst("1 * 2.0", 1 * 2.0),
            isConst("1L * 2", 2L),
            isConst("1L * 2F", 1L * 2F),
            isConst("1 * 2F", 1 * 2F),
            isConst("1 * 2", 2),


            isConst("40 / 4", 40 / 4),
            isConst("40 / 4.0", 40 / 4.0),
            isConst("40 / 4L", 40 / 4L),
            isConst("40 / 4.0f", 40 / 4.0f),
            isConst("40 / 4.0f", 40 / 4.0f),

            isConst("40 % 4", 40 % 4),
            isConst("40 % 4.0", 40 % 4.0),
            isConst("40 % 4L", 40 % 4L),
            isConst("40 % 4.0f", 40 % 4.0f),
            isConst("40 % 4.0f", 40 % 4.0f),

            isConst("3.0f % 4.0d", 3.0 % 4.0d),

            // those are type errors
            notConst("1*true"),
            notConst("true*1"),

            notConst("true/1"),
            notConst("\"\"/1"),
            notConst("1/true"),
            notConst("1/\"\""),

            notConst("true%1"),
            notConst("\"\"%1"),
            notConst("1%true"),
            notConst("1%\"\"")
        );
    }

    @Test
    strictfp void testRelationals() {
        assertAll(
            isConst("1 <= 2L", true),
            isConst("2 <= 2d", true),
            isConst("'c' <= 6", false),
            isConst("4 <= 2d", false),
            isConst("4f <= 2", false),

            isConst("1 > 2L", false),
            isConst("2 > 2d", false),
            isConst("'c' > 6", true),
            isConst("4 > 2d", true),
            isConst("4f > 2", true),

            isConst("1 < 2L", true),
            isConst("2 < 2d", false),
            isConst("'c' < 6", false),
            isConst("4 < 2d", false),
            isConst("4f < 2", false),

            isConst("1 >= 2L", false),
            isConst("2 >= 2d", true),
            isConst("'c' >= 6", true),
            isConst("4 >= 2d", true),
            isConst("4f >= 2", true),


            notConst("\"\" instanceof String"),
            notConst("\"\" <= 3"),
            notConst("4 <= \"\""),
            notConst("4 < \"\""),
            notConst("\"\" < 3"),
            notConst("4 > \"\""),
            notConst("\"\" > 3"),
            notConst("4 >= \"\""),
            notConst("\"\" >= 3")
        );
    }

    @Test
    strictfp void testEquality() {
        assertAll(
            isConst("1 == 2", false),
            isConst("2 != 2d", false),
            isConst("'x' != (int) 'x'", false),
            isConst("'x' == (int) 'x'", true),


            notConst("\"\" == \"\""),
            notConst("\"\" != \"\""),
            notConst("2 != \"\"")
        );
    }

    @Test
    strictfp void testConditionals() {
        assertAll(
            isConst("true ? 1 + 2 : 4", 3),
            isConst("false ? 1 + 2 : 4", 4),
            isConst("1 == 2 ? 1 + 2 : 4", 4),
            isConst("1 < 2 ? 1 + 2 : 4", 3),

            notConst("false ? 1 + 2 : null"),
            notConst("false ? null : 5"),
            notConst("false ? (Integer) 1 + 2 : 5"),
            notConst("(Boolean) false ? 1 + 2 : 5")

        );
    }


    @Test
    strictfp void testConstFields() {
        assertAll(
            isConst("Math.PI", Math.PI),
            isConst("Math.PI > 3", true),

            notConst("System.out")
        );
    }


}
