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
                infixExpr(RIGHT_SHIFT) {
                    int(1)
                    int(2)
                }
            }

            "1 << 2 << 2" should parseAs {
                infixExpr(LEFT_SHIFT) {
                    infixExpr(LEFT_SHIFT) {
                        int(1)
                        int(2)
                    }
                    int(2)
                }
            }

            "1 >>> 2 >>> 3" should parseAs {
                infixExpr(UNSIGNED_RIGHT_SHIFT) {
                    infixExpr(UNSIGNED_RIGHT_SHIFT) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }

            // this is a corner case whereby < width > matches type arguments
            "i < width >> 1" should parseAs {
                infixExpr(LT) {
                    variableAccess("i")
                    infixExpr(RIGHT_SHIFT) {
                        variableAccess("width")
                        int(1)
                    }
                }
            }

            "1 >> 2 << 3" should parseAs {
                infixExpr(LEFT_SHIFT) {

                    infixExpr(RIGHT_SHIFT) {
                        int(1)
                        int(2)
                    }

                    int(3)
                }
            }

            "1 << 2 << 3 >> 4 >> 5" should parseAs {
                infixExpr(RIGHT_SHIFT) {
                    infixExpr(RIGHT_SHIFT) {
                        infixExpr(LEFT_SHIFT) {
                            infixExpr(LEFT_SHIFT) {
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
                infixExpr(LT) {
                    infixExpr(RIGHT_SHIFT) {
                        int(2)
                        int(2)
                    }
                    int(3)
                }
            }

            "2 >> 2 + 3" should parseAs {
                infixExpr(RIGHT_SHIFT) {
                    int(2)

                    infixExpr(ADD) {
                        int(2)
                        int(3)
                    }
                }
            }
        }
    }
})
