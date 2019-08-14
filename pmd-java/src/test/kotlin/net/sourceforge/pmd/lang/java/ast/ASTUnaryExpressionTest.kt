package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.READ
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.WRITE
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType
import net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx
import net.sourceforge.pmd.lang.java.ast.UnaryOp.PostfixOp.POST_DECREMENT
import net.sourceforge.pmd.lang.java.ast.UnaryOp.PostfixOp.POST_INCREMENT
import net.sourceforge.pmd.lang.java.ast.UnaryOp.PrefixOp.*

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
                prefixExpr(UNARY_MINUS) {
                    number()
                }
            }

            "-2" should parseAs {
                prefixExpr(UNARY_MINUS) {
                    number()
                }
            }

            "-2" should parseAs {
                prefixExpr(UNARY_MINUS) {
                    number()
                }
            }

            "-2" should parseAs {
                prefixExpr(UNARY_MINUS) {
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
                    prefixExpr(UNARY_MINUS) {
                        number()
                    }
                }
            }

            "2 +-2" should parseAs {
                additiveExpr(ADD) {
                    number()
                    prefixExpr(UNARY_MINUS) {
                        number()
                    }
                }
            }

            "2 + +2" should parseAs {
                additiveExpr(ADD) {
                    number()
                    prefixExpr(UNARY_PLUS) {
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

                    prefixExpr(COMPLEMENT) {
                        variableAccess("q", READ)
                    }
                }
            }

            "(p)!q" should parseAs {
                castExpr {
                    classType("p")

                    prefixExpr(NEGATION) {
                        variableAccess("q", READ)
                    }
                }
            }

            "(p)++" should parseAs {
                postfixExpr(POST_INCREMENT) {
                    parenthesized {
                        variableAccess("p", WRITE)
                    }
                }
            }

            "(p)++q" shouldNot parse()
            "(p)--q" shouldNot parse()

            "i+++i" should parseAs {
                additiveExpr(ADD) {
                    postfixExpr(POST_INCREMENT) {
                        variableAccess("i", WRITE)
                    }
                    variableAccess("i", READ)
                }
            }

            "i---i" should parseAs {
                additiveExpr(ADD) {
                    postfixExpr(POST_DECREMENT) {
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

                                prefixExpr(UNARY_PLUS) {
                                    variableAccess("q", READ)
                                }
                            }
                        }

                        "($type)-q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                prefixExpr(UNARY_MINUS) {
                                    variableAccess("q", READ)
                                }
                            }
                        }

                        "($type)++q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                prefixExpr(PRE_INCREMENT) {
                                    variableAccess("q", WRITE)
                                }
                            }
                        }

                        "($type)--q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                prefixExpr(PRE_DECREMENT) {
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
                prefixExpr(NEGATION) {
                    prefixExpr(NEGATION) {
                        boolean(true)
                    }
                }
            }

            "~~1" should parseAs {
                prefixExpr(COMPLEMENT) {
                    prefixExpr(COMPLEMENT) {
                        number()
                    }
                }
            }

            "-~1" should parseAs {
                prefixExpr(UNARY_MINUS) {
                    prefixExpr(COMPLEMENT) {
                        number()
                    }
                }
            }

            "-+-+1" should parseAs {
                prefixExpr(UNARY_MINUS) {
                    prefixExpr(UNARY_PLUS) {
                        prefixExpr(UNARY_MINUS) {
                            prefixExpr(UNARY_PLUS) {
                                number()
                            }
                        }
                    }
                }
            }
        }
    }
})
