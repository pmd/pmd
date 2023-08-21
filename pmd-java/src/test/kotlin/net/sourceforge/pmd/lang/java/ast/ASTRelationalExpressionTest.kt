/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast


class ASTRelationalExpressionTest : ParserTestSpec({


    parserTest("Relational expressions operator") {

        inContext(ExpressionParsingCtx) {
            "b < 3" should parseAs {
                infixExpr(BinaryOp.LT) {
                    variableAccess("b")
                    number()
                }
            }


            "a <= 3" should parseAs {
                infixExpr(BinaryOp.LE) {
                    variableAccess("a")
                    number()
                }
            }

            "1 > b" should parseAs {
                infixExpr(BinaryOp.GT) {
                    number()
                    variableAccess("b")
                }
            }

            "1 >= 3" should parseAs {
                infixExpr(BinaryOp.GE) {
                    int(1)
                    int(3)
                }
            }
        }
    }

    parserTest("Relational expressions precedence") {
        inContext(ExpressionParsingCtx) {

            "1 < 3 instanceof Boolean" should parseAs {

                infixExpr(BinaryOp.INSTANCEOF) {
                    infixExpr(BinaryOp.LT) {
                        int(1)
                        int(3)
                    }

                    typeExpr {
                        classType("Boolean")
                    }
                }
            }

            "1 == 3 < 4" should parseAs {
                infixExpr(BinaryOp.EQ) {
                    int(1)

                    infixExpr(BinaryOp.LT) {
                        int(3)
                        int(4)
                    }
                }
            }

            "1 < 3 + 4 instanceof Boolean" should parseAs {

                infixExpr(BinaryOp.INSTANCEOF) {
                    infixExpr(BinaryOp.LT) {

                        int(1)

                        infixExpr(BinaryOp.ADD) {
                            int(3)
                            int(4)
                        }
                    }

                    typeExpr {
                        classType("Boolean")
                    }
                }
            }
        }
    }

})
