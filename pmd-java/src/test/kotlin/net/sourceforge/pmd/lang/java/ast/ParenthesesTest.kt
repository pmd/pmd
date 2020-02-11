/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import io.kotlintest.shouldBe
import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.INT

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
class ParenthesesTest : ParserTestSpec({


    parserTest("Test parens") {

        inContext(StatementParsingCtx) {

            "int a = 3;" should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    primitiveType(INT)
                    variableDeclarator("a") {
                        it::getInitializer shouldBe int(3) {
                            it::getParenthesisDepth shouldBe 0
                            it::isParenthesized shouldBe false
                        }
                    }
                }
            }

            "int a = (3);" should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    primitiveType(INT)
                    variableDeclarator("a") {
                        it::getInitializer shouldBe int(3) {
                            it::getParenthesisDepth shouldBe 1
                            it::isParenthesized shouldBe true
                        }
                    }
                }
            }

            "int a = ((3));" should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    primitiveType(INT)
                    variableDeclarator("a") {
                        it::getInitializer shouldBe int(3) {
                            it::getParenthesisDepth shouldBe 2
                            it::isParenthesized shouldBe true

                            it.tokenList().map { it.image } shouldBe listOf("(", "(", "3", ")", ")")
                        }
                    }
                }
            }

            "int a = ((a)).f;" should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    primitiveType(INT)
                    variableDeclarator("a") {
                        it::getInitializer shouldBe fieldAccess("f") {
                            it::getParenthesisDepth shouldBe 0
                            it::isParenthesized shouldBe false

                            it::getQualifier shouldBe variableAccess("a") {
                                it::getParenthesisDepth shouldBe 2
                                it::isParenthesized shouldBe true

                                it.tokenList().map { it.image } shouldBe listOf("(", "(", "a", ")", ")")
                            }
                        }
                    }
                }
            }

            "int a = ((a).f);" should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    primitiveType(INT)
                    variableDeclarator("a") {
                        it::getInitializer shouldBe fieldAccess("f") {
                            it::getParenthesisDepth shouldBe 1
                            it::isParenthesized shouldBe true

                            it.tokenList().map { it.image } shouldBe listOf("(", "(", "a", ")", ".", "f", ")")

                            it::getQualifier shouldBe variableAccess("a") {
                                it::getParenthesisDepth shouldBe 1
                                it::isParenthesized shouldBe true

                                it.tokenList().map { it.image } shouldBe listOf("(", "a", ")")
                            }
                        }
                    }
                }
            }

            // the left parens shouldn't be flattened by AbstractLrBinaryExpr
            "int a = ((1 + 2) + f);" should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    primitiveType(INT)
                    variableDeclarator("a") {
                        it::getInitializer shouldBe infixExpr(BinaryOp.ADD) {
                            it::getParenthesisDepth shouldBe 1
                            it::isParenthesized shouldBe true

                            it.tokenList().map { it.image } shouldBe
                                    listOf("(", "(", "1", "+", "2", ")", "+", "f", ")")

                            infixExpr(BinaryOp.ADD) {
                                it::getParenthesisDepth shouldBe 1
                                it::isParenthesized shouldBe true


                                it.tokenList().map { it.image } shouldBe
                                        listOf("(", "1", "+", "2", ")")

                                int(1)
                                int(2)
                            }

                            variableAccess("f") {
                                it::isParenthesized shouldBe false
                                it::getParenthesisDepth shouldBe 0
                            }
                        }
                    }
                }
            }

            "int a = (1 + (2 + f));" should parseAs {
                localVarDecl {
                    localVarModifiers { }
                    primitiveType(INT)
                    variableDeclarator("a") {
                        it::getInitializer shouldBe infixExpr(BinaryOp.ADD) {
                            it::getParenthesisDepth shouldBe 1
                            it::isParenthesized shouldBe true

                            int(1)

                            infixExpr(BinaryOp.ADD) {
                                it::getParenthesisDepth shouldBe 1
                                it::isParenthesized shouldBe true

                                int(2)
                                variableAccess("f")
                            }
                        }
                    }
                }
            }
        }
    }


    parserTest("Test expressions with complicated LHS") {

        // the qualifier here is not an ASTPrimaryExpression,
        // since parentheses are flattened.
        // The API should reflect that

        inContext(ExpressionParsingCtx) {

            "((String) obj).length()" should parseAs {
                methodCall("length") {

                    it::getQualifier shouldBe parenthesized {
                        castExpr {
                            classType("String")
                            variableAccess("obj")
                        }
                    }

                    it::getArguments shouldBe child {}
                }
            }

            "((int[]) obj)[(int) i]" should parseAs {
                arrayAccess {
                    it::getQualifier shouldBe parenthesized {
                        castExpr {
                            arrayType({ primitiveType(INT) }) {
                                arrayDim()
                            }
                            variableAccess("obj")
                        }
                    }

                    it::getIndexExpression shouldBe castExpr {
                        primitiveType(INT)
                        variableAccess("i")
                    }
                }
            }

            "(switch (obj) { case a -> 1; default -> 2; }).length()" should parseAs {
                methodCall("length") {

                    it::getQualifier shouldBe parenthesized {
                        switchExpr()
                    }

                    it::getArguments shouldBe child {}
                }
            }
        }
    }


})
