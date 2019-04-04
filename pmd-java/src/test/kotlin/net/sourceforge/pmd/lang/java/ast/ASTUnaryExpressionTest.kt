package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType
import net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx
import net.sourceforge.pmd.lang.java.ast.UnaryOp.*

/**
 * Nodes that previously corresponded to ASTAllocationExpression.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
class ASTUnaryExpressionTest : ParserTestSpec({

    parserTest("Simple unary expressions") {

        inContext(ExpressionParsingCtx) {
            "-2" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    number()
                }
            }

            "-2" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    number()
                }
            }

            "-2" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    number()
                }
            }

            "-2" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    number()
                }
            }
        }
    }

    parserTest("Unary expression precedence") {

        inContext(ExpressionParsingCtx) {
            "2 + -2" should parseAs {
                additiveExpr(ADD) {
                    number()
                    unaryExpr(UNARY_MINUS) {
                        number()
                    }
                }
            }

            "2 +-2" should parseAs {
                additiveExpr(ADD) {
                    number()
                    unaryExpr(UNARY_MINUS) {
                        number()
                    }
                }
            }

            "2 + +2" should parseAs {
                additiveExpr(ADD) {
                    number()
                    unaryExpr(UNARY_PLUS) {
                        number()
                    }
                }
            }

            "2 ++ 2" shouldNot parse()
            "2 -- 2" shouldNot parse()
        }
    }

    parserTest("Unary expression ambiguity corner cases") {

        inContext(ExpressionParsingCtx) {
            "(p)+q" should parseAs {
                additiveExpr(ADD) {
                    parenthesized {
                        variableRef("p")
                    }
                    variableRef("q")
                }
            }


            "(p)~q" should parseAs {
                castExpr {
                    classType("p")

                    unaryExpr(BITWISE_INVERSE) {
                        variableRef("q")
                    }
                }
            }

            "(p)!q" should parseAs {
                castExpr {
                    classType("p")

                    unaryExpr(BOOLEAN_NOT) {
                        variableRef("q")
                    }
                }
            }

            "(p)++" should parseAs {
                postfixExpr(INCREMENT) {
                    parenthesized {
                        variableRef("p")
                    }
                }
            }

            "i+++i" should parseAs {
                additiveExpr(ADD) {
                    postfixExpr(INCREMENT) {
                        variableRef("i")
                    }
                    variableRef("i")
                }
            }

            // "++i++" doesn't compile so don't test it


            PrimitiveType
                    .values()
                    .filter { it.isNumeric }
                    .forEach { type ->

                        "($type)+q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                unaryExpr(UNARY_PLUS) {
                                    variableRef("q")
                                }
                            }
                        }

                        "($type)-q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                unaryExpr(UNARY_MINUS) {
                                    variableRef("q")
                                }
                            }
                        }

                        "($type)++q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                unaryExpr(INCREMENT) {
                                    variableRef("q")
                                }
                            }
                        }
                    }
        }
    }


    parserTest("Unary expression is right-associative") {

        inContext(ExpressionParsingCtx) {

            "!!true" should parseAs {
                unaryExpr(UnaryOp.BOOLEAN_NOT) {
                    unaryExpr(UnaryOp.BOOLEAN_NOT) {
                        boolean(true)
                    }
                }
            }

            "~~1" should parseAs {
                unaryExpr(BITWISE_INVERSE) {
                    unaryExpr(BITWISE_INVERSE) {
                        number()
                    }
                }
            }

            "-~1" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    unaryExpr(BITWISE_INVERSE) {
                        number()
                    }
                }
            }

            "-+-+1" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    unaryExpr(UNARY_PLUS) {
                        unaryExpr(UNARY_MINUS) {
                            unaryExpr(UNARY_PLUS) {
                                number()
                            }
                        }
                    }
                }
            }
        }
    }
})
