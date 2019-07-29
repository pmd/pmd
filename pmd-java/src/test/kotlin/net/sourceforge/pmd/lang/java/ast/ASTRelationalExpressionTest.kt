/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx


class ASTRelationalExpressionTest : ParserTestSpec({


    parserTest("Relational expressions operator") {

        inContext(ExpressionParsingCtx) {
            "b < 3" should parseAs {
                compExpr(BinaryOp.LT) {
                    variableAccess("b")
                    number()
                }
            }


            "a <= 3" should parseAs {
                compExpr(BinaryOp.LE) {
                    variableAccess("a")
                    number()
                }
            }

            "1 > b" should parseAs {
                compExpr(BinaryOp.GT) {
                    number()
                    variableAccess("b")
                }
            }

            "1 >= 3" should parseAs {
                compExpr(BinaryOp.GE) {
                    int(1)
                    int(3)
                }
            }
        }
    }

    parserTest("Relational expressions precedence") {
        inContext(ExpressionParsingCtx) {

            "1 < 3 instanceof Boolean" should parseAs {

                instanceOfExpr {
                    compExpr(BinaryOp.LT) {
                        int(1)
                        int(3)
                    }

                    it::getTypeNode shouldBe classType("Boolean")
                }
            }

            "1 == 3 < 4" should parseAs {
                equalityExpr(BinaryOp.EQ) {
                    int(1)

                    compExpr(BinaryOp.LT) {
                        int(3)
                        int(4)
                    }
                }
            }

            "1 < 3 + 4 instanceof Boolean" should parseAs {

                instanceOfExpr {
                    compExpr(BinaryOp.LT) {

                        int(1)

                        additiveExpr(BinaryOp.ADD) {
                            int(3)
                            int(4)
                        }
                    }

                    it::getTypeNode shouldBe classType("Boolean")
                }
            }
        }
    }

})
