/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.BinaryOp.*
import net.sourceforge.pmd.lang.java.ast.UnaryOp.UNARY_MINUS


class ASTMultiplicativeExpressionTest : ParserTestSpec({

    parserTest("Simple multiplicative expression should be flat") {

        inContext(ExpressionParsingCtx) {
            "1 * 2 * 3" should parseAs {
                infixExpr(MUL) {
                    infixExpr(MUL) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }

            "1 / 2 / -4" should parseAs {
                infixExpr(DIV) {
                    infixExpr(DIV) {
                        int(1)
                        int(2)
                    }
                    unaryExpr(UNARY_MINUS) {
                        int(4)
                    }
                }
            }

            "1 % 2 % 3" should parseAs {
                infixExpr(MOD) {
                    infixExpr(MOD) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }
        }
    }

    parserTest("Changing operators should push a new node") {
        inContext(ExpressionParsingCtx) {

            "1 * 2 / 3 % 2" should parseAs {
                infixExpr(MOD) {
                    infixExpr(DIV) {
                        infixExpr(MUL) {
                            int(1)
                            int(2)
                        }
                        int(3)
                    }
                    int(2)
                }
            }
        }
    }

})
