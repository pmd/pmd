/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.NodeSpec
import net.sourceforge.pmd.lang.ast.test.ValuedNodeSpec
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.*
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Earliest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.UnaryOp.UNARY_MINUS

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTLiteralTest : ParserTestSpec({

    parserTest("String literal") {

        inContext(ExpressionParsingCtx) {

            "\"\"" should parseAs {
                stringLit("\"\"") {
                    it::isStringLiteral shouldBe true
                    it::getConstValue shouldBe ""
                }
            }

            "\"foo\"" should parseAs {
                stringLit("\"foo\"") {
                    it::getConstValue shouldBe "foo"
                }
            }

            "\"foo\\t\"" should parseAs {
                stringLit("\"foo\\t\"") {
                    it::getConstValue shouldBe "foo\t"
                }
            }
        }
    }

    parserTest("Text block literal", javaVersion = J13__PREVIEW) {

        val delim = "\"\"\""

        inContext(ExpressionParsingCtx) {


            suspend fun String.testTextBlock(contents: NodeSpec<ASTStringLiteral> = EmptyAssertions) {

                this should parseAs {

                    textBlock {
                        it::getImage shouldBe this@testTextBlock.trim()
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
            Hi, "Bob"
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

        }

    }


    parserTest("Text block literal on non-JDK13 preview", javaVersions = JavaVersion.except(J13__PREVIEW, J14__PREVIEW)) {

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



    parserTest("String literal escapes") {
        inContext(ExpressionParsingCtx) {

            "\"abc\u1234abc\"" should parseAs {
                stringLit("\"abc\u1234abc\"") {
                    it::getConstValue shouldBe "abc\u1234abc"
                }
            }

            "\"abc\\u1234abc\"" should parseAs {
                stringLit("\"abc\\u1234abc\"") {
                    it::getConstValue shouldBe "abc\u1234abc"
                }
            }

            "\"abcüabc\"" should parseAs {
                stringLit("\"abcüabc\"") {
                    it::getConstValue shouldBe "abcüabc"
                }
            }
        }
    }



    parserTest("Char literal") {
        inContext(ExpressionParsingCtx) {

            "'c'" should parseAs {
                charLit("'c'") {
                    it::isCharLiteral shouldBe true
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

    parserTest("Boolean literals") {
        inContext(ExpressionParsingCtx) {

            "true" should parseAs {
                boolean(true)
            }

            "false" should parseAs {
                boolean(false)
            }
        }
    }

    parserTest("Null literal") {
        inContext(ExpressionParsingCtx) {
            "null" should parseAs {
                nullLit()
            }
        }
    }

    parserTest("Numeric literals") {
        inContext(ExpressionParsingCtx) {

            "12" should parseAs {
                number(INT) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsLong shouldBe 12L
                    it::getValueAsFloat shouldBe 12.0f
                    it::getValueAsDouble shouldBe 12.0
                    it::getImage shouldBe "12"
                }
            }

            "1___234" should parseAs {
                number(INT) {
                    it::getValueAsInt shouldBe 1234
                    it::getImage shouldBe "1___234"
                }
            }

            "0b0000_0010" should parseAs {
                number(INT) {
                    it::getValueAsInt shouldBe 2
                    it::getImage shouldBe "0b0000_0010"

                }
            }

            "-0X0000_000f" should parseAs { // this is not a float, it's hex
                unaryExpr(UNARY_MINUS) {
                    number(INT) {
                        it::getImage shouldBe "0X0000_000f"
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
                    it::getImage shouldBe "12l"
                }
            }

            "12L" should parseAs {
                number(LONG) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsLong shouldBe 12L
                    it::getImage shouldBe "12L"

                }
            }

            "12d" should parseAs {
                number(DOUBLE) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsFloat shouldBe 12.0f
                    it::getValueAsDouble shouldBe 12.0
                    it::getImage shouldBe "12d"
                }
            }

            "12f" should parseAs {
                number(FLOAT) {
                    it::getValueAsInt shouldBe 12
                    it::getValueAsFloat shouldBe 12.0f
                    it::getValueAsDouble shouldBe 12.0
                    it::getImage shouldBe "12f"

                }
            }

            "-3_456.123_456" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    number(DOUBLE) {
                        it::getValueAsInt shouldBe 3456
                        it::getValueAsFloat shouldBe 3456.123456f
                        it::getValueAsDouble shouldBe 3456.123456
                        it::getImage shouldBe "3_456.123_456"
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
        }
    }

    parserTest("Hex floating point literals") {

        // the exponent is binary:
        // p1  multiplies by 2
        // p-1 divides by 2

        inContext(ExpressionParsingCtx) {

            val exp30f: NodeSpec<*> = {
                number(FLOAT) {
                    it::getValueAsDouble shouldBe 30.0
                    it::getValueAsFloat shouldBe 30f
                    it::getValueAsInt shouldBe 30
                }
            }


            "0x0fp1f" should parseAs(exp30f)

            @Suppress("LocalVariableName")
            val exp7_5d: NodeSpec<*> = {
                number(DOUBLE) {
                    it::getValueAsDouble shouldBe 7.5
                    it::getValueAsFloat shouldBe 7.5f
                    it::getValueAsInt shouldBe 7
                }
            }

            "0x0fp-1" should parseAs(exp7_5d)
            "0x0fp-1d" should parseAs(exp7_5d)

            // the L is forbidden, since it's floating point
            "0x0fp1l" shouldNot parse()
        }
    }

    parserTest("Hex integral literals") {


        inContext(ExpressionParsingCtx) {

            fun hex15(type: PrimitiveType): ValuedNodeSpec<*, ASTNumericLiteral> = {
                number(type) {
                    it::getValueAsDouble shouldBe 15.0
                    it::getValueAsFloat shouldBe 15f
                    it::getValueAsInt shouldBe 15
                    it::getValueAsLong shouldBe 15L
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

    parserTestGroup("Binary numeric literals") {

        onVersions(Earliest..J1_6) {
            // binary literals were introduced in 1.7

            inContext(ExpressionParsingCtx) {

                "0b011" shouldNot parse()
                "0B011" shouldNot parse()
                "0B0_1__1" shouldNot parse()

                "0B0_1__1l" shouldNot parse()
                "0b0_11L" shouldNot parse()

            }
        }

        onVersions(J1_7..Latest) {
            fun binaryThree(type: PrimitiveType): NodeSpec<*> = {
                number(type) {
                    it::getValueAsDouble shouldBe 3.0
                    it::getValueAsFloat shouldBe 3f
                    it::getValueAsInt shouldBe 3
                    it::getValueAsLong shouldBe 3L
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
    }

})
