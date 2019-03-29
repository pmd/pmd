/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTShiftExpressionTest : ParserTestSpec({

    parserTest("Simple shift expressions") {

        "1 >> 2" should matchExpr<ASTShiftExpression> {
            it::getOp shouldBe BinaryOp.RIGHT_SHIFT
            child<ASTNumericLiteral> {}
            child<ASTNumericLiteral> {}
        }

        "1 << 2 << 2" should matchExpr<ASTShiftExpression> {
            it::getOp shouldBe BinaryOp.LEFT_SHIFT
            child<ASTNumericLiteral> {}
            child<ASTNumericLiteral> {}
            child<ASTNumericLiteral> {}
        }

        "1 >>> 2 >>> 3" should matchExpr<ASTShiftExpression> {
            it::getOp shouldBe BinaryOp.UNSIGNED_RIGHT_SHIFT
            child<ASTNumericLiteral> {}
            child<ASTNumericLiteral> {}
            child<ASTNumericLiteral> {}
        }

    }

    parserTest("Changing operators should push a new node") {

        "1 >> 2 << 3" should matchExpr<ASTShiftExpression> {
            it::getOp shouldBe BinaryOp.LEFT_SHIFT

            child<ASTShiftExpression> {
                it::getOp shouldBe BinaryOp.RIGHT_SHIFT
                child<ASTNumericLiteral> {}
                child<ASTNumericLiteral> {}
            }

            child<ASTNumericLiteral> {}
        }

        "1 << 2 << 3 >> 4 >> 5" should matchExpr<ASTShiftExpression> {
            it::getOp shouldBe BinaryOp.RIGHT_SHIFT

            child<ASTShiftExpression> {
                it::getOp shouldBe BinaryOp.LEFT_SHIFT
                child<ASTNumericLiteral> {}
                child<ASTNumericLiteral> {}
                child<ASTNumericLiteral> {}
            }

            child<ASTNumericLiteral> {}
            child<ASTNumericLiteral> {}
        }
    }

    parserTest("Unary expression precedence") {

        "2 >> 2 < 3" should matchExpr<ASTRelationalExpression> {

            child<ASTShiftExpression> {
                it::getOp shouldBe BinaryOp.RIGHT_SHIFT
                child<ASTNumericLiteral> {}
                child<ASTNumericLiteral> {}
            }
            child<ASTNumericLiteral> {}
        }

        "2 >> 2 + 3" should matchExpr<ASTShiftExpression> {
            child<ASTNumericLiteral> {}

            child<ASTAdditiveExpression> {
                it::getOp shouldBe BinaryOp.ADD
                child<ASTNumericLiteral> {}
                child<ASTNumericLiteral> {}
            }
        }
    }
})
