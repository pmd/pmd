/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD
import net.sourceforge.pmd.lang.java.ast.BinaryOp.SUB


class ASTAdditiveExpressionTest : ParserTestSpec({

    parserTest("Simple additive expression should be flat") {

        "1 + 2 + 3" should matchExpr<ASTAdditiveExpression> {
            it::getOperator shouldBe "+"


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


        "1 + 2 + 3 + 4 * 5" should matchExpr<ASTAdditiveExpression> {
            it::getOp shouldBe ADD
            it::getOperator shouldBe "+"

            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 1
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 2
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 3
            }
            child<ASTMultiplicativeExpression> {
                it::getOperator shouldBe "*"

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 4
                }
                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 5
                }
            }
        }

        "1 + 2 + 3 * 4 + 5" should matchExpr<ASTAdditiveExpression> {
            it::getOp shouldBe ADD
            it::getOperator shouldBe "+"

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
                    it::getValueAsInt shouldBe 4
                }
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 5
            }
        }

        "1 * 2 + 3 * 4 + 5" should matchExpr<ASTAdditiveExpression> {
            it::getOp shouldBe ADD
            it::getOperator shouldBe "+"

            child<ASTMultiplicativeExpression> {
                it::getOperator shouldBe "*"

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 1
                }
                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 2
                }
            }
            child<ASTMultiplicativeExpression> {
                it::getOperator shouldBe "*"

                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 3
                }
                child<ASTNumericLiteral> {
                    it::getValueAsInt shouldBe 4
                }
            }
            child<ASTNumericLiteral> {
                it::getValueAsInt shouldBe 5
            }
        }
    }

    parserTest("Changing operators should push a new node") {

        "1 + 2 - 3" should matchExpr<ASTAdditiveExpression> {
            it::getOperator shouldBe "-"

            child<ASTAdditiveExpression> {
                it::getOperator shouldBe "+"

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

        "1 + 4 + 2 - 3" should matchExpr<ASTAdditiveExpression> {
            it::getOperator shouldBe "-"

            child<ASTAdditiveExpression> {
                it::getOperator shouldBe "+"

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
        "1 + 4 + 2 - 3 + 4 - 1" should matchExpr<ASTAdditiveExpression> {
            it::getOp shouldBe SUB
            it::getOperator shouldBe "-"

            child<ASTAdditiveExpression> {
                it::getOp shouldBe ADD
                it::getOperator shouldBe "+"

                child<ASTAdditiveExpression> {
                    it::getOp shouldBe SUB
                    it::getOperator shouldBe "-"

                    child<ASTAdditiveExpression> {
                        it::getOp shouldBe ADD
                        it::getOperator shouldBe "+"

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

    // TODO

})
