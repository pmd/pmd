/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.BinaryOp.*
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx
import net.sourceforge.pmd.lang.java.ast.UnaryOp.UNARY_MINUS


class ASTMultiplicativeExpressionTest : ParserTestSpec({

    parserTest("Simple multiplicative expression should be flat") {

        inContext(ExpressionParsingCtx) {
            "1 * 2 * 3" should parseAs {
                multiplicativeExpr(MUL) {
                    multiplicativeExpr(MUL) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }

            "1 / 2 / -4" should parseAs {
                multiplicativeExpr(DIV) {
                    multiplicativeExpr(DIV) {
                        int(1)
                        int(2)
                    }
                    unaryExpr(UNARY_MINUS) {
                        int(4)
                    }
                }
            }

            "1 % 2 % 3" should parseAs {
                multiplicativeExpr(MOD) {
                    multiplicativeExpr(MOD) {
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
                multiplicativeExpr(MOD) {
                    multiplicativeExpr(DIV) {
                        multiplicativeExpr(MUL) {
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
