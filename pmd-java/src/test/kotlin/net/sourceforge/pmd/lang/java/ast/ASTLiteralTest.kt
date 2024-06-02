/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.since
import net.sourceforge.pmd.lang.java.ast.UnaryOp.UNARY_MINUS
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.*
import net.sourceforge.pmd.lang.test.ast.NodeSpec
import net.sourceforge.pmd.lang.test.ast.ValuedNodeSpec
import net.sourceforge.pmd.lang.test.ast.shouldBe
import net.sourceforge.pmd.lang.test.ast.shouldHaveText

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTLiteralTest : ParserTestSpec({
    parserTestContainer("String literal") {
        inContext(ExpressionParsingCtx) {
            "\"\"" should parseAs {
                stringLit("\"\"") {
                    it::getConstValue shouldBe ""
                    it shouldHaveText "\"\""
                }
            }

            "\"foo\"" should parseAs {
                stringLit("\"foo\"") {
                    it::getConstValue shouldBe "foo"
                    it shouldHaveText "\"foo\""
                }
            }

            "\"foo\\t\"" should parseAs {
                stringLit("\"foo\\t\"") {
                    it::getConstValue shouldBe "foo\t"
                }
            }

            "(\"foo\\t\")" should parseAs {
                stringLit("\"foo\\t\"") {
                    it::getConstValue shouldBe "foo\t"
                    it shouldHaveText "(\"foo\\t\")"
                    it::getParenthesisDepth shouldBe 1
                }
            }
        }
    }

    parserTestContainer("Text block literal", javaVersions = since(J15)) {
        val delim = "\"\"\""

        inContext(ExpressionParsingCtx) {
            suspend fun String.testTextBlock(contents: NodeSpec<ASTStringLiteral> = EmptyAssertions) {
                this should parseAs {
                    textBlock {
                        it.literalText.toString() shouldBe this@testTextBlock.trim()
                        contents()
                    }
                }
            }

            suspend fun String.testTextBlock() {
                this.testTextBlock(EmptyAssertions)
            }

            """
$delim
<html>
    <body>
        <p>Hello, world</p>
    </body>
</html>
$delim

            """.testTextBlock()

            """ 
   $delim
   winter$delim
            """.testTextBlock()

            """
   $delim
   winter
   $delim
            """.testTextBlock()

            """
   $delim
            Hi, "Alice"
       $delim
            """.testTextBlock()


            """
    $delim
    Hi,
     "Bob"
    $delim                
            """.testTextBlock()

            """
    $delim  
    "
    $delim
            """.testTextBlock()

            """
    $delim
    \\
    $delim
            """.testTextBlock()

            """
    $delim
    String text = \$delim
    A text block inside a text block
    \$delim;
    $delim
            """.testTextBlock()


            """
    $delim
    x$delim
            """.testTextBlock()

        }

    }

    parserTestContainer("Text block literal on non-JDK13 preview", javaVersions = Earliest.rangeTo(J14)) {
        val delim = "\"\"\""

        inContext(ExpressionParsingCtx) {
            """
$delim
<html>
    <body>
        <p>Hello, world</p>
    </body>
</html>
$delim

            """ shouldNot parse()

            """ 
   $delim
   winter$delim
            """ shouldNot parse()

        }
    }

    parserTestContainer("String literal escapes") {
        inContext(ExpressionParsingCtx) {
            "\"abc\u1234abc\"" should parseAs {
                stringLit("\"abc\u1234abc\"") {
                    it::getConstValue shouldBe "abc\u1234abc"
                }
            }

            "\"abc\\u1234abc\"" should parseAs {
                stringLit("\"abc\u1234abc\"") {
                    it::getConstValue shouldBe "abc\u1234abc"
                    it.originalText.toString() shouldBe "\"abc\\u1234abc\""
                }
            }

            "\"abcüabc\"" should parseAs {
                stringLit("\"abcüabc\"") {
                    it::getConstValue shouldBe "abcüabc"
                }
            }
        }
    }

    parserTestContainer("String literal octal escapes") {
        inContext(ExpressionParsingCtx) {
            // (kotlin doesn't have octal escapes)
            val char = "123".toInt(radix = 8).toChar()

            "\"\\123\"" should parseAs {
                stringLit("\"\\123\"") {
                    it::getConstValue shouldBe char.toString()
                }
            }
            val delim = "\"\"\""

            """
                $delim
                \123
                $delim
            """ should parseAs {
                textBlock {
                    it::getConstValue shouldBe char.toString() + "\n"
                }
            }
        }
    }

    parserTestContainer("Char literal") {
        inContext(ExpressionParsingCtx) {
            "'c'" should parseAs {
                charLit("'c'") {
                    it::getConstValue shouldBe 'c'
                }
            }

            "('c')" should parseAs {
                charLit("'c'") {
                    it::getConstValue shouldBe 'c'
                }
            }

            "'\t'" should parseAs {
                charLit("'\t'") {
                    it::getConstValue shouldBe '\t'
                }
            }

            "'\\t'" should parseAs {
                charLit("'\\t'") {
                    it::getConstValue shouldBe '\t'
                }
            }
        }
    }

    parserTestContainer("Boolean literals") {
        inContext(ExpressionParsingCtx) {
            "true" should parseAs {
                boolean(true)
            }

            "false" should parseAs {
                boolean(false)
            }
        }
    }

    parserTestContainer("Null literal") {
        inContext(ExpressionParsingCtx) {
            "null" should parseAs {
                nullLit()
            }
        }
    }

    parserTestContainer("Numeric literals") {
        inContext(ExpressionParsingCtx) {
            "12" should parseAs {
                number(INT) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsLong shouldBe 12L
                    it::getValueAsFloat shouldBe 12.0f
                    it::getValueAsDouble shouldBe 12.0
                    it.literalText.toString() shouldBe "12"
                }
            }

            "1___234" should parseAs {
                number(INT) {
                    it::getValueAsInt shouldBe 1234
                    it.literalText.toString() shouldBe "1___234"
                }
            }

            "0b0000_0010" should parseAs {
                number(INT) {
                    it::getValueAsInt shouldBe 2
                    it.literalText.toString() shouldBe "0b0000_0010"
                }
            }

            "1234_5678_9012_3456L" should parseAs {
                number(LONG) {
                    it::getValueAsLong shouldBe 1234_5678_9012_3456L
                }
            }

            "-0X0000_000f" should parseAs { // this is not a float, it's hex
                unaryExpr(UNARY_MINUS) {
                    number(INT) {
                        it.literalText.toString() shouldBe "0X0000_000f"
                        it::getValueAsInt shouldBe 15
                        it::getValueAsFloat shouldBe 15f
                        it::getValueAsDouble shouldBe 15.0
                    }
                }
            }

            "12l" should parseAs {
                number(LONG) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsLong shouldBe 12L
                    it::getValueAsFloat shouldBe 12.0f
                    it::getValueAsDouble shouldBe 12.0
                    it.literalText.toString() shouldBe "12l"
                }
            }

            "12L" should parseAs {
                number(LONG) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsLong shouldBe 12L
                    it.literalText.toString() shouldBe "12L"
                }
            }

            "12d" should parseAs {
                number(DOUBLE) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsFloat shouldBe 12.0f
                    it::getValueAsDouble shouldBe 12.0
                    it.literalText.toString() shouldBe "12d"
                }
            }

            "12f" should parseAs {
                number(FLOAT) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsFloat shouldBe 12.0f
                    it::getValueAsDouble shouldBe 12.0
                    it.literalText.toString() shouldBe "12f"
                }
            }

            "-3_456.12_3" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    number(DOUBLE) {
                        it::getValueAsInt shouldBe 3456
                        it::getValueAsFloat shouldBe 3456.123f
                        it::getValueAsDouble shouldBe 3456.123
                        it.literalText.toString() shouldBe "3_456.12_3"
                    }
                }
            }


            "0_" shouldNot parse()
            "0_0" should parseAs {
                number(INT) {
                    it::getBase shouldBe 8
                    it::getConstValue shouldBe 0
                }
            }

            "0__0" should parseAs {
                number(INT) {
                    it::getBase shouldBe 8
                    it::getConstValue shouldBe 0
                }
            }

            "0fl" shouldNot parse()
            "0dl" shouldNot parse()
            "0Dl" shouldNot parse()
            "0DL" shouldNot parse()
            "0fL" shouldNot parse()
            "0FL" shouldNot parse()

            // this starts with zero so is octal,
            // but 9 is too big for an octal digit
            "099" shouldNot parse()
            "00_8" shouldNot parse()
            "08_" shouldNot parse()
            "0x8_" shouldNot parse()
            "8_" shouldNot parse()
            "0b" shouldNot parse()
            "0x" shouldNot parse()
            "0" should parseAs {
                number(INT) {
                    it::getBase shouldBe 10 // by convention
                }
            }
            "0.5" should parseAs {
                number(DOUBLE) {
                    it::getBase shouldBe 10
                }
            }

            "0." should parseAs {
                number(DOUBLE) {
                    it::getBase shouldBe 10
                }
            }
            val doubleOrFloatInBase10: NodeSpec<*> = {
                number {
                    it::getBase shouldBe 10
                }
            }
            "05e10" should parseAs(doubleOrFloatInBase10)
            "05e10f" should parseAs(doubleOrFloatInBase10)
            "00f" should parseAs(doubleOrFloatInBase10)
            "00d" should parseAs(doubleOrFloatInBase10)
            "00D" should parseAs(doubleOrFloatInBase10)
            "050.0" should parseAs(doubleOrFloatInBase10)
        }
    }

    parserTestContainer("Hex floating point literals") {
        // the exponent is binary:
        // p1  multiplies by 2
        // p-1 divides by 2

        inContext(ExpressionParsingCtx) {
            val exp30f: NodeSpec<*> = {
                number(FLOAT) {
                    it::getValueAsDouble shouldBe 30.0
                    it::getValueAsFloat shouldBe 30f
                    it::getValueAsInt shouldBe 30
                    it::getBase shouldBe 16
                }
            }

            "0x0fp1f" should parseAs(exp30f)

            @Suppress("LocalVariableName")
            val exp7_5d: NodeSpec<*> = {
                number(DOUBLE) {
                    it::getValueAsDouble shouldBe 7.5
                    it::getValueAsFloat shouldBe 7.5f
                    it::getValueAsInt shouldBe 7
                    it::getBase shouldBe 16
                }
            }

            "0x0fp-1" should parseAs(exp7_5d)
            "0x0fp-1d" should parseAs(exp7_5d)

            // the L is forbidden, since it's floating point
            "0x0fp1l" shouldNot parse()
        }
    }

    parserTestContainer("Hex integral literals") {
        inContext(ExpressionParsingCtx) {
            fun hex15(type: PrimitiveTypeKind): ValuedNodeSpec<*, ASTNumericLiteral> = {
                number(type) {
                    it::getValueAsDouble shouldBe 15.0
                    it::getValueAsFloat shouldBe 15f
                    it::getValueAsInt shouldBe 15
                    it::getValueAsLong shouldBe 15L
                    it::getBase shouldBe 16
                }
            }

            val hex15l = hex15(LONG)
            val hex15i = hex15(INT)

            "0x0fl" should parseAs(hex15l)
            "0x0fL" should parseAs(hex15l)
            "0X0fl" should parseAs(hex15l)
            "0X0fL" should parseAs(hex15l)
            "0X0FL" should parseAs(hex15l)
            "0x0f" should parseAs(hex15i)
            "0x0F" should parseAs(hex15i)
            "0X0f" should parseAs(hex15i)
            "0X0F" should parseAs(hex15i)
            "0x0_0__0f" should parseAs(hex15i)
            "0x0_0__0F" should parseAs(hex15i)

            "-0X0000_000f" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    hex15i()
                }
            }
        }
    }

    parserTestContainer("Binary numeric literals - pre java1.7", javaVersions = Earliest..J1_6) {
        // binary literals were introduced in 1.7

        inContext(ExpressionParsingCtx) {
            "0b011" shouldNot parse()
            "0B011" shouldNot parse()
            "0B0_1__1" shouldNot parse()

            "0B0_1__1l" shouldNot parse()
            "0b0_11L" shouldNot parse()
        }
    }

    parserTestContainer("Binary numeric literals - java1.7+", javaVersions = J1_7..Latest) {
        fun binaryThree(type: PrimitiveTypeKind): NodeSpec<*> = {
            number(type) {
                it::getValueAsDouble shouldBe 3.0
                it::getValueAsFloat shouldBe 3f
                it::getValueAsInt shouldBe 3
                it::getValueAsLong shouldBe 3L
                it::getBase shouldBe 2
            }
        }

        inContext(ExpressionParsingCtx) {
            "0b011" should parseAs(binaryThree(INT))
            "0B011" should parseAs(binaryThree(INT))
            "0B0_1__1" should parseAs(binaryThree(INT))

            "0B0_1__1l" should parseAs(binaryThree(LONG))
            "0b0_11L" should parseAs(binaryThree(LONG))

            "0b05" shouldNot parse()
            "0b_1" shouldNot parse()
            "0b1_" shouldNot parse()
        }
    }
})
