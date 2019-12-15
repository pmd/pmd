/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType.PrimitiveType.INT
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.ExpressionParsingCtx
import net.sourceforge.pmd.lang.java.ast.ParserTestCtx.Companion.StatementParsingCtx


class ASTLambdaExpressionTest : ParserTestSpec({

    parserTest("Simple lambda expressions", javaVersions = J1_8..Latest) {

        "a -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    variableId("a") {
                        it::isTypeInferred shouldBe true
                        it::isLambdaParameter shouldBe true
                    }
                }
            }


            child<ASTMethodCall>(ignoreChildren = true) {}
        }

        "(a,b) -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    variableId("a") {
                        it::isTypeInferred shouldBe true
                        it::isLambdaParameter shouldBe true
                    }

                }
                child<ASTLambdaParameter> {
                    variableId("b") {
                        it::isTypeInferred shouldBe true
                        it::isLambdaParameter shouldBe true
                    }
                }
            }


            child<ASTMethodCall>(ignoreChildren = true) {}
        }

        "(a,b) -> { foo(); } " should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe false
            it::isBlockBody shouldBe true

            it::getParameters shouldBe child {
                child<ASTLambdaParameter> {
                    variableId("a")

                }
                child<ASTLambdaParameter> {
                    variableId("b")
                }
            }


            child<ASTBlock>(ignoreChildren = true) {}
        }

        "(final int a, @F List<String> b) -> foo()" should matchExpr<ASTLambdaExpression> {
            it::isExpressionBody shouldBe true
            it::isBlockBody shouldBe false

            it::getParameters shouldBe child {
                lambdaParam {
                    it::isFinal shouldBe true

                    it::getTypeNode shouldBe primitiveType(INT)

                    variableId("a") {
                        it::isFinal shouldBe true
                        it::isLambdaParameter shouldBe true
                        it::isTypeInferred shouldBe false
                    }
                }
                lambdaParam {
                    annotation()
                    it::getTypeNode shouldBe classType("List")
                    variableId("b") {
                        it::isFinal shouldBe false
                        it::isLambdaParameter shouldBe true
                        it::isTypeInferred shouldBe false
                    }
                }
            }


            methodCall("foo")
        }

    }

    parserTest("Mixed array notation/varargs", javaVersions = J1_8..Latest) {

        inContext(ExpressionParsingCtx) {
            "(final int a[]@B[], @F List<String>@a [] b @A []) -> foo()" should parseAs {

                exprLambda {

                    it::getParameters shouldBe child {
                        lambdaParam {
                            it::isFinal shouldBe true

                            it::getTypeNode shouldBe primitiveType(INT)

                            variableId("a") {
                                it::isFinal shouldBe true
                                it::isLambdaParameter shouldBe true
                                it::isTypeInferred shouldBe false

                                it::getExtraDimensions shouldBe child {
                                    arrayDim {}
                                    arrayDim {
                                        annotation("B")
                                    }
                                }
                            }
                        }

                        lambdaParam {
                            annotation()
                            it::getTypeNode shouldBe arrayType {
                                classType("List")
                                it::getDimensions shouldBe child {
                                    arrayDim {
                                        annotation("a")
                                    }
                                }
                            }
                            variableId("b") {
                                it::isFinal shouldBe false
                                it::isLambdaParameter shouldBe true
                                it::isTypeInferred shouldBe false


                                it::getExtraDimensions shouldBe child {
                                    arrayDim {
                                        annotation("A")
                                    }
                                }
                            }
                        }
                    }


                    methodCall("foo")
                }
            }
        }
    }

    parserTest("Varargs parameter", javaVersions = J1_8..Latest) {

        inContext(ExpressionParsingCtx) {
            "(String ... b) -> {}" should parseAs {

                blockLambda {

                    it::getParameters shouldBe child {
                        lambdaParam {
                            it::getTypeNode shouldBe arrayType {
                                classType("String")
                                it::getDimensions shouldBe child {
                                    varargsArrayDim { }
                                }
                            }

                            variableId("b") {
                                it::getExtraDimensions shouldBe null
                            }
                        }
                    }


                    block { }
                }
            }

            "(String @A [] @B ... b) -> {}" should parseAs {

                blockLambda {

                    it::getParameters shouldBe child {
                        lambdaParam {
                            it::getTypeNode shouldBe arrayType {
                                classType("String")
                                it::getDimensions shouldBe child {
                                    arrayDim {
                                        annotation("A")
                                    }
                                    varargsArrayDim {
                                        annotation("B")
                                    }
                                }
                            }

                            variableId("b") {
                                it::getExtraDimensions shouldBe null
                            }
                        }
                    }


                    block { }
                }
            }
        }
    }

    parserTest("Negative lambda contexts") {
        inContext(StatementParsingCtx) {
            "a -> {}" shouldNot parse()
        }
    }

    parserTest("Positive lambda contexts") {

        inContext(ExpressionParsingCtx) {
            "(a -> {})" should parse()
        }
    }

})
