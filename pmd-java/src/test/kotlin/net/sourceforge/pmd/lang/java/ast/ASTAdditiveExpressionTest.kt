/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.BinaryOp.*


class ASTAdditiveExpressionTest : ParserTestSpec({

    parserTest("Simple additive expression should be flat") {

        inContext(ExpressionParsingCtx) {
            "1 + 2 + 3" should parseAs {
                infixExpr(ADD) {
                    infixExpr(ADD) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }

            "1 + 2 + 3 + 4 * 5" should parseAs {
                infixExpr(ADD) {
                    infixExpr(ADD) {
                        infixExpr(ADD) {
                            int(1)
                            int(2)
                        }
                        int(3)
                    }
                    infixExpr(MUL) {
                        int(4)
                        int(5)
                    }
                }
            }

            "1 + 2 + 3 * 4 + 5" should parseAs {
                infixExpr(ADD) {
                    infixExpr(ADD) {
                        infixExpr(ADD) {
                            int(1)
                            int(2)
                        }
                        infixExpr(MUL) {
                            int(3)
                            int(4)
                        }
                    }
                    int(5)
                }
            }

            "1 * 2 + 3 * 4 + 5" should parseAs {
                infixExpr(ADD) {
                    infixExpr(ADD) {
                        infixExpr(MUL) {
                            int(1)
                            int(2)
                        }
                        infixExpr(MUL) {
                            int(3)
                            int(4)
                        }
                    }
                    int(5)
                }
            }
        }
    }

    parserTest("Changing operators should push a new node") {
        inContext(ExpressionParsingCtx) {
            "1 + 2 - 3" should parseAs {
                infixExpr(SUB) {
                    infixExpr(ADD) {
                        int(1)
                        int(2)
                    }
                    int(3)
                }
            }

            "1 + 4 + 2 - 3" should parseAs {
                infixExpr(SUB) {
                    infixExpr(ADD) {
                        infixExpr(ADD) {
                            int(1)
                            int(4)
                        }
                        int(2)
                    }
                    int(3)
                }
            }

            // ((((1 + 4 + 2) - 3) + 4) - 1)
            "1 + 4 + 2 - 3 + 4 - 1" should parseAs {

                infixExpr(SUB) {
                    infixExpr(ADD) {
                        infixExpr(SUB) {
                            infixExpr(ADD) {
                                infixExpr(ADD) {
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
