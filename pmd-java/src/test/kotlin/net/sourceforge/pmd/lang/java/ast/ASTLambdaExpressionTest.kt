/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast

import net.sourceforge.pmd.lang.ast.test.shouldBe
import net.sourceforge.pmd.lang.java.ast.JavaVersion.Companion.Latest
import net.sourceforge.pmd.lang.java.ast.JavaVersion.J1_8
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind.*


class ASTLambdaExpressionTest : ParserTestSpec({

    parserTest("Simple lambda expressions", javaVersions = J1_8..Latest) {

        inContext(ExpressionParsingCtx) {


            "a -> foo()" should parseAs {
                exprLambda {


                    it::getParameters shouldBe lambdaFormals {
                        simpleLambdaParam("a") {
                            it::isTypeInferred shouldBe true
                            it::isLambdaParameter shouldBe true
                        }

                    }


                    methodCall("foo")
                }
            }

            "(a,b) -> foo()" should parseAs {
                exprLambda {


                    it::getParameters shouldBe lambdaFormals {
                        simpleLambdaParam("a") {
                            it::isTypeInferred shouldBe true
                            it::isLambdaParameter shouldBe true
                        }

                        simpleLambdaParam("b") {
                            it::isTypeInferred shouldBe true
                            it::isLambdaParameter shouldBe true
                        }
                    }



                    methodCall("foo")
                }
            }

            "(a,b) -> { foo(); } " should parseAs {
                blockLambda {

                    it::getParameters shouldBe lambdaFormals {
                        simpleLambdaParam("a")
                        simpleLambdaParam("b")
                    }


                    block()
                }
            }

            "(final int a, @F List<String> b) -> foo()" should parseAs {
                exprLambda {

                    it::getParameters shouldBe lambdaFormals {
                        lambdaParam {
                            it::getModifiers shouldBe modifiers {
                                it::getExplicitModifiers shouldBe setOf(JModifier.FINAL)
                            }

                            it::getTypeNode shouldBe primitiveType(INT)

                            variableId("a") {
                                it::isFinal shouldBe true
                                it::isLambdaParameter shouldBe true
                                it::isTypeInferred shouldBe false
                            }
                        }
                        lambdaParam {
                            it::getModifiers shouldBe modifiers {
                                it::getExplicitModifiers shouldBe setOf()
                                annotation("F")
                            }

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
        }
    }

    parserTest("Mixed array notation/varargs", javaVersions = J1_8..Latest) {

        inContext(ExpressionParsingCtx) {
            "(final int a[]@B[], @F List<String>@a [] b @A []) -> foo()" should parseAs {

                exprLambda {

                    it::getParameters shouldBe child {
                        lambdaParam {
                            it::getModifiers shouldBe modifiers {
                                it::getExplicitModifiers shouldBe setOf(JModifier.FINAL)
                            }

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
                            it::getModifiers shouldBe modifiers {
                                it::getExplicitModifiers shouldBe setOf()
                                annotation("F")
                            }

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
                            it::getModifiers shouldBe modifiers {}
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
                            it::getModifiers shouldBe modifiers {}

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
