package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
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
                it::getBaseExpression shouldBe child<ASTBooleanLiteral> {}
            }
        }
    }
})
