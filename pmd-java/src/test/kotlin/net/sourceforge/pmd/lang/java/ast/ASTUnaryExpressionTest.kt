package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.READ
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType.WRITE
import net.sourceforge.pmd.lang.java.ast.BinaryOp.ADD
import net.sourceforge.pmd.lang.java.ast.BinaryOp.SUB
import net.sourceforge.pmd.lang.java.ast.UnaryOp.*
import net.sourceforge.pmd.lang.java.types.JPrimitiveType
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.BOOLEAN
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT

class ASTUnaryExpressionTest : ParserTestSpec({

    parserTest("Simple unary expressions") {

        inContext(ExpressionParsingCtx) {
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
                infixExpr(ADD) {
                    number()
                    unaryExpr(UNARY_MINUS) {
                        number()
                    }
                }
            }

            "2 +-2" should parseAs {
                infixExpr(ADD) {
                    number()
                    unaryExpr(UNARY_MINUS) {
                        number()
                    }
                }
            }

            "2 + +2" should parseAs {
                infixExpr(ADD) {
                    number()
                    unaryExpr(UNARY_PLUS) {
                        number()
                    }
                }
            }

            "+(int)-a" should parseAs {
                unaryExpr(UNARY_PLUS) {
                    castExpr {
                        primitiveType(INT)
                        unaryExpr(UNARY_MINUS) {
                            variableAccess("a")
                        }
                    }
                }
            }
            "+-(int)a" should parseAs {
                unaryExpr(UNARY_PLUS) {
                    unaryExpr(UNARY_MINUS) {
                        castExpr {
                            primitiveType(INT)
                            variableAccess("a")
                        }
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
                infixExpr(ADD) {
                    parenthesized {
                        variableAccess("p", READ)
                    }
                    variableAccess("q", READ)
                }
            }


            "(p)~q" should parseAs {
                castExpr {
                    classType("p")

                    unaryExpr(COMPLEMENT) {
                        variableAccess("q", READ)
                    }
                }
            }

            "(p)!q" should parseAs {
                castExpr {
                    classType("p")

                    unaryExpr(NEGATION) {
                        variableAccess("q", READ)
                    }
                }
            }

            "(p)++" should parseAs {
                unaryExpr(POST_INCREMENT) {
                    parenthesized {
                        variableAccess("p", WRITE)
                    }
                }
            }

            "(p)++q" shouldNot parse()
            "(p)--q" shouldNot parse()

            "i+++i" should parseAs {
                infixExpr(ADD) {
                    unaryExpr(POST_INCREMENT) {
                        variableAccess("i", WRITE)
                    }
                    variableAccess("i", READ)
                }
            }

            "i---i" should parseAs {
                infixExpr(SUB) {
                    unaryExpr(POST_DECREMENT) {
                        variableAccess("i", WRITE)
                    }
                    variableAccess("i", READ)
                }
            }

            // "++i++" parses, but doesn't compile, so don't test it
            // same for eg "p+++++q" (which doesn't parse)

            (JPrimitiveType.PrimitiveTypeKind.values().toList() - BOOLEAN)
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

                                unaryExpr(PRE_INCREMENT) {
                                    variableAccess("q", WRITE)
                                }
                            }
                        }

                        "($type)--q" should parseAs {
                            castExpr {
                                primitiveType(type)

                                unaryExpr(PRE_DECREMENT) {
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
                unaryExpr(NEGATION) {
                    unaryExpr(NEGATION) {
                        boolean(true)
                    }
                }
            }

            "~~1" should parseAs {
                unaryExpr(COMPLEMENT) {
                    unaryExpr(COMPLEMENT) {
                        number()
                    }
                }
            }

            "-~1" should parseAs {
                unaryExpr(UNARY_MINUS) {
                    unaryExpr(COMPLEMENT) {
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
