package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTLiteralTest : ParserTestSpec({

    parserTest("String literal") {

        "\"\"" should matchExpr<ASTStringLiteral> {
            it::isStringLiteral shouldBe true
            it::getUnescapedValue shouldBe ""
            it::getImage shouldBe "\"\""
        }

        "\"foo\"" should matchExpr<ASTStringLiteral> {
            it::getUnescapedValue shouldBe "foo"
            it::getImage shouldBe "\"foo\""
        }

        "\"foo\\t\"" should matchExpr<ASTStringLiteral> {
            it::getUnescapedValue shouldBe "foo\t"
            it::getImage shouldBe "\"foo\\t\""
        }

    }


    parserTest("String literal escapes") {
        "\"abc\u1234abc\"" should matchExpr<ASTStringLiteral> {
            it::getUnescapedValue shouldBe "abc\u1234abc"
            it::getImage shouldBe "\"abc\u1234abc\""
        }

        "\"abc\\u1234abc\"" should matchExpr<ASTStringLiteral> {
            it::getUnescapedValue shouldBe "abc\u1234abc"
            it::getImage shouldBe "\"abc\\u1234abc\""
        }
        "\"abcüabc\"" should matchExpr<ASTStringLiteral> {
            it::getUnescapedValue shouldBe "abcüabc"
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

    parserTest("Class literals") {

        "void.class" should matchExpr<ASTClassLiteral> {
            it::isClassLiteral shouldBe true
            it::isVoid shouldBe true
            it.typeNode shouldBe null
        }

        "Integer.class" should matchExpr<ASTClassLiteral> {
            it::isClassLiteral shouldBe true
            it::isVoid shouldBe false

            it.typeNode shouldBe child<ASTClassOrInterfaceType> {}
        }

        "int.class" should matchExpr<ASTClassLiteral> {
            it::isVoid shouldBe false

            it.typeNode shouldBe child<ASTPrimitiveType> {}
        }

        "int[].class" should matchExpr<ASTClassLiteral> {
            it::isVoid shouldBe false

            it.typeNode shouldBe child<ASTArrayType> {
                it.elementType shouldBe child<ASTPrimitiveType> {}
                it.dimensions shouldBe child {
                    it.size shouldBe 1

                    child<ASTArrayTypeDim> {}
                }
            }
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
            it::getOp shouldBe UnaryOp.UNARY_MINUS
            it::getBaseExpression shouldBe child<ASTNumericLiteral> {
                it::isCharLiteral shouldBe false
                it::isNumericLiteral shouldBe true
                it::isIntLiteral shouldBe true
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
            it::getOp shouldBe UnaryOp.UNARY_MINUS

            it::getBaseExpression shouldBe child<ASTNumericLiteral> {
                it::isIntLiteral shouldBe false
                it::isDoubleLiteral shouldBe true
                it::isNumericLiteral shouldBe true
                it::isFloatLiteral shouldBe false
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

        "0x0fp1f" should matchExpr<ASTNumericLiteral> {
            it::isIntLiteral shouldBe false
            it::isDoubleLiteral shouldBe false
            it::isNumericLiteral shouldBe true
            it::isLongLiteral shouldBe false
            it::isFloatLiteral shouldBe true

            it::getValueAsDouble shouldBe 30.0
            it::getValueAsFloat shouldBe 30f
            it::getValueAsInt shouldBe 30
        }

        "0x0fp-1" should matchExpr<ASTNumericLiteral> {
            it::isIntLiteral shouldBe false
            it::isDoubleLiteral shouldBe true
            it::isNumericLiteral shouldBe true
            it::isLongLiteral shouldBe false
            it::isFloatLiteral shouldBe false

            it::getValueAsDouble shouldBe 7.5
            it::getValueAsFloat shouldBe 7.5f
            it::getValueAsInt shouldBe 7
        }

    }

})
