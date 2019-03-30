package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTUnaryExpressionTest : ParserTestSpec({

    parserTest("Simple unary expressions") {

        "-2" should matchExpr<ASTUnaryExpression> {
            it::getOp shouldBe UnaryOp.UNARY_MINUS
            it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
        }

        "+2" should matchExpr<ASTUnaryExpression> {
            it::getOp shouldBe UnaryOp.UNARY_PLUS
            it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
        }

        "~2" should matchExpr<ASTUnaryExpression> {
            it::getOp shouldBe UnaryOp.BITWISE_INVERSE
            it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
        }

        "!true" should matchExpr<ASTUnaryExpression> {
            it::getOp shouldBe UnaryOp.BOOLEAN_NOT
            it::getBaseExpression shouldBe child<ASTBooleanLiteral> {}
        }
    }

    parserTest("Unary expression precedence") {

        "2 + -2" should matchExpr<ASTAdditiveExpression> {

            child<ASTNumericLiteral> {}
            child<ASTUnaryExpression> {
                it::getOp shouldBe UnaryOp.UNARY_MINUS
                it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
            }
        }

        "2 +-2" should matchExpr<ASTAdditiveExpression> {

            child<ASTNumericLiteral> {}
            child<ASTUnaryExpression> {
                it::getOp shouldBe UnaryOp.UNARY_MINUS
                it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
            }
        }

        "2 + +2" should matchExpr<ASTAdditiveExpression> {

            child<ASTNumericLiteral> {}
            child<ASTUnaryExpression> {
                it::getOp shouldBe UnaryOp.UNARY_PLUS
                it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
            }
        }

        "2 ++ 2" should notParseIn(ExpressionParsingCtx)
    }

    parserTest("Unary expression ambiguity corner cases") {

        "(p)+q" should matchExpr<ASTAdditiveExpression> {
            it::getOp shouldBe BinaryOp.ADD

            child<ASTParenthesizedExpression>(ignoreChildren = true) {}
            child<ASTVariableReference> {}
        }


        "(p)~q" should matchExpr<ASTCastExpression> {
            it::getCastType shouldBe child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "p"
            }

            it::getCastExpression shouldBe child<ASTUnaryExpression> {
                child<ASTVariableReference> {}
            }
        }

        "(p)!q" should matchExpr<ASTCastExpression> {
            it::getCastType shouldBe child<ASTClassOrInterfaceType> {
                it::getTypeImage shouldBe "p"
            }

            it::getCastExpression shouldBe child<ASTUnaryExpression> {
                child<ASTVariableReference> {}
            }
        }

        "(p)++" should matchExpr<ASTPostfixExpression> {

            child<ASTParenthesizedExpression> {
                child<ASTVariableReference> {

                }
            }
        }

        PrimitiveType
                .values()
                .filter { it.isNumeric }
                .map { it.token }
                .forEach { type ->

                    "($type)+q" should matchExpr<ASTCastExpression> {
                        it::getCastType shouldBe child<ASTPrimitiveType> {
                            it::getTypeImage shouldBe type
                        }

                        it::getCastExpression shouldBe child<ASTUnaryExpression> {
                            child<ASTVariableReference> {}
                        }
                    }

                    "($type)-q" should matchExpr<ASTCastExpression> {
                        it::getCastType shouldBe child<ASTPrimitiveType> {
                            it::getTypeImage shouldBe type
                        }

                        it::getCastExpression shouldBe child<ASTUnaryExpression> {
                            child<ASTVariableReference> {}
                        }
                    }

                    "($type)++q" should matchExpr<ASTCastExpression> {

                        it::getCastType shouldBe child<ASTPrimitiveType> {
                            it::getTypeImage shouldBe type
                        }

                        it::getCastExpression shouldBe child<ASTPreIncrementExpression> {
                            child<ASTVariableReference> {}
                        }
                    }
                }

    }


    parserTest("Unary expression is right-associative") {

        "!!true" should matchExpr<ASTUnaryExpression> {
            it::getOp shouldBe UnaryOp.BOOLEAN_NOT
            it::getBaseExpression shouldBe child<ASTUnaryExpression> {
                it::getOp shouldBe UnaryOp.BOOLEAN_NOT
                it::getBaseExpression shouldBe child<ASTBooleanLiteral> {}
            }
        }

        "~~1" should matchExpr<ASTUnaryExpression> {
            it::getOp shouldBe UnaryOp.BITWISE_INVERSE
            it::getBaseExpression shouldBe child<ASTUnaryExpression> {
                it::getOp shouldBe UnaryOp.BITWISE_INVERSE
                it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
            }
        }

        "-~1" should matchExpr<ASTUnaryExpression> {
            it::getOp shouldBe UnaryOp.UNARY_MINUS
            it::getBaseExpression shouldBe child<ASTUnaryExpression> {
                it::getOp shouldBe UnaryOp.BITWISE_INVERSE
                it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
            }
        }

        "-+-+1" should matchExpr<ASTUnaryExpression> {
            it::getOp shouldBe UnaryOp.UNARY_MINUS
            it::getBaseExpression shouldBe child<ASTUnaryExpression> {
                it::getOp shouldBe UnaryOp.UNARY_PLUS
                it::getBaseExpression shouldBe child<ASTUnaryExpression> {
                    it::getOp shouldBe UnaryOp.UNARY_MINUS
                    it::getBaseExpression shouldBe child<ASTUnaryExpression> {
                        it::getOp shouldBe UnaryOp.UNARY_PLUS
                        it::getBaseExpression shouldBe child<ASTNumericLiteral> {}
                    }
                }
            }
        }
    }
})
