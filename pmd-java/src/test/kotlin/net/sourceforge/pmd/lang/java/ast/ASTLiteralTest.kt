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

        "\"\"" should matchExpr<ASTStringLiteral> {
            it::isStringLiteral shouldBe true
            it::getConstValue shouldBe ""
            it::getImage shouldBe "\"\""
        }

        "\"foo\"" should matchExpr<ASTStringLiteral> {
            it::getConstValue shouldBe "foo"
            it::getImage shouldBe "\"foo\""
        }

        "\"foo\\t\"" should matchExpr<ASTStringLiteral> {
            it::getConstValue shouldBe "foo\t"
            it::getImage shouldBe "\"foo\\t\""
        }

    }

    parserTest("Text block literal", javaVersion = J13__PREVIEW) {

        val delim = "\"\"\""

        inContext(ExpressionParsingCtx) {


            fun String.testTextBlock(contents: NodeSpec<ASTStringLiteral> = EmptyAssertions) {

                this should parseAs {

                    textBlock {
                        it::getImage shouldBe this@testTextBlock.trim()
                        contents()
                    }
                }
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


    parserTest("Text block literal on non-JDK13 preview", javaVersions = !J13__PREVIEW) {

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
        "\"abc\u1234abc\"" should matchExpr<ASTStringLiteral> {
            it::getConstValue shouldBe "abc\u1234abc"
            it::getImage shouldBe "\"abc\u1234abc\""
        }

        "\"abc\\u1234abc\"" should matchExpr<ASTStringLiteral> {
            it::getConstValue shouldBe "abc\u1234abc"
            it::getImage shouldBe "\"abc\\u1234abc\""
        }
        "\"abcüabc\"" should matchExpr<ASTStringLiteral> {
            it::getConstValue shouldBe "abcüabc"
            it::getImage shouldBe "\"abcüabc\""
        }
    }



    parserTest("Char literal") {

        "'c'" should matchExpr<ASTCharLiteral> {
            it::isCharLiteral shouldBe true
            it::getUnescapedValue shouldBe 'c'
            it::getImage shouldBe "'c'"
        }

        "'\t'" should matchExpr<ASTCharLiteral> {
            it::getUnescapedValue shouldBe '\t'
            it::getImage shouldBe "'\t'"
        }

        "'\\t'" should matchExpr<ASTCharLiteral> {
            it::getUnescapedValue shouldBe '\t'
            it::getImage shouldBe "'\\t'"
        }
    }

    parserTest("Boolean literals") {

        "true" should matchExpr<ASTBooleanLiteral> {
            it::isBooleanLiteral shouldBe true
            it::isTrue shouldBe true
        }

        "false" should matchExpr<ASTBooleanLiteral> {
            it::isBooleanLiteral shouldBe true
            it::isTrue shouldBe false
        }
    }

    parserTest("Null literal") {

        "null" should matchExpr<ASTNullLiteral> {
            it::isBooleanLiteral shouldBe false
            it::isStringLiteral shouldBe false
            it::isNullLiteral shouldBe true
        }
    }

    parserTest("Numeric literals") {

        "12" should matchExpr<ASTNumericLiteral> {
            it::isStringLiteral shouldBe false
            it::isCharLiteral shouldBe false
            it::isNumericLiteral shouldBe true
            it::isIntLiteral shouldBe true
            it::getValueAsInt shouldBe 12
            it::getValueAsLong shouldBe 12L
            it::getValueAsFloat shouldBe 12.0f
            it::getValueAsDouble shouldBe 12.0
            it::getImage shouldBe "12"
        }

        "1___234" should matchExpr<ASTNumericLiteral> {
            it::isCharLiteral shouldBe false
            it::isNumericLiteral shouldBe true
            it::isIntLiteral shouldBe true
            it::getValueAsInt shouldBe 1234
            it::getImage shouldBe "1___234"
        }

        "0b0000_0010" should matchExpr<ASTNumericLiteral> {
            it::isCharLiteral shouldBe false
            it::isNumericLiteral shouldBe true
            it::isIntLiteral shouldBe true
            it::getValueAsInt shouldBe 2
            it::getImage shouldBe "0b0000_0010"
        }

        "-0X0000_000f" should matchExpr<ASTUnaryExpression> {
            it::getOperator shouldBe UNARY_MINUS
            it::getOperand shouldBe number(INT) {
                it::getImage shouldBe "0X0000_000f"
                it::getValueAsInt shouldBe 15
                it::getValueAsFloat shouldBe 15f
                it::getValueAsDouble shouldBe 15.0
            }
        }

        "12l" should matchExpr<ASTNumericLiteral> {
            it::isCharLiteral shouldBe false
            it::isNumericLiteral shouldBe true
            it::isIntLiteral shouldBe false
            it::isLongLiteral shouldBe true
            it::getValueAsInt shouldBe 12
            it::getValueAsLong shouldBe 12L
            it::getValueAsFloat shouldBe 12.0f
            it::getValueAsDouble shouldBe 12.0
            it::getImage shouldBe "12l"
        }

        "12L" should matchExpr<ASTNumericLiteral> {
            it::isLongLiteral shouldBe true
            it::getValueAsInt shouldBe 12
            it::getValueAsLong shouldBe 12L
            it::getImage shouldBe "12L"
        }

        "12d" should matchExpr<ASTNumericLiteral> {
            it::isIntLiteral shouldBe false
            it::isNumericLiteral shouldBe true
            it::isDoubleLiteral shouldBe true
            it::getValueAsInt shouldBe 12
            it::getValueAsFloat shouldBe 12.0f
            it::getValueAsDouble shouldBe 12.0
            it::getImage shouldBe "12d"
        }

        "12f" should matchExpr<ASTNumericLiteral> {
            it::isIntLiteral shouldBe false
            it::isDoubleLiteral shouldBe false
            it::isNumericLiteral shouldBe true
            it::isFloatLiteral shouldBe true
            it::getValueAsInt shouldBe 12
            it::getValueAsFloat shouldBe 12.0f
            it::getValueAsDouble shouldBe 12.0
            it::getImage shouldBe "12f"
        }

        "-3_456.123_456" should matchExpr<ASTUnaryExpression> {
            it::getOperator shouldBe UNARY_MINUS

            it::getOperand shouldBe number(DOUBLE) {
                it::getValueAsInt shouldBe 3456
                it::getValueAsFloat shouldBe 3456.123456f
                it::getValueAsDouble shouldBe 3456.123456
                it::getImage shouldBe "3_456.123_456"
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
