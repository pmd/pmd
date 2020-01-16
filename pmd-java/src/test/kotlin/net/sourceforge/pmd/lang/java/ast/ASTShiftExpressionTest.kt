/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.BinaryOp.*

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
                shiftExpr(RIGHT_SHIFT) {
                    int(1)
                    int(2)
                }
            }

            "1 << 2 << 2" should parseAs {
                shiftExpr(LEFT_SHIFT) {
                    shiftExpr(LEFT_SHIFT) {
                        int(1)
                        int(2)
                    }
                    int(2)
                }
            }

            "1 >>> 2 >>> 3" should parseAs {
                shiftExpr(UNSIGNED_RIGHT_SHIFT) {
                    shiftExpr(UNSIGNED_RIGHT_SHIFT) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }

            // this is a corner case whereby < width > matches type arguments
            "i < width >> 1" should parseAs {
                compExpr(LT) {
                    variableAccess("i")
                    shiftExpr(RIGHT_SHIFT) {
                        variableAccess("width")
                        int(1)
                    }
                }
            }

            "1 >> 2 << 3" should parseAs {
                shiftExpr(LEFT_SHIFT) {

                    shiftExpr(RIGHT_SHIFT) {
                        int(1)
                        int(2)
                    }

                    int(3)
                }
            }

            "1 << 2 << 3 >> 4 >> 5" should parseAs {
                shiftExpr(RIGHT_SHIFT) {
                    shiftExpr(RIGHT_SHIFT) {
                        shiftExpr(LEFT_SHIFT) {
                            shiftExpr(LEFT_SHIFT) {
                                int(1)
                                int(2)
                            }
                            int(3)
                        }
                        int(4)
                    }
                    int(5)
                }
            }
        }
    }

    parserTest("Unary expression precedence") {
        inContext(ExpressionParsingCtx) {

            "2 >> 2 < 3" should parseAs {
                compExpr(LT) {
                    shiftExpr(RIGHT_SHIFT) {
                        int(2)
                        int(2)
                    }
                    int(3)
                }
            }

            "2 >> 2 + 3" should parseAs {
                shiftExpr(RIGHT_SHIFT) {
                    int(2)

                    additiveExpr(ADD) {
                        int(2)
                        int(3)
                    }
                }
            }
        }
    }
})
