package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.READ
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.WRITE
import net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD
import net.sourceforge.pmd.lang.java.ast.IncrementOp.*
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

        // the following cases test ambiguity between cast of unary, and eg parenthesized additive expr

        // see https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-UnaryExpressionNotPlusMinus
        // comments about ambiguity are below grammar

        inContext(ExpressionParsingCtx) {
            "(p)+q" should parseAs {
                additiveExpr(ADD) {
                    parenthesized {
                        variableAccess("p", READ)
                    }
                    variableAccess("q", READ)
                }
            }


            "(p)~q" should parseAs {
                castExpr {
                    classType("p")

                    unaryExpr(BITWISE_INVERSE) {
                        variableAccess("q", READ)
                    }
                }
            }

            "(p)!q" should parseAs {
                castExpr {
                    classType("p")

                    unaryExpr(BOOLEAN_NOT) {
                        variableAccess("q", READ)
                    }
                }
            }

            "(p)++" should parseAs {
                postfixMutation(INCREMENT) {
                    parenthesized {
                        variableAccess("p", WRITE)
                    }
                }
            }

            "(p)++q" shouldNot parse()
            "(p)--q" shouldNot parse()

            "i+++i" should parseAs {
                additiveExpr(ADD) {
                    postfixMutation(INCREMENT) {
                        variableAccess("i", WRITE)
                    }
                    variableAccess("i", READ)
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
                                    variableAccess("q", READ)
                                }
                            }
                        }

                        "($type)-q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                unaryExpr(UNARY_MINUS) {
                                    variableAccess("q", READ)
                                }
                            }
                        }

                        "($type)++q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                prefixMutation(INCREMENT) {
                                    variableAccess("q", WRITE)
                                }
                            }
                        }

                        "($type)--q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                prefixMutation(DECREMENT) {
                                    variableAccess("q", WRITE)
                                }
                            }
                        }

                        "($type)++" shouldNot parse()
                        "($type)--" shouldNot parse()
                    }
        }
    }


    parserTest("Unary expression is right-associative") {

        inContext(ExpressionParsingCtx) {

            "!!true" should parseAs {
                unaryExpr(BOOLEAN_NOT) {
                    unaryExpr(BOOLEAN_NOT) {
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
