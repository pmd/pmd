/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.BinaryOp.*


class ASTEqualityExpressionTest : ParserTestSpec({

    parserTest("Test equality expressions") {

        inContext(ExpressionParsingCtx) {

            "1 == 2 == 3" should parseAs {
                infixExpr(EQ) {
                    infixExpr(EQ) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }

            "1 != 2 != 3 * 5" should parseAs {
                infixExpr(NE) {
                    infixExpr(NE) {
                        int(1)
                        int(2)
                    }

                    infixExpr(MUL) {
                        int(3)
                        int(5)
                    }
                }
            }

            "1 == 2 != 3" should parseAs {
                infixExpr(NE) {
                    infixExpr(EQ) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }

            "1 == 4 == 2 != 3" should parseAs {
                infixExpr(NE) {
                    infixExpr(EQ) {
                        infixExpr(EQ) {
                            int(1)
                            int(4)
                        }
                        int(2)
                    }
                    int(3)
                }
            }

            "1 == 4 == 2 != 3 == 4 != 1" should parseAs {
                infixExpr(NE) {
                    infixExpr(EQ) {
                        infixExpr(NE) {
                            infixExpr(EQ) {
                                infixExpr(EQ) {
                                    int(1)
                                    int(4)
                                }
                                int(2)
                            }
                            int(3)
                        }
                        int(4)
                    }
                    int(1)
                }
            }
        }
    }

})
