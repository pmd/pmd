/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTShiftExpressionTest : ParserTestSpec({

    parserTest("Simple shift expressions") {

        inContext(ExpressionParsingCtx) {

            "1 >> 2" should parseAs {
                shiftExpr(BinaryOp.RIGHT_SHIFT) {
                    int(1)
                    int(2)
                }
            }

            "1 << 2 << 2" should parseAs {
                shiftExpr(BinaryOp.LEFT_SHIFT) {
                    int(1)
                    int(2)
                    int(2)
                }
            }

            "1 >>> 2 >>> 3" should parseAs {
                shiftExpr(BinaryOp.UNSIGNED_RIGHT_SHIFT) {
                    int(1)
                    int(2)
                    int(3)
                }
            }

            // this is a corner case whereby < width > matches type arguments
            "i < width >> 1" should parseAs {
                compExpr(BinaryOp.LT) {
                    variableAccess("i")
                    shiftExpr(BinaryOp.RIGHT_SHIFT) {
                        variableAccess("width")
                        int(1)
                    }
                }
            }
        }
    }

    parserTest("Changing operators should push a new node") {
        inContext(ExpressionParsingCtx) {

            "1 >> 2 << 3" should parseAs {
                shiftExpr(BinaryOp.LEFT_SHIFT) {

                    shiftExpr(BinaryOp.RIGHT_SHIFT) {
                        int(1)
                        int(2)
                    }

                    int(3)
                }
            }

            "1 << 2 << 3 >> 4 >> 5" should parseAs {
                shiftExpr(BinaryOp.RIGHT_SHIFT) {

                    shiftExpr(BinaryOp.LEFT_SHIFT) {
                        int(1)
                        int(2)
                        int(3)
                    }

                    int(4)
                    int(5)
                }
            }
        }
    }

    parserTest("Unary expression precedence") {
        inContext(ExpressionParsingCtx) {

            "2 >> 2 < 3" should parseAs {
                compExpr(BinaryOp.LT) {
                    shiftExpr(BinaryOp.RIGHT_SHIFT) {
                        int(2)
                        int(2)
                    }
                    int(3)
                }
            }

            "2 >> 2 + 3" should parseAs {
                shiftExpr(BinaryOp.RIGHT_SHIFT) {
                    int(2)

                    additiveExpr(BinaryOp.ADD) {
                        int(2)
                        int(3)
                    }
                }
            }
        }
    }
})
