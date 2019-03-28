/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.BinaryOp.*


class ASTMultiplicativeExpressionTest : ParserTestSpec({

    parserTest("Simple multiplicative expression should be flat") {

        "1 * 2 * 3" should matchExpr<ASTMultiplicativeExpression> {
            it::getOperator shouldBe "*"
            it::getOp shouldBe BinaryOp.MUL


            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 1
            }

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 2
            }

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 3
            }
        }


        "1 / 2 / -4" should matchExpr<ASTMultiplicativeExpression> {
            it::getOp shouldBe DIV
            it::getOperator shouldBe "/"


            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 1
            }

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 2
            }

            child<ASTUnaryExpression> {
                it::getOp shouldBe UnaryOp.UNARY_MINUS
                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 4
                }
            }
        }

        "1 % 2 % 3" should matchExpr<ASTMultiplicativeExpression> {
            it::getOp shouldBe MOD
            it::getOperator shouldBe "%"

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 1
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 2
            }

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 3
            }
        }
    }

    parserTest("Changing operators should push a new node") {

        "1 * 2 / 3 % 2" should matchExpr<ASTMultiplicativeExpression> {
            it::getOperator shouldBe "%"
            it::getOp shouldBe MOD

            child<ASTMultiplicativeExpression> {
                it::getOperator shouldBe "/"
                it::getOp shouldBe DIV

                child<ASTMultiplicativeExpression> {
                    it::getOperator shouldBe "*"
                    it::getOp shouldBe MUL

                    child<ASTNumericLiteral> {
                        it::getValueAsInt shouldBe 1
                    }

                    child<ASTNumericLiteral> {
                        it::getValueAsInt shouldBe 2
                    }
                }

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 3
                }
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 2
            }
        }
    }

})
