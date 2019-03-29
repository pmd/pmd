/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.BinaryOp.*


class ASTEqualityExpressionTest : ParserTestSpec({

    parserTest("Simple equality expression should be flat") {

        "1 == 2 == 3" should matchExpr<ASTEqualityExpression> {
            it::getOp shouldBe EQ
            it::getOperator shouldBe "=="


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


        "1 != 2 != 3 * 5" should matchExpr<ASTEqualityExpression> {
            it::getOp shouldBe NE
            it::getOperator shouldBe "!="

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 1
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 2
            }
            child<ASTMultiplicativeExpression> {
                it::getOperator shouldBe "*"

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 3
                }
                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 5
                }
            }
        }
    }

    parserTest("Changing operators should push a new node") {

        "1 == 2 != 3" should matchExpr<ASTEqualityExpression> {
            it::getOp shouldBe NE

            child<ASTEqualityExpression> {
                it::getOp shouldBe EQ

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

        "1 == 4 == 2 != 3" should matchExpr<ASTEqualityExpression> {
            it::getOp shouldBe NE

            child<ASTEqualityExpression> {
                it::getOp shouldBe EQ

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 1
                }

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 4
                }

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 2
                }
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 3
            }
        }

        // ((((1 + 4 + 2) - 3) + 4) - 1)
        "1 == 4 == 2 != 3 == 4 != 1" should matchExpr<ASTEqualityExpression> {
            it::getOp shouldBe NE

            child<ASTEqualityExpression> {
                it::getOp shouldBe EQ

                child<ASTEqualityExpression> {
                    it::getOp shouldBe NE

                    child<ASTEqualityExpression> {
                        it::getOp shouldBe EQ

                        child<ASTNumericLiteral> {
                            it::getValueAsInt shouldBe 1
                        }
                        child<ASTNumericLiteral> {
                            it::getValueAsInt shouldBe 4
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
                    it::getValueAsInt shouldBe 4
                }
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 1
            }
        }
    }

})
